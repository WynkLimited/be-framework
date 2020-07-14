package com.airtel.helper.data;

import com.airtel.helper.Interfaces.IDbHelper;
import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;
import org.testng.Reporter;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper implements IDbHelper {

    static final String CONFIGURATION_FILE_PATH = "config/envConfig.properties";

    public DbHelper() {

    }

    public Connection createDbConnection(String server, String sid, String port, String userName, String password) {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String connectionString = "jdbc:oracle:thin:@" + server + ":" + port + ":" + sid;
            con = DriverManager.getConnection(connectionString, userName, password);
        } catch (Exception e) {
            Reporter.log(e.getLocalizedMessage());
        }
        return con;
    }

    @Override
    public List<Map<String, String>> fetchDatafromDb(Connection connection, String query) {
        ResultSet result = executeQuery(connection, query);
        return convertResultToMap(result);
    }

    /**
     * @param connection
     * @param query
     * @return int - This output will be number in int which Specifies
     * number of rows affected by The Query.The output will be -1 in
     * case of any Exception.The Output will be 1 in case of 1 row is affected.
     */
    @Override
    public int updateDeleteDataFromDb(Connection connection, String query) {
        int result = 0;

        try {
            Statement stmt = connection.createStatement();
            result = stmt.executeUpdate(query);
            connection.commit();
        } catch (SQLException var4) {
            ReportHelper.logValidationFailure("Couldn't get db connection", "DB connection", var4.getMessage(), "DB connection failure");
            return -1;
        } finally {
            this.closeConnection(connection);
        }

        return result;
    }

    @Override
    public ResultSet executeQuery(Connection connection, String query) {
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

   public    void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            Reporter.log("Error while closing connection");
        }
    }

    /**
     * This method converts the data returned from DB to List<Map>.
     *
     * @param resultSet
     * @return
     */
    @Override
    public List<Map<String, String>> convertResultToMap(ResultSet resultSet) {
        List<Map<String, String>> queryData = new ArrayList<>();
        try {
            int columnLength = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                HashMap<String, String> rowData = new HashMap<>();
                for (int colIterator = 1; colIterator <= columnLength; colIterator++) {
                    String colName = resultSet.getMetaData().getColumnLabel(colIterator);
                    String colValue = null;
                    if (resultSet.getObject(colName) == null) {
                        rowData.put(colName, colValue);
                    } else if (resultSet.getObject(colName).getClass().getName().contains("BLOB")) {
                        Blob blob = resultSet.getBlob(colName);
                        InputStream is = blob.getBinaryStream();
                        ObjectInputStream ois = new ObjectInputStream(is);
                        Object obj = ois.readObject();
                        colValue = obj.toString();
                        rowData.put(colName, colValue);
                        ois.close();
                    } else {
                        colValue = resultSet.getString(colName);
                        rowData.put(colName, colValue);
                    }
                }
                queryData.add(rowData);
            }
        } catch (Exception e) {
            Reporter.log(e.getMessage());
        }
        return queryData;
    }

    /**
     * @param connection
     * @param query
     * @param fileName
     */
    @Override
    public void fetchDBDataToFile(Connection connection, String query, String fileName) {
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
        } finally {
            this.closeConnection(connection);
        }
    }

    public boolean insertDataIntoSQL(Connection connection, String tableName, Field[] columnsName,
                                     List<String> colValues) {
        try {
            String commaSepeartedColumnsName = "";
            String commaSepeartedColumnsValue = "";

            for (int i = 0; i < columnsName.length - 1; i++) {
                commaSepeartedColumnsName = commaSepeartedColumnsName + columnsName[i].getName() + ",";
                commaSepeartedColumnsValue = commaSepeartedColumnsValue + "?" + ",";
            }
            // get comma sepearted list of column Name
            commaSepeartedColumnsName = commaSepeartedColumnsName + columnsName[columnsName.length - 1].getName();
            // get comma sepearted list of column Vlaue
            commaSepeartedColumnsValue = commaSepeartedColumnsValue + " ? ";

            String query = "insert into " + tableName + "(" + commaSepeartedColumnsName + ")" + " values ("
                    + commaSepeartedColumnsValue + ")";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int k = 0; k < colValues.size(); k++)
                preparedStatement.setString(k + 1, colValues.get(k));

            // execute the preparedstatement and return result accordingly
            return preparedStatement.executeUpdate() == 1;

        } catch (Exception e) {
            Reporter.log(e.getMessage());
            return false;
        }

    }



    /**
     * This method perform any DML operation on sql on the basis of query passed in parameter
     * @param connection
     * @param query
     * @return
     */
    public  boolean performSqlOperations(Connection connection,String query) {
        PreparedStatement preparedStatement = null;
        boolean result=true;

        try {
            preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeUpdate() == 1; // execute the preparedstatement and return result accordingly

        } catch (SQLException ex) {
            result=false;
            ReportHelper.reporterLogging(false, "Error In Executing sql query " + ex.getMessage());

        } finally {
            try {
                preparedStatement.close();
            } catch (Exception ex) {
                ReportHelper.reporterLogging(false, "Error In closing preparedStatement in sql  " + ex.getMessage());
            }
        }
        return result;
    }


}
