package com.galenassignment.methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public static Connection con = null;

	static {
		String dbUrl = "jdbc:mysql://127.0.0.1:3306/galen";
		String userName = "root";
		String password = "priyanka123";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(Constants.dbUrl, Constants.userName, Constants.password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return con;
	}
}
