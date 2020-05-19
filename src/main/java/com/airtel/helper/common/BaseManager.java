package com.airtel.helper.common;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Record;
import com.airtel.controller.GetAuthManager;
import com.airtel.dto.GetAuthRequest;
import com.airtel.dto.GetAuthResponse;
import com.airtel.helper.Interfaces.IAeroSpikeHelper;
import com.airtel.helper.Interfaces.IDbHelper;
import com.airtel.helper.Interfaces.IJsonParser;
import com.airtel.helper.Interfaces.IManager;
import com.airtel.helper.Interfaces.IMongoHelper;
import com.airtel.helper.Interfaces.IResponseValidator;
import com.airtel.helper.data.AerospikeHelper;
import com.airtel.helper.data.DbHelper;
import com.airtel.helper.data.JsonParser;
import com.airtel.helper.data.MongoHelper;
import com.airtel.helper.report.ReportHelper;
import com.airtel.validator.GenericComparator;
import com.google.gson.JsonArray;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public abstract class BaseManager extends ApiHelper
		implements IManager, IDbHelper, IMongoHelper, IAeroSpikeHelper, IResponseValidator, IJsonParser {

	/*
	 * 
	 * Implement IJson parser if needed.
	 * 
	 */
	protected final DbHelper _dbHelper;
	protected final AerospikeHelper _aeroSpikeHelper;
	protected final MongoHelper _mongoHelper;
	protected final JsonParser _jsonHelper;
	protected final GenericComparator _responsevalidator;

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
		return _dbHelper.insertDataIntoSQL(connection, tableName, columnsName, colValues);
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
		return this._mongoHelper.findMongoDocumentAndDeleteIt(mongoClient, key, mongoValue, dataBaseName,
				collectionName);
	}

	@Override
	public boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
			String searchKey, String searchValue, Object mongoNewUpdatedObject) {

		return this._mongoHelper.updateMongoDocument(mongoClient, collectionName, dataBaseName, searchKey, searchValue,
				mongoNewUpdatedObject);
	}

	@Override
	public boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
			String searchKey, String searchValue, Map mongoNewUpdatedObject) {

		return this._mongoHelper.updateMongoDocument(mongoClient, collectionName, dataBaseName, searchKey, searchValue,
				mongoNewUpdatedObject);
	}

	@Override
	public boolean updateMongoDocuments(MongoClient mongoClient, String collectionName, String dataBaseName,
			String searchKey, String searchValue, Map mongoNewUpdatedObject, boolean updateMultipleDocuments) {

		return this._mongoHelper.updateMongoDocuments(mongoClient, collectionName, dataBaseName, searchKey, searchValue,
				mongoNewUpdatedObject, updateMultipleDocuments);
	}

	@Override
	public List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient mongoClient, Map<String, String> map,
			String dataBaseName, String collectionName) {
		return this._mongoHelper.fetchDataFromMongoForMultipleKeys(mongoClient, map, dataBaseName, collectionName);
	}

	/**
	 * Aero Spike Helper Methods Implementation
	 **/

	public AerospikeClient getAeroSpikeClientForMultipleNodes(List<String> serverIpList, int port) {
		return this._aeroSpikeHelper.getAeroSpikeClientForMultipleNodes(serverIpList, port);

	}

	/// port : 30000
	public AerospikeClient getAeroSpikeClientForSingleNode(String serverIp, int port) {
		return this._aeroSpikeHelper.getAeroSpikeClientForSingleNode(serverIp, port);

	}

	public Record[] getMultipleRecordFromPKList(AerospikeClient client, String nameSpace, String set,
			List<String> pkList, String binName) {

		return this._aeroSpikeHelper.getMultipleRecordFromPKList(client, nameSpace, set, pkList, binName);
	}

	public Record getRecordForAkey(AerospikeClient client, String namespace, String setName, String value,
			String binName) {
		return this._aeroSpikeHelper.getRecordForAkey(client, namespace, setName, value, binName);
	}

	public void writeRecordForAKey(AerospikeClient client, String namespace, String setName, String value,
			List<Bin> binList) {
		this._aeroSpikeHelper.writeRecordForAKey(client, namespace, setName, value, binList);

	}

	public Boolean deleteRecordForAkey(AerospikeClient client, String namespace, String setName, String value) {

		return this._aeroSpikeHelper.deleteRecordForAkey(client, namespace, setName, value);

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
	 * This method will fetch map of value's at a particular JSON PATH mentioned in
	 * propertyFilePath
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
				finalExpectedVal = jsonArray.get(0) != null ? jsonArray.get(0).toString() : null; // get First Value
																									// from List in case
																									// of multiple
																									// values
			} else
				finalExpectedVal = expectedVal == null ? null : String.valueOf(expectedVal); // incase Of String or null
																								// values putting it as
																								// it is
			expectedMap.put(mapperProp.getKey(), finalExpectedVal);

		}
		return expectedMap;
	}

	@Override
	public Map<String, Map<String, Object>> fieldMapper(String expectedApiResp, String actualApiResp,
			Map<String, String> mapperProperties) {
		return null;
	}

	@Override
	public Map<String, Map<String, Object>> fieldMapperString(String expectedApiResp, String actualApiResp,
			Map<String, String> mapperProperties) {
		return null;
	}

	@Override
	public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath) {
		return this._responsevalidator.mappedResponseValidator(expectedJson, actualJson, propertyFilePath);
	}

	@Override
	public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath,
			String testCaseName) {
		return this._responsevalidator.mappedResponseValidator(expectedJson, actualJson, propertyFilePath,
				testCaseName);
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
	public boolean compareJsonWithRequest(String responseExpected, String responseActual, String request,
			String propFilePath) {
		return this._responsevalidator.compareJsonWithRequest(responseExpected, responseActual, request, propFilePath);
	}

	/**
	 * Json Parser Methods Implementation
	 **/

	@Override
	public <T> T convertFromJson(String jsonData, Class<T> classType) {
		return this._jsonHelper.convertFromJson(jsonData, classType);
	}

	@Override
	public <T> T convertFromJson(String jsonData, Type type) {
		return this._jsonHelper.convertFromJson(jsonData, type);
	}

	@Override
	public <T> List<T> convertFromJsonArray(String jsonArray, Type listType) {
		return this._jsonHelper.convertFromJsonArray(jsonArray, listType);
	}

	@Override
	public Object convertJsonArrayObjToStringObj(Object object) {
		return this._jsonHelper.convertJsonArrayObjToStringObj(object);
	}

	@Override
	public String convertToJson(Object obj) {
		return this._jsonHelper.convertToJson(obj);
	}

	@Override
	public Object fetchJPathVal(String inputJson, String keyName) {
		return this._jsonHelper.fetchJPathVal(inputJson, keyName);
	}

	@Override
	public <T> T fetchJPathVal(String inputJson, String keyName, Class<T> classType) {
		return this._jsonHelper.fetchJPathVal(inputJson, keyName, classType);
	}

	@Override
	public boolean isJsonArrayNotBlank(JsonArray jsonArray) {
		return this._jsonHelper.isJsonArrayNotBlank(jsonArray);
	}

	@Override
	public boolean isJsonObjectInsideJsonObjectNotBlank(JSONObject jsonObject, String field) {
		return this._jsonHelper.isJsonObjectInsideJsonObjectNotBlank(jsonObject, field);
	}

	@Override
	public boolean isListFieldBlank(JSONObject jsonObject, String field) {
		return this._jsonHelper.isListFieldBlank(jsonObject, field);
	}

	@Override
	public boolean isStringFeildNotBlankInsideJSONObject(JSONObject jsonObject, String field) {
		return this._jsonHelper.isListFieldBlank(jsonObject, field);
	}

	@Override
	public org.json.JSONArray objectToJSONArray(Object object) {
		return this._jsonHelper.objectToJSONArray(object);
	}

	@Override
	public Object sortArray(Object unsortedArrayObj) {
		return this._jsonHelper.sortArray(unsortedArrayObj);
	}

	@Override
	public boolean validateJsonSchema(String response, String responseSchema, String id) {
		return this._responsevalidator.validateJsonSchema(response, responseSchema, id);
	}

	@Override
	public void closeConnection(Connection connection) {
		this._dbHelper.closeConnection(connection);
	}

	@Override
	public boolean performSqlOperations(Connection connection, String query) {
		return this._dbHelper.performSqlOperations(connection, query);
	}

	public List<Header> getAuthenticatedHeaders(String env, String rtn, String endPoint, String httpMethod,
			String request, Map<String, String> queryParams) {
		return getEncryptedHeader(env, rtn, endPoint, httpMethod, request, queryParams);
	}

	public List<Header> getEncryptedHeader(String env, String rtn, String endPoint, String httpMethod, String request,
			Map<String, String> queryParams) {
		List<Header> headers = new ArrayList<>();
		Response apiResponse = getAuthResponse(env, rtn, endPoint, httpMethod, request, queryParams);
		if (apiResponse.contentType().contains(ContentType.JSON.toString()) && apiResponse.asString() != null
				&& apiResponse.getStatusCode() == 200) {
			GetAuthResponse authResponse = new GetAuthResponse(apiResponse);
			headers.add(new Header("x-bsy-os", authResponse.getXBsyOs()));
			headers.add(new Header("x-bsy-did", authResponse.getXBsyDid()));
			headers.add(new Header("x-bsy-dt", authResponse.getXBsyDt()));
			headers.add(new Header("x-bsy-bn", authResponse.getXBsyBn()));
			headers.add(new Header("x-bsy-utkn", authResponse.getXBsyUtkn()));
		} else
			ReportHelper.logValidationFailure("User is not logged in", "Http Response code should be 200",
					"Http Response code should is 500", "API Failure");
		return headers;

	}

	private Response getAuthResponse(String env, String rtn, String endPoint, String httpMethod, String request,
			Map<String, String> queryParams) {
		Response authResponse = executeApi(env, rtn, endPoint, httpMethod, request, queryParams);
		return authResponse;
	}

	private Response executeApi(String env, String rtn, String endPoint, String httpMethod, String request,
			Map<String, String> queryParams) {
		GetAuthManager authManager = new GetAuthManager();
		String apiUrl = authManager.setUrl();
		GetAuthRequest authRequest = authManager.setRequest(httpMethod, request, endPoint, queryParams);
		String apiRequest = convertToJson(authRequest);
		List<Header> apiHeader = authManager.setHeaders();
		Map<String, String> apiQueryParams = authManager.setQueryParams(env, rtn);
		Response response = postResponseWithHeaders(apiRequest, apiHeader, ContentType.JSON,apiUrl, apiQueryParams,
				false);
		return response;
	}

}
