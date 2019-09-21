package com.framework.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.framework.web.annotations.GeneratedValue;
import com.framework.web.annotations.Id;

public class TableSeeder {

	public static void generate(Class<?> clazz, int amount) {

		try (Connection con = Database.getConnection()) {

			if (con != null) {

				String sql = Queries.makeInsertQuery(clazz);

				for (int i = 0; i < amount; ++i) {

					Field[] fields = clazz.getDeclaredFields();
					Object[] args = new Object[fields.length];

					for (int j = 0; j < fields.length; ++j) {

						Field field = fields[j];

						if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class)) {
							continue;
						}

						Class<?> type = field.getType();

						if (type == Integer.TYPE || type == Integer.class) {
							args[j] = i + 1;
						} else if (type == Double.TYPE || type == Double.class) {
							args[j] = i + 1;
						} else if (type == Float.TYPE || type == Float.class) {
							args[j] = i + 1;
						} else if (type == Long.TYPE || type == Long.class) {
							args[j] = i + 1;
						} else if (type == Boolean.TYPE || type == Boolean.class) {
							args[j] = true;
						} else {
							args[j] = field.getName() + (i + 1);
						}

					}

					try (PreparedStatement ps = con.prepareStatement(sql)) {

						for (int j = 0; j < args.length; ++j) {
							Object arg = args[j];
							ps.setObject(j + 1, arg);
						}

						ps.executeUpdate();
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
