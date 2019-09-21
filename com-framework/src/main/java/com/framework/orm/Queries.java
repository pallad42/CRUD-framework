package com.framework.orm;

import java.lang.reflect.Field;
import java.util.List;

public class Queries {

	public static String makeInsertQuery(Class<?> clazz) {
		
		String tableName = EntityManager.getEntityTableName(clazz);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + tableName);

		List<TableColumn> columns = EntityManager.entities.get(clazz);

		sql.append("(");
		for (int i = 0; i < columns.size(); ++i) {

			TableColumn column = columns.get(i);

			sql.append(column.getName());

			if (i < columns.size() - 1) {
				sql.append(", ");
			}

		}
		sql.append(")");

		sql.append(" VALUES ");

		sql.append("(");

		Field[] fields = clazz.getDeclaredFields();
		int length = fields.length;
		for (int i = 0; i < length; ++i) {

			sql.append("?");

			if (i < columns.size() - 1) {
				sql.append(", ");
			}

		}
		sql.append(")");

		return sql.toString();
		
	}
	
	public static String makeDeleteQuery(Class<?> clazz) {
		
		String tableName = EntityManager.getEntityTableName(clazz);

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + tableName + " WHERE ");

		List<TableColumn> columns = EntityManager.entities.get(clazz);

		for (int i = 0; i < columns.size(); ++i) {

			TableColumn column = columns.get(i);

			sql.append(column.getName() + "=" + "?");

			if (i < columns.size() - 1) {
				sql.append(" AND ");
			}

		}

		return sql.toString();
		
	}
	
}
