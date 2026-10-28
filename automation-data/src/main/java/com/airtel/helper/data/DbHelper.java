package com.airtel.helper.data;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airtel.helper.Interfaces.IDbHelper;
import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;

/**
 * Oracle JDBC helper with try-with-resources and safer statement handling.
 */
public class DbHelper implements IDbHelper {

	private static final Logger log = LoggerFactory.getLogger(DbHelper.class);
	private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

	/** LOB env config used by {@link TestDataHelper} data providers. */
	public static final String CONFIGURATION_FILE_PATH = "config/envConfig.properties";

	@Override
	public Connection createDbConnection(String server, String sid, String port, String userName, String password) {
		try {
			Class.forName(ORACLE_DRIVER);
			String connectionString = "jdbc:oracle:thin:@" + server + ":" + port + ":" + sid;
			Connection con = DriverManager.getConnection(connectionString, userName, password);
			log.info("Oracle connection opened for {}:{}", server, sid);
			return con;
		} catch (Exception e) {
			log.error("Failed to create Oracle connection to {}:{}", server, sid, e);
			ReportHelper.reporterLogging(false, "DB connection failure: " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<Map<String, String>> fetchDatafromDb(Connection connection, String query) {
		ResultSet result = executeQuery(connection, query);
		return convertResultToMap(result);
	}

	/**
	 * @return rows affected, or {@code -1} on error. Closes the connection (legacy behaviour).
	 */
	@Override
	public int updateDeleteDataFromDb(Connection connection, String query) {
		Objects.requireNonNull(connection, "connection");
		try (Statement stmt = connection.createStatement()) {
			int result = stmt.executeUpdate(query);
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			return result;
		} catch (SQLException e) {
			log.error("update/delete failed: {}", query, e);
			ReportHelper.logValidationFailure("Couldn't execute DB update", "DB update", e.getMessage(),
					"DB connection failure");
			return -1;
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public ResultSet executeQuery(Connection connection, String query) {
		if (connection == null) {
			return null;
		}
		try {
			Statement stmt = connection.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			log.error("Query failed: {}", query, e);
			ReportHelper.logValidationFailure("Couldn't execute DB query", "DB query", e.getMessage(),
					"DB connection failure");
			return null;
		}
	}

	@Override
	public void closeConnection(Connection connection) {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			log.warn("Error while closing connection", e);
		}
	}

	@Override
	public List<Map<String, String>> convertResultToMap(ResultSet resultSet) {
		List<Map<String, String>> queryData = new ArrayList<>();
		if (resultSet == null) {
			return queryData;
		}
		try {
			ResultSetMetaData meta = resultSet.getMetaData();
			int columnLength = meta.getColumnCount();
			while (resultSet.next()) {
				Map<String, String> rowData = new HashMap<>();
				for (int col = 1; col <= columnLength; col++) {
					String colName = meta.getColumnLabel(col);
					Object value = resultSet.getObject(col);
					if (value == null) {
						rowData.put(colName, null);
					} else if (value instanceof Blob || value.getClass().getName().contains("BLOB")) {
						Blob blob = resultSet.getBlob(col);
						try (InputStream is = blob.getBinaryStream();
								ObjectInputStream ois = new ObjectInputStream(is)) {
							rowData.put(colName, String.valueOf(ois.readObject()));
						}
					} else {
						rowData.put(colName, resultSet.getString(col));
					}
				}
				queryData.add(rowData);
			}
		} catch (Exception e) {
			log.error("Failed converting ResultSet to map", e);
		}
		return queryData;
	}

	@Override
	public void fetchDBDataToFile(Connection connection, String query, String fileName) {
		FileHelper fileHelper = new FileHelper();
		try {
			ResultSet result = executeQuery(connection, query);
			if (result == null) {
				return;
			}
			int columnLength = result.getMetaData().getColumnCount();
			while (result.next()) {
				StringJoiner joiner = new StringJoiner("---");
				for (int col = 1; col <= columnLength; col++) {
					joiner.add(result.getString(col));
				}
				fileHelper.writeToFile(joiner + System.lineSeparator(), fileName, true);
			}
		} catch (SQLException e) {
			log.error("fetchDBDataToFile failed", e);
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public boolean insertDataIntoSQL(Connection connection, String tableName, Field[] columnsName,
			List<String> colValues) {
		Objects.requireNonNull(connection, "connection");
		Objects.requireNonNull(columnsName, "columnsName");
		if (columnsName.length == 0) {
			return false;
		}
		StringJoiner cols = new StringJoiner(",");
		StringJoiner placeholders = new StringJoiner(",");
		for (Field column : columnsName) {
			cols.add(column.getName());
			placeholders.add("?");
		}
		String query = "insert into " + tableName + "(" + cols + ") values (" + placeholders + ")";
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			for (int i = 0; i < colValues.size(); i++) {
				preparedStatement.setString(i + 1, colValues.get(i));
			}
			return preparedStatement.executeUpdate() == 1;
		} catch (Exception e) {
			log.error("Insert failed for table {}", tableName, e);
			return false;
		}
	}

	@Override
	public boolean performSqlOperations(Connection connection, String query) {
		Objects.requireNonNull(connection, "connection");
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			return preparedStatement.executeUpdate() >= 0;
		} catch (SQLException ex) {
			log.error("SQL operation failed: {}", query, ex);
			ReportHelper.reporterLogging(false, "Error executing SQL: " + ex.getMessage());
			return false;
		}
	}
}
