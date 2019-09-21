package com.framework.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.web.utils.Utils;

public class CrudQueries {

	// SELECT
	public static <T> List<T> returnRowResultSet(Class<T> clazz, String sql, Object... args) {

		try (Connection con = Database.getConnection()) {

			if (con != null) {

				if (args.length > 0) {

					try (PreparedStatement ps = con.prepareStatement(sql)) {

						for (int i = 0; i < args.length; ++i) {
							Object arg = args[i];
							ps.setObject(i + 1, arg);
						}

						try (ResultSet rs = ps.executeQuery()) {

							return resultList(clazz, rs);
						}
					}

				} else {

					try (Statement stmt = con.createStatement()) {

						try (ResultSet rs = stmt.executeQuery(sql)) {

							return resultList(clazz, rs);

						}
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static <T> List<T> resultList(Class<T> clazz, ResultSet rs) {

		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();

			List<T> list = new ArrayList<T>();

			while (rs.next()) {

				Map<String, String> map = new HashMap<String, String>();

				for (int i = 1; i <= count; ++i) {
					map.put(rsmd.getColumnName(i), rs.getObject(i).toString());
				}

				T obj = Utils.createModel(clazz, map);
				list.add(obj);

			}

			return list;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// INSERT, UPDATE, DELETE
	public static int returnUpdateCount(String sql, Object... args) {

		try (Connection con = Database.getConnection()) {

			if (con != null) {

				if (args.length > 0) {

					try (PreparedStatement ps = con.prepareStatement(sql)) {

						for (int i = 0; i < args.length; ++i) {
							Object arg = args[i];
							ps.setObject(i + 1, arg);
						}

						return ps.executeUpdate();
					}

				} else {

					try (Statement stmt = con.createStatement()) {

						return stmt.executeUpdate(sql);

					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;

	}

}
