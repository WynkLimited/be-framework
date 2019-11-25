package com.airtel.helper.Interfaces;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Map;

public interface IDbHelper {

    Connection createDbConnection(String server, String sid, String port, String userName, String password);

    List<Map<String, String>> fetchDatafromDb(Connection connection, String query);

    int updateDeleteDataFromDb(Connection connection, String query);

    ResultSet executeQuery(Connection connection, String query);

    void fetchDBDataToFile(Connection connection, String query, String fileName);

    boolean insertDataIntoSQL(Connection connection, String tableName, Field[] columnsName,
                              List<String> colValues);

    List<Map<String, String>> convertResultToMap(ResultSet resultSet);

    void closeConnection(Connection connection);

   boolean performSqlOperations(Connection connection,String query);
}
