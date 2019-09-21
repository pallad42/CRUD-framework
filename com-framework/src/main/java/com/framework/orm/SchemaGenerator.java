package com.framework.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.framework.web.annotations.GeneratedValue;
import com.framework.web.annotations.Id;
import com.framework.web.annotations.Seeder;

public class SchemaGenerator {

	private static String chooseType(Class<?> type) {
		if (type == Integer.TYPE || type == Integer.class) {
			return "INT";
		}

		if (type == Double.TYPE || type == Double.class) {
			return "DOUBLE";
		}

		if (type == Float.TYPE || type == Float.class) {
			return "FLOAT";
		}

		if (type == Long.TYPE || type == Long.class) {
			return "BIGINT";
		}

		if (type == Boolean.TYPE || type == Boolean.class) {
			return "BOOLEAN";
		}

		if (type == String.class) {
			return "TEXT";
		}

		return null;
	}

	public static void makeTable(Class<?> clazz) {

		List<TableColumn> columns = new ArrayList<TableColumn>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			String columnName = field.getName();
			Class<?> fieldType = field.getType();
			String columnType = chooseType(fieldType);

			TableColumn column = new TableColumn(columnName, columnType);

			// @Id
			if (field.isAnnotationPresent(Id.class)) {
				column.setPrimaryKey(true);
			}
			// @GeneratedValue
			if (field.isAnnotationPresent(GeneratedValue.class)) {
				column.setAutoIncrement(true);
			}

			columns.add(column);
		}

		EntityManager.entities.put(clazz, columns);

		String tableName = EntityManager.getEntityTableName(clazz);

		StringBuilder str = new StringBuilder();
		str.append("CREATE TABLE IF NOT EXISTS " + tableName + " (");

		for (int i = 0; i < columns.size(); ++i) {

			TableColumn column = columns.get(i);

			String name = column.getName() + " ";
			String type = column.getType() + " ";
			String autoIncrement = column.isAutoIncrement() ? "AUTO_INCREMENT" + " " : "";
			String primaryKey = column.isPrimaryKey() ? "PRIMARY KEY" + " " : "";

			str.append(name + type + autoIncrement + primaryKey);

			if (i < columns.size() - 1) {
				str.append(",");
			}

		}

		str.append(")");

		String dropTable = "DROP TABLE IF EXISTS " + tableName + ";";
		String createTable = str.toString();

		try (Connection con = Database.getConnection()) {
			if (con != null) {
				try (Statement stmt = con.createStatement()) {
					stmt.executeUpdate(dropTable);
					stmt.executeUpdate(createTable);
					
					if(clazz.isAnnotationPresent(Seeder.class)) {
						
						Seeder annotation = (Seeder)clazz.getAnnotation(Seeder.class);
						int value = annotation.value();
						
						TableSeeder.generate(clazz, value);		
						
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
