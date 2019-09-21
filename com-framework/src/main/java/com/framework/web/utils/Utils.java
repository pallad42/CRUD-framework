package com.framework.web.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

	public static Object parse(Class<?> type, String value) {
		
		if(value == null) {
			return null;
		}
		
		if(type == Integer.TYPE || type == Integer.class) {
			return Integer.parseInt(value);
		}
		
		if(type == Double.TYPE || type == Double.class) {
			return Double.parseDouble(value);
		}
		
		if(type == Float.TYPE || type == Float.class) {
			return Float.parseFloat(value);
		}
		
		if(type == Long.TYPE || type == Long.class) {
			return Long.parseLong(value);
		}

		if(type == Boolean.TYPE || type == Boolean.class) {
			return Boolean.parseBoolean(value);
		}
		
		if(type == String.class) {
			return value;
		}
		
		return null;
	}

	public static <T> T createModel(Class<T> clazz, Map<String, String> map) {
		
		try {
			Method[] methods = clazz.getDeclaredMethods();
			List<Method> setters = new ArrayList<Method>();

			for (Method method : methods) {
				if (methodIsSetter(method)) {
					setters.add(method);
				}
			}

			T obj = clazz.getConstructor().newInstance();

			for (Method method : setters) {
				// skip 'set' from setter method name
				StringBuilder stringBuilder = new StringBuilder(method.getName().substring(3));
				// change first letter to lower case
				stringBuilder.setCharAt(0, Character.toLowerCase(stringBuilder.charAt(0)));
				
				String mapKey = stringBuilder.toString();
				String mapValue = map.get(mapKey);

				if (map.containsKey(mapKey)) {
					// setters contains only one parameter
					Class<?> type = method.getParameterTypes()[0];

					Object arg = parse(type, mapValue);
					
					method.invoke(obj, arg);
				}
			}
			
			return obj;
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static boolean methodIsSetter(Method method) {
		
		if (!method.getName().startsWith("set")) {
			return false;
		}
		if (method.getReturnType() != void.class) {
			return false;
		}
		if (method.getParameterCount() != 1) {
			return false;
		}
		
		return true;
	}
	
}
