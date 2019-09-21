package com.framework.orm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

	private static final String propertiesPath = "src/main/resources/application.properties";
	private static String driver;
	private static String url;
	private static String username;
	private static String password;

	public static void init() {
		Properties properties = new Properties();

		try (InputStream in = new FileInputStream(propertiesPath)) {
			properties.load(in);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Properties file not found: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		driver = properties.getProperty("db.driver");

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		url = properties.getProperty("db.url");
		username = properties.getProperty("db.username");
		password = properties.getProperty("db.password");
	}

	public static Connection getConnection() {

		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new RuntimeException("Database connection error: " + e.getMessage() + "\n");
		}

	}

}
