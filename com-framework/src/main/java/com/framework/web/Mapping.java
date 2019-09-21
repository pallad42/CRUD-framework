package com.framework.web;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.framework.web.annotations.PathVariable;
import com.framework.web.annotations.RequestParam;
import com.framework.web.annotations.RequestBody;
import com.framework.web.annotations.RequestMapping;
import com.framework.web.enums.RequestMethod;
import com.framework.web.enums.ResponseStatus;
import com.framework.web.utils.Utils;
import com.google.common.base.Defaults;

public class Mapping {

	public static Set<MappingObject> mappingSet = new HashSet<MappingObject>();

	public static void init() {

		for (Object component : DispatcherServlet.components) {

			Class<?> componentClass = component.getClass();

			String[] classUriArray = { "" };

			if (componentClass.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping requestMapping = (RequestMapping) componentClass.getAnnotation(RequestMapping.class);
				String[] value = requestMapping.value();

				if (value.length > 0) {
					classUriArray = value;
				}
			}

			Method[] methods = componentClass.getDeclaredMethods();

			for (Method method : methods) {

				if (method.isAnnotationPresent(RequestMapping.class)) {

					RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
					String[] methodUriArray = requestMapping.value();
					RequestMethod[] methodRequestArray = requestMapping.method();

					for (String classUri : classUriArray) {

						for (String methodUri : methodUriArray) {

							for (RequestMethod requestMethod : methodRequestArray) {

								String fullUri = classUri + methodUri;

								boolean existUri = false;

								for (MappingObject obj : mappingSet) {

									if (obj.getUri().equals(fullUri)) {

										if (obj.containsMethod(requestMethod)) {
											throw new RuntimeException(method + " and " + obj.getMethod(requestMethod) + " contain the same URI - [" + obj + "]");
										}

										obj.getRequestMethods().put(requestMethod, method);

										existUri = true;
									}

								}

								if (!existUri) {
									MappingObject uri = new MappingObject(fullUri, component);
									uri.getRequestMethods().put(requestMethod, method);
									mappingSet.add(uri);
								}
							}
						}
					}
				}
			}
		}
	}

	public static MappingObject resolveMapping(Request request, Response response) {

		MappingObject obj = null;
		for (MappingObject mappingObject : mappingSet) {
			if (mappingObject.getUri().equals(request.getUri())) {
				obj = mappingObject;
				break;
			}
		}

		if (obj != null) {

			Method method = obj.getMethod(request.getMethod());

			if (method != null) {
				response.send(ResponseStatus.Code_200);
				return obj;
			} else {
				response.send(ResponseStatus.Code_501);
			}

		}

		return resolveRegex(request, response);
	}

	private static MappingObject resolveRegex(Request request, Response response) {

		MappingObject obj = null;

		Set<MappingObject> matchesUri = new HashSet<MappingObject>();

		for (MappingObject mappingObject : Mapping.mappingSet) {

			String regex = mappingObject.getUri().replaceAll("\\{\\w+\\}", "\\\\w+");

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(request.getUri());

			if (matcher.matches()) {
				matchesUri.add(mappingObject);
			}

		}
		for (MappingObject matchesObj : matchesUri) {

			if (matchesObj.containsMethod(request.getMethod())) {

				if (obj == null) {
					obj = matchesObj;
				} else {
					throw new RuntimeException("Ambiguous handler methods mapped for " + request.getUri() + ": " + matchesUri);
				}

			}

		}

		if (obj != null) {
			response.send(ResponseStatus.Code_200);
		} else {
			if (!matchesUri.isEmpty()) {
				response.send(ResponseStatus.Code_501);
			} else {
				response.send(ResponseStatus.Code_404);
			}
		}

		return obj;
	}

	public static Object[] resolveMethodArguments(MappingObject obj, Request request) {

		Method method = obj.getMethod(request.getMethod());

		Parameter[] parameters = method.getParameters();
		Object[] args = new Object[parameters.length];

		for (int i = 0; i < parameters.length; ++i) {

			Parameter param = parameters[i];

			if ((args[i] = paramRequestBody(param, request)) != null)
				continue;

			if ((args[i] = paramRequestParam(method, param, request)) != null)
				continue;

			if ((args[i] = paramPathVariable(param, request, obj)) != null)
				continue;

		}

		return args;

	}

	private static Object paramRequestBody(Parameter param, Request request) {

		if (param.isAnnotationPresent(RequestBody.class)) {

			RequestBody annotation = (RequestBody) param.getAnnotation(RequestBody.class);
			RequestMethod requestMethod = annotation.method();

			switch (requestMethod) {
			case GET:
				return Utils.createModel(param.getType(), request.getQuery());

			case POST:
				return Utils.createModel(param.getType(), request.getPayload());

			default:
				break;
			}

		}

		return null;

	}

	private static Object paramRequestParam(Method method, Parameter param, Request request) {

		if (param.isAnnotationPresent(RequestParam.class)) {

			RequestParam annotation = (RequestParam) param.getAnnotation(RequestParam.class);

			String value = annotation.value();
			boolean required = annotation.required();
			RequestMethod requestMethod = annotation.method();
			String defaultValue = annotation.defaultValue();

			Object result = null;

			switch (requestMethod) {
			case GET:
				result = Utils.parse(param.getType(), request.getQuery().get(value));
				break;
			case POST:
				result = Utils.parse(param.getType(), request.getPayload().get(value));
				break;
			default:
				break;
			}

			if (result == null) {
				if (required) {
					throw new RuntimeException("The parameter '" + value + "' is missing in the request '" + method
							+ "'. Switch [required=false] inside @RequestParam if you prefer a null value if the parameter is not present in the request.");
				} else {

					if (!defaultValue.isEmpty()) {
						return Utils.parse(param.getType(), defaultValue);
					}

					return Defaults.defaultValue(param.getType());
				}
			}

			return result;
		}

		return null;

	}

	private static Object paramPathVariable(Parameter param, Request request, MappingObject obj) {

		if (param.isAnnotationPresent(PathVariable.class)) {

			String[] requestUriParts = request.getUri().split("/");
			String[] uriObjectParts = obj.getUri().split("/");

			PathVariable annotation = (PathVariable) param.getAnnotation(PathVariable.class);
			String name = "{" + annotation.value() + "}";

			for (int j = 0; j < requestUriParts.length; ++j) {

				String uriPart = uriObjectParts[j];

				if (uriPart.equals(name)) {
					Class<?> type = param.getType();
					String value = requestUriParts[j];
					return Utils.parse(type, value);
				}

			}

			throw new RuntimeException("Missing URI template variable '" + annotation.value() + "' for method parameter of type " + param.getParameterizedType().getTypeName());
		}

		return null;

	}

}
