package com.airtel.helper.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Reporter;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;

public class DbHelper {

	 static final String CONFIGURATION_FILE_PATH = "config/envConfig.properties";

	public static Connection createDbConnection(String environment, String sid) {
		return createDbConnection(environment, sid, "1521");
	}

	public static Connection createDbConnection(String environment, String sid, String port) {
		Connection con = null;
		String dbServerIp = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, environment + "_IP");
		String userName = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, environment + "_USER");
		String password = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, environment + "_PASSWORD");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String connectionString = "jdbc:oracle:thin:@" + dbServerIp + ":" + port + ":" + sid;
			con = DriverManager.getConnection(connectionString, userName, password);
		} catch (Exception e) {
			Reporter.log(e.getLocalizedMessage());
		}
		return con;
	}

	/**
	 * 
	 * @param connection
	 * @param query
	 * @return List<Map<String,String>> - This output will have data returned from
	 *         query. Each element of list corresponds to 1 row of DB, key of the
	 *         map will be column name whereas value will be value of that column in
	 *         that row
	 */
	public static List<Map<String, String>> fetchDatafromDb(Connection connection, String query) {
		ResultSet result = executeQuery(connection, query);
		return convertResultToMap(result);
	}

	private static ResultSet executeQuery(Connection connection, String query) {
		ResultSet result = null;
		try {
			if (connection != null) {
				Statement stmt = connection.createStatement();
				result = stmt.executeQuery(query);
			}
		} catch (SQLException e) {
			ReportHelper.logValidationFailure("Couldn't get db connection", "DB connection", e.getMessage(),
					"DB connection failure");
		}

		return result;
	}

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			Reporter.log("Error while closing connection");
		}
	}

	private static List<Map<String, String>> convertResultToMap(ResultSet resultSet) {
		List<Map<String, String>> queryData = new ArrayList<>();
		try {
			int columnLength = resultSet.getMetaData().getColumnCount();
			while (resultSet.next()) {
				HashMap<String, String> rowData = new HashMap<>();
				for (int colIterator = 1; colIterator <= columnLength; colIterator++) {
					String colName = resultSet.getMetaData().getColumnLabel(colIterator);
					String colValue = resultSet.getString(colName);
					rowData.put(colName, colValue);
				}
				queryData.add(rowData);
			}
		} catch (SQLException e) {
			Reporter.log(e.getMessage());
		}
		return queryData;
	}

	public static void fetchDBDataToFile(Connection connection, String query, String fileName) {
		try {
			FileHelper fileHelper = new FileHelper();
			ResultSet result = executeQuery(connection, query);
			int columnLength = result.getMetaData().getColumnCount();
			while (result.next()) {
				String rowData = "";
				StringBuilder sb = new StringBuilder();
				for (int colIterator = 1; colIterator <= columnLength; colIterator++) {
					String value = result.getString(colIterator);
					sb.append(value).append("---");
				}
				sb.setLength(sb.length() - 3);
				sb.append(System.getProperty("line.separator"));
				rowData = sb.toString();
				fileHelper.writeToFile(rowData, fileName, true);
			}

		} catch (SQLException e) {
			Reporter.log(e.getMessage());
		}
		closeConnection(connection);
	}

}
