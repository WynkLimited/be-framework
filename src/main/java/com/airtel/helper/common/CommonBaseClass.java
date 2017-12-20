package com.airtel.helper.common;

import java.sql.Connection;
import java.sql.SQLException;

import org.testng.Reporter;
import org.testng.annotations.AfterMethod;

public class CommonBaseClass {

	public static Connection connection = null;

	@AfterMethod
	public void closeDBConnection() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			Reporter.log("Exception In closing DB Connection In CommonBaseClass");
			e.getStackTrace();
		}
	}

}
