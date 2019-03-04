package com.airtel.helper.common;

import com.airtel.helper.Interfaces.IDbHelper;
import com.airtel.helper.Interfaces.IManager;
import com.airtel.helper.data.DbHelper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public abstract class BaseManager extends ApiHelper implements IManager, IDbHelper {

    DbHelper _dbConnection;

    public BaseManager() {

    }

    public Connection createDbConnection(String server, String username, String password, String sid, String port) {

        this._dbConnection = new DbHelper();
        return this._dbConnection.createDbConnection(server, sid, port, username, password);
    }

    @Override
    public int updateDeleteDataFromDb(Connection connection, String query) {
        return _dbConnection.updateDeleteDataFromDb(connection, query);
    }

    @Override
    public ResultSet executeQuery(Connection connection, String query) {
        return _dbConnection.executeQuery(connection, query);
    }

    @Override
    public void fetchDBDataToFile(Connection connection, String query, String fileName) {
        _dbConnection.fetchDBDataToFile(connection, query, fileName);
    }

    @Override
    public boolean insertDataIntoSQL(Connection connection, String tableName, Field[] columnsName,
                                     List<String> colValues) {
        return _dbConnection.insertDataIntoSQL(connection, tableName, columnsName,
                colValues);
    }


}
