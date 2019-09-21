package com.framework.orm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.framework.web.annotations.Entity;
import com.framework.web.annotations.TableName;

public class EntityManager {

	public static Map<Class<?>, List<TableColumn>> entities = new HashMap<Class<?>, List<TableColumn>>();

	
	public static boolean isEntity(Class<?> clazz) {

		if (clazz.isAnnotationPresent(Entity.class)) {
			return true;
		}
		return false;

	}

	public static String getEntityTableName(Class<?> clazz) {

		if (isEntity(clazz)) {

			if (clazz.isAnnotationPresent(TableName.class)) {
				TableName annotation = (TableName) clazz.getAnnotation(TableName.class);
				String value = annotation.value();
				return value;
			}

			String name = clazz.getSimpleName().toLowerCase();
			return name;

		}

		throw new RuntimeException(clazz + " is not Entity");

	}

	public static String getFieldName(Class<?> clazz, Predicate<TableColumn> predicate) {

		if (entities.containsKey(clazz)) {

			List<TableColumn> columns = entities.get(clazz);

			for (TableColumn column : columns) {

				if (predicate.test(column)) {
					return column.getName();
				}

			}
		}

		return null;

	}

}
