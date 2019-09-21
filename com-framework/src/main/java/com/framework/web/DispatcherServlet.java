package com.framework.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.orm.SchemaGenerator;
import com.framework.web.annotations.Autowired;
import com.framework.web.annotations.Component;
import com.framework.web.annotations.Entity;
import com.framework.web.annotations.Qualifier;
import com.framework.web.annotations.ResponseBody;
import com.framework.web.enums.ResponseContentType;
import com.framework.web.enums.ResponseStatus;
import com.framework.web.utils.SearchAnnotation;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class DispatcherServlet {

	public static Set<Object> components = new HashSet<Object>();
	private static ObjectMapper mapper = new ObjectMapper();

	public static void init(String packageName) {

		try {
			scanAnnotations(packageName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		Mapping.init();

	}

	public static void passSocket(Socket socket) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		Request request = new Request(in);
		Response response = new Response();

		MappingObject obj = Mapping.resolveMapping(request, response);

		if (response.getStatus().equals(ResponseStatus.Code_200)) {

			try {
				Object controller = obj.getController();
				Method method = obj.getMethod(request.getMethod());

				if (method.isAnnotationPresent(ResponseBody.class) || SearchAnnotation.deepSearch(controller.getClass(), ResponseBody.class)) {

					Object[] args = Mapping.resolveMethodArguments(obj, request);

					Object methodResult = null;

					if (args.length > 0) {
						methodResult = method.invoke(controller, args);
					} else {
						methodResult = method.invoke(controller);
					}

					String json = mapper.writeValueAsString(methodResult);
					response.setContentType(ResponseContentType.JSON);
					response.send(ResponseStatus.Code_200, json);

				} else {

					if (method.getReturnType() != String.class) {
						throw new RuntimeException("View method '" + method + "' must return " + String.class + ", or add @ResponseBody annotation before method definition");
					}
					throw new RuntimeException("View mapping is not supporting");
				}
			} catch (Exception e) {
				response.send(ResponseStatus.Code_500);
				e.printStackTrace();
			}
		}

		response.write(out);

		in.close();
		out.close();
		socket.close();
	}

	private static void scanAnnotations(String packageName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {

		Set<Field> fields = new HashSet<Field>();

		try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(packageName).scan()) {
			// @Component
			for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(Component.class.getName())) {
				Class<?> clazz = Class.forName(routeClassInfo.getName());

				if (!clazz.isAnnotation() && !clazz.isInterface()) {

					Object obj = clazz.getDeclaredConstructor().newInstance();
					components.add(obj);

				}
			}
			// @Entity
			for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(Entity.class.getName())) {

				Class<?> clazz = Class.forName(routeClassInfo.getName());

				SchemaGenerator.makeTable(clazz);

			}
			// @Autowired
			for (ClassInfo routeClassInfo : scanResult.getClassesWithFieldAnnotation(Autowired.class.getName())) {
				Class<?> clazz = Class.forName(routeClassInfo.getName());

				Field[] classFields = clazz.getDeclaredFields();

				for (Field field : classFields) {

					if (field.isAnnotationPresent(Autowired.class)) {
						fields.add(field);
					}
				}
			}
		}

		findAutowired(fields);

	}

	private static void findAutowired(Set<Field> fields) throws ClassNotFoundException {

		for (Field field : fields) {

			Class<?> fieldOwnerClass = field.getDeclaringClass();
			Class<?> fieldClass = (Class<?>) field.getAnnotatedType().getType();

			Object destination = null;
			Object value = null;

			for (Object component : components) {

				Class<?> componentClass = component.getClass();

				if (componentClass.equals(fieldOwnerClass)) {
					destination = component;
				}

				if (field.isAnnotationPresent(Qualifier.class)) {

					Qualifier annotation = (Qualifier) field.getAnnotation(Qualifier.class);
					Class<?> qualifierValue = annotation.value();

					if (componentClass.equals(qualifierValue)) {
						value = component;

						if (destination != null) {
							break;
						}
					}

				} else if (fieldClass.isAssignableFrom(componentClass)) {

					if (value == null) {
						value = component;
					} else {
						throw new RuntimeException("More than one Component match to field '" + field + "', use @Qualifier annotation to resolve it");
					}

				}

			}

			if (destination == null) {
				throw new RuntimeException("Class: '" + fieldOwnerClass + "' is not a Component!");
			}

			if (value == null) {
				if (field.isAnnotationPresent(Qualifier.class)) {

					Qualifier annotation = (Qualifier) field.getAnnotation(Qualifier.class);
					Class<?> qualifierValue = annotation.value();
					throw new RuntimeException("Class: '" + qualifierValue + "' is not a Component!");

				} else {
					throw new RuntimeException("No Components matches to field '" + fieldClass + "' from '" + fieldOwnerClass + "'");
				}
			}

			field.setAccessible(true);
			try {
				field.set(destination, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(false);
		}

	}

}