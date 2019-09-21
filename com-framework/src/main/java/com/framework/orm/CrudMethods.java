package com.framework.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.framework.web.annotations.GeneratedValue;
import com.framework.web.annotations.Id;

public class CrudMethods {

	public static <T> long count(Class<T> clazz) {

		String tableName = EntityManager.getEntityTableName(clazz);

		String sql = "SELECT COUNT(*) FROM " + tableName;

		try (Connection con = Database.getConnection()) {

			if (con != null) {

				try (Statement stmt = con.createStatement()) {

					try (ResultSet rs = stmt.executeQuery(sql)) {

						rs.next();

						return rs.getInt(1);

					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;

	}

	public static <T> void delete(Class<T> clazz, T entity) {

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

		Field[] fields = clazz.getDeclaredFields();
		int length = fields.length;

		Object[] args = new Object[length];

		for (int i = 0; i < length; ++i) {

			Field field = fields[i];

			field.setAccessible(true);
			try {
				args[i] = field.get(entity);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(false);

		}

		int count = CrudQueries.returnUpdateCount(sql.toString(), args);

		if (count > 1) {
			throw new RuntimeException("There are two objects in the database with the same id");
		}

	}

	public static <T> void deleteAll(Class<T> clazz) {

		String tableName = EntityManager.getEntityTableName(clazz);

		String sql = "TRUNCATE TABLE " + tableName;

		CrudQueries.returnUpdateCount(sql);

	}

	public static <T> void deleteAll(Class<T> clazz, Collection<T> entities) {

		for (T entity : entities) {
			delete(clazz, entity);
		}

	}

	public static <T, ID> void deleteById(Class<T> clazz, ID id) {

		String tableName = EntityManager.getEntityTableName(clazz);
		String field_Id = EntityManager.getFieldName(clazz, (TableColumn tc) -> tc.isPrimaryKey());

		String sql = "DELETE FROM " + tableName + " WHERE " + field_Id + "=?";

		int count = CrudQueries.returnUpdateCount(sql, id);

		if (count > 1) {
			throw new RuntimeException("There are two objects in the database with the same id");
		}
	}

	public static <T, ID> boolean existsById(Class<T> clazz, ID id) {

		String tableName = EntityManager.getEntityTableName(clazz);
		String field_Id = EntityManager.getFieldName(clazz, (TableColumn tc) -> tc.isPrimaryKey());

		String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + field_Id + "=?";

		try (Connection con = Database.getConnection()) {

			if (con != null) {

				try (PreparedStatement ps = con.prepareStatement(sql)) {

					ps.setObject(1, id);

					try (ResultSet rs = ps.executeQuery()) {

						rs.next();

						int amount = rs.getInt(1);

						if (amount == 1) {
							return true;
						} else if (amount > 1) {
							throw new RuntimeException("There are two objects in the database with the same id");
						}

					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;

	}

	public static <T> Collection<T> findAll(Class<T> clazz) {

		String tableName = EntityManager.getEntityTableName(clazz);

		String sql = "SELECT * FROM " + tableName;

		return CrudQueries.returnRowResultSet(clazz, sql);

	}

	public static <T, ID> Collection<T> findAllById(Class<T> clazz, Collection<ID> ids) {

		Collection<T> result = new ArrayList<T>();

		for (ID id : ids) {

			T entity = findById(clazz, id);
			result.add(entity);

		}

		return result;

	}

	public static <T, ID> T findById(Class<T> clazz, ID id) {

		String tableName = EntityManager.getEntityTableName(clazz);
		String field_Id = EntityManager.getFieldName(clazz, (TableColumn tc) -> tc.isPrimaryKey());

		String sql = "SELECT * FROM " + tableName + " WHERE " + field_Id + "=?";

		List<T> result = CrudQueries.returnRowResultSet(clazz, sql, id);

		if (result.isEmpty()) {
			return null;
		} else if (result.size() > 1) {
			throw new RuntimeException("There are two objects in the database with the same id");
		}

		return result.get(0);

	}

	public static <T> T save(Class<T> clazz, T entity) {

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

		Object[] args = new Object[length];

		for (int i = 0; i < length; ++i) {

			Field field = fields[i];

			if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class)) {
				continue;
			}

			field.setAccessible(true);
			try {
				args[i] = field.get(entity);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			field.setAccessible(false);

		}

		int result = CrudQueries.returnUpdateCount(sql.toString(), args);

		if (result > 0) {
			return entity;
		}

		return null;

	}

	public static <T> Collection<T> saveAll(Class<T> clazz, Collection<T> entities) {

		for (T entity : entities) {
			save(clazz, entity);
		}

		return entities;

	}

}
