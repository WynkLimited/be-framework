package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.bson.Document;

import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.common.BaseTestClass;
import com.airtel.helper.report.ReportHelper;

import com.mongodb.BasicDBList;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Created by b0200202 on 12/6/17.
 */
public class MongoHelper extends BaseTestClass {

	public static MongoClient connectToMongoDBServer(String serverIp, Integer port) {
		mongoClient = new MongoClient(serverIp, port);
		return mongoClient;
	}

	@SuppressWarnings("rawtypes")
	public static MongoCollection getCollection(MongoClient mongoClient, String dataBase, String collection) {

		MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBase);
		return mongoDatabase.getCollection(collection);

	}

	public static BasicDBObject getMongoDBQuery(String key, String value) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(key, value);
		return searchQuery;
	}

	public static BasicDBObject prepareMongoQueryFromMultipleKeys(Map<String, String> map) {

		BasicDBObject andQuery = new BasicDBObject();
		BasicDBList basicDBList = new BasicDBList();
		for (Map.Entry<String, String> entry : map.entrySet())
			basicDBList.add(new BasicDBObject(entry.getKey(), entry.getValue()));

		andQuery.put("$and", basicDBList);
		return andQuery;
	}

	@SuppressWarnings("rawtypes")
	public static List<Object> getResultFromMongo(MongoCollection mongoCollection, BasicDBObject searchQuery) {
		FindIterable findIterable = mongoCollection.find(searchQuery);
		MongoCursor mongoCursor = findIterable.iterator();
		List<Object> mongoAsObjectResult = new ArrayList<>();

		while (mongoCursor.hasNext())
			mongoAsObjectResult.add(mongoCursor.next());

		return mongoAsObjectResult;
	}

	@SuppressWarnings("rawtypes")
	public static List<JSONObject> parseMongoDocuments(List<Object> resultFromMongoAsObject) {

		List<JSONObject> jsonObjectListOfMongo = new ArrayList<>();
		Iterator<Object> mongoResultIterator = resultFromMongoAsObject.iterator();
		while (mongoResultIterator.hasNext()) {
			JSONObject jsonObject = new JSONObject(ApiHelper.convertToJson(mongoResultIterator.next()));
			jsonObjectListOfMongo.add(jsonObject);
		}

		return jsonObjectListOfMongo;
	}

	/**
	 * This method delete mongo document
	 * 
	 * @param mongoClient
	 * @param mongoValue
	 * @param dataBaseName
	 * @param collectionName
	 * @return
	 */

	public static boolean findMongoDocumentAndDeleteIt(MongoClient mongoClient, String key, String mongoValue,
			String dataBaseName, String collectionName) {
		boolean mongoDeleteion = true;
		BasicDBObject query = MongoHelper.getMongoDBQuery(key, mongoValue);
		MongoCollection mongoCollection = MongoHelper.getCollection(mongoClient, dataBaseName, collectionName);
		List<Object> resultFromMongoAsObject = MongoHelper.getResultFromMongo(mongoCollection, query);

		if (!resultFromMongoAsObject.isEmpty()) {
			Document mongoDocument;
			Iterator<Object> mongoListIterator = resultFromMongoAsObject.iterator();
			while (mongoListIterator.hasNext()) {
				mongoDocument = (Document) mongoListIterator.next();
				try {
					mongoCollection.deleteOne(mongoDocument);

				} catch (Exception e) {
					mongoDeleteion = false;
					ReportHelper.logValidationFailure("MongoDeletion", "to delete", "not able to delete",
							"Error In Deleting Mongo Document from DB : " + dataBaseName + "collectionName :"
									+ collectionName);
				}
			}
		} else
			mongoDeleteion = false;

		return mongoDeleteion;

	}

	/**
	 * this method find a document on the basis of search key & value and then
	 * update a mongo document which is passed in parameter as mongoNewUpdatedObject
	 * 
	 * @param mongoClient
	 * @param collectionName
	 * @param dataBaseName
	 * @param searchKey
	 * @param searchValue
	 * @param mongoNewUpdatedObject
	 * @return
	 */
	public static boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
			String searchKey, String searchValue, Object mongoNewUpdatedObject) {
		boolean updateStatus = true;
		MongoCollection mongoCollection = null;
		try {
			mongoCollection = MongoHelper.getCollection(mongoClient, dataBaseName, collectionName);
			BasicDBObject query = new BasicDBObject();
			query.append(searchKey, searchValue);
			BasicDBObject updatedDocument = BasicDBObject.parse(mongoNewUpdatedObject.toString());
			Bson newDocument = new Document("$set", updatedDocument);

			mongoCollection.findOneAndUpdate(query, newDocument);
		} catch (Exception e) {
			updateStatus = false;
			ReportHelper.logValidationFailure("MongoUpdation", "to update", "not able to updated",
					"Error In updating Mongo Document datatabase " + dataBaseName + " collection  :  "
							+ mongoCollection);
		}

		return updateStatus;
	}



	/**
	 * This method will fetch data from mongo for multiple keys
	 * @param mongoClient
	 * @param map
	 * @param dataBaseName
	 * @param collectionName
	 * @return
	 */
	public List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient mongoClient,Map<String,String> map, String dataBaseName, String collectionName) {

		BasicDBObject query = MongoHelper.prepareMongoQueryFromMultipleKeys(map);
		MongoCollection mongoCollection = MongoHelper.getCollection(mongoClient, dataBaseName, collectionName);
		List<Object> mongoResult = MongoHelper.getResultFromMongo(mongoCollection, query);
		return MongoHelper.parseMongoDocuments(mongoResult);
	}


	/**
	 * This method will convert the list of json returned from mongo to list of  particular class type using GSON
	 * @param jsonObjectListOfMongo
	 * @param classType
	 * @param <T>
	 * @return
	 */
	public <T> List<T> convertFromJsonToInputClassList(List<JSONObject> jsonObjectListOfMongo, Class<T> classType) {
		Gson gson= new Gson();
		T requiredClass =null;

		List<T>  requiredResponseList =new ArrayList<T>();
		if (!jsonObjectListOfMongo.isEmpty()){
			Iterator<JSONObject> mongoObjectIteartor = jsonObjectListOfMongo.iterator();

			while (mongoObjectIteartor.hasNext()) {
				JSONObject mongoObject = mongoObjectIteartor.next();

				try {
					requiredClass = gson.fromJson(mongoObject.toString(),classType);
				}catch (Exception e)
				{
					ReportHelper.reporterLogging(false,"Exception in Gnson while Mapping mongo Object to Specific class for ID "+mongoObject.get("_id")+e.getStackTrace());
				}

				requiredResponseList.add(requiredClass);
			}
			return requiredResponseList;
		}
		else
			return  new ArrayList<T>();
	}

}
