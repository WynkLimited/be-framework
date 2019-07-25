package com.airtel.helper.common;

import com.aerospike.client.*;
import com.airtel.helper.Interfaces.*;
import com.airtel.helper.data.*;
import com.airtel.validator.GenericComparator;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import net.minidev.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseManager extends ApiHelper implements IManager, IDbHelper, IMongoHelper, IAeroSpikeHelper, IResponseValidator,IJsonParser {


    /*

    Implement IJson parser if needed.

     */
    public final IDbHelper _dbHelper;
    public final IAeroSpikeHelper _aeroSpikeHelper;
    public final IMongoHelper _mongoHelper;
    public final IJsonParser _jsonHelper;
    public final IResponseValidator _responsevalidator;

    public BaseManager() {

        _dbHelper = new DbHelper();
        _mongoHelper = new MongoHelper();
        _aeroSpikeHelper = new AerospikeHelper();
        _responsevalidator = new GenericComparator();
        _jsonHelper = new JsonParser();
    }


    /**
     * SQL Helper Methods implementation
     */


    public Connection createDbConnection(String server, String username, String password, String sid, String port) {
        return this._dbHelper.createDbConnection(server, sid, port, username, password);
    }

    @Override
    public List<Map<String, String>> fetchDatafromDb(Connection connection, String query) {
        return this._dbHelper.fetchDatafromDb(connection, query);
    }

    @Override
    public int updateDeleteDataFromDb(Connection connection, String query) {
        return _dbHelper.updateDeleteDataFromDb(connection, query);
    }

    @Override
    public ResultSet executeQuery(Connection connection, String query) {
        return _dbHelper.executeQuery(connection, query);
    }

    @Override
    public void fetchDBDataToFile(Connection connection, String query, String fileName) {
        _dbHelper.fetchDBDataToFile(connection, query, fileName);
    }

    @Override
    public boolean insertDataIntoSQL(Connection connection, String tableName, Field[] columnsName,
                                     List<String> colValues) {
        return _dbHelper.insertDataIntoSQL(connection, tableName, columnsName,
                colValues);
    }

    @Override
    public List<Map<String, String>> convertResultToMap(ResultSet resultSet) {
        return null;
    }


    /**
     * Mongo Helper Methods implementation
     */


    @Override
    public MongoClient getMongoClient(String serverIp, Integer port) {
        return this._mongoHelper.getMongoClient(serverIp, port);
    }

    @Override
    public MongoCollection getCollection(MongoClient mongoClient, String dataBase, String collectionName) {
        return this._mongoHelper.getCollection(mongoClient, dataBase, collectionName);
    }

    @Override
    public BasicDBObject getMongoDBQuery(String key, Object value) {
        return this._mongoHelper.getMongoDBQuery(key, value);
    }

    @Override
    public BasicDBObject prepareMongoQueryFromMultipleKeys(Map<String, String> map) {
        return this._mongoHelper.prepareMongoQueryFromMultipleKeys(map);
    }

    @Override
    public List<Object> getResultFromMongo(MongoCollection mongoCollection, BasicDBObject searchQuery) {
        return this._mongoHelper.getResultFromMongo(mongoCollection, searchQuery);
    }

    @Override
    public List<JSONObject> parseMongoDocuments(List<Object> resultFromMongoAsObject) {
        return this._mongoHelper.parseMongoDocuments(resultFromMongoAsObject);
    }

    @Override
    public boolean findMongoDocumentAndDeleteIt(MongoClient mongoClient, String key, Object mongoValue,
                                                String dataBaseName, String collectionName) {
        return this._mongoHelper.findMongoDocumentAndDeleteIt(mongoClient, key, mongoValue, dataBaseName, collectionName);
    }

    @Override
    public boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
                                       String searchKey, String searchValue, Object mongoNewUpdatedObject) {

        return this._mongoHelper.updateMongoDocument(mongoClient, dataBaseName, collectionName, searchKey, searchValue, mongoNewUpdatedObject);
    }

    @Override
    public List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient mongoClient, Map<String, String> map, String dataBaseName, String collectionName) {
        return this._mongoHelper.fetchDataFromMongoForMultipleKeys(mongoClient, map, dataBaseName, collectionName);
    }

    /**
     * Aero Spike Helper Methods Implementation
     **/

    public AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList, int port) {
        return this._aeroSpikeHelper.getAeroSpikeClientForMultipleNodes(serverIpList, port);

    }

    ///port : 30000
    public AerospikeClient getAeroSpikeClientForSingleNode(String serverIp, int port) {
        return this._aeroSpikeHelper.getAeroSpikeClientForSingleNode(serverIp, port);

    }

    public Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set,
                                                List<String> pkList, String binName) {

        return this._aeroSpikeHelper.getMultipleRecordFromPKList(client, nameSpace, set,
                pkList, binName);
    }

    public Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
                                   String binName) {
        return this._aeroSpikeHelper.getRecordForAkey(client, namespace, setName,
                value, binName);
    }

    public void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
                                   List<Bin> binList) {
        this._aeroSpikeHelper.writeRecordForAKey(client, namespace, setName,
                value, binList);

    }

    public Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value) {

        return this._aeroSpikeHelper.deleteRecordForAkey(client, namespace, setName,
                value);

    }

    public List<Map<String, Object>> parseAerospikeRecordForaBin(Record[] record) {

        return this._aeroSpikeHelper.parseAerospikeRecordForaBin(record);

    }

    public List<Map<String, Object>> parseMultipleRecord(Record[] record) {

        return this._aeroSpikeHelper.parseMultipleRecord(record);

    }

    public Map<String, Object> parseSingleRecord(Record record) {
        return this._aeroSpikeHelper.parseSingleRecord(record);
    }

    public Record getAllBinsForAKey(AerospikeClient client, String namespace, String setName, String value) {
        return this._aeroSpikeHelper.getAllBinsForAKey(client, namespace, setName, value);
    }


    /**
     * This method will fetch map of value's at a particular
     * JSON PATH mentioned in propertyFilePath
     *
     * @param responseJson
     * @param propertyFilePath
     * @return
     */
    public Map<String, String> fieldMapperForASingleJsonObject(String responseJson, String propertyFilePath) {
        String finalExpectedVal = "";
        Map<String, String> masterMappings = fetchAllProperties(propertyFilePath);
        Map<String, String> expectedMap = new HashMap<>();
        for (Map.Entry<String, String> mapperProp : masterMappings.entrySet()) {
            Object expectedVal = _jsonHelper.fetchJPathVal(responseJson, mapperProp.getValue());
            if (expectedVal != null && expectedVal.getClass().isInstance(new JSONArray())) {

                JSONArray jsonArray = (JSONArray) expectedVal;
                finalExpectedVal = jsonArray.get(0) != null ? jsonArray.get(0).toString() : null; // get First Value from List in case of multiple values
            } else
                finalExpectedVal = expectedVal == null ? null : String.valueOf(expectedVal); // incase Of String or null values  putting  it as it is
            expectedMap.put(mapperProp.getKey(), finalExpectedVal);

        }
        return expectedMap;
    }

    @Override
    public Map<String, Map<String, Object>> fieldMapper(String expectedApiResp, String actualApiResp, Map<String, String> mapperProperties) {
        return null;
    }

    @Override
    public Map<String, Map<String, Object>> fieldMapperString(String expectedApiResp, String actualApiResp, Map<String, String> mapperProperties) {
        return null;
    }

    @Override
    public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath) {
        return this._responsevalidator.mappedResponseValidator(expectedJson, actualJson, propertyFilePath);
    }

    @Override
    public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath, String testCaseName) {
       return this._responsevalidator.mappedResponseValidator(expectedJson, actualJson, propertyFilePath, testCaseName);
    }

    @Override
    public boolean compareJson(String responseExpected, String responseActual) {
        return this._responsevalidator.compareJson(responseExpected, responseActual);
    }

    @Override
    public boolean compareJson(String responseExpected, String responseActual, String propFilePath) {
        return this._responsevalidator.compareJson(responseExpected, responseActual, propFilePath);
    }

    @Override
    public boolean compareJsonWithRequest(String responseExpected, String responseActual, String request, String propFilePath) {
        return this._responsevalidator.compareJsonWithRequest(responseExpected, responseActual, request, propFilePath);
    }
}
