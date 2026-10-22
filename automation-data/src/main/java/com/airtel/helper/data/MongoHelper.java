package com.airtel.helper.data;

import com.airtel.helper.Interfaces.IMongoHelper;
import com.airtel.helper.common.BaseTestClass;
import com.airtel.helper.report.ReportHelper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by b0200202 on 12/6/17.
 */
public class MongoHelper extends BaseTestClass  implements IMongoHelper {

	public  MongoClient getMongoClient (String serverIp, Integer port) {
		if (mongoClient ==null){
			synchronized (MongoHelper.class){
				if (mongoClient ==null){
					mongoClient = MongoClients.create("mongodb://" + serverIp + ":" + port);
				}
			}
		}
		return mongoClient;
	}

	@SuppressWarnings("rawtypes")
	public MongoCollection getCollection(MongoClient mongoClient, String dataBase, String collection) {

		MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBase);
		return mongoDatabase.getCollection(collection);

	}

	public BasicDBObject getMongoDBQuery(String key, Object value) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(key, value);
		return searchQuery;
	}

	public  BasicDBObject prepareMongoQueryFromMultipleKeys(Map<String, String> map) {

		BasicDBObject andQuery = new BasicDBObject();
		BasicDBList basicDBList = new BasicDBList();
		for (Map.Entry<String, String> entry : map.entrySet())
			basicDBList.add(new BasicDBObject(entry.getKey(), entry.getValue()));

		andQuery.put("$and", basicDBList);
		return andQuery;
	}

	@SuppressWarnings("rawtypes")
	
	public List<Object> getResultFromMongo(MongoCollection mongoCollection, BasicDBObject searchQuery) {
		FindIterable findIterable = mongoCollection.find(searchQuery);
		MongoCursor mongoCursor = findIterable.iterator();
		List<Object> mongoAsObjectResult = new ArrayList<>();

		while (mongoCursor.hasNext())
			mongoAsObjectResult.add(mongoCursor.next());

		return mongoAsObjectResult;
	}

	@SuppressWarnings("rawtypes")
	public  List<JSONObject> parseMongoDocuments(List<Object> resultFromMongoAsObject) {

		List<JSONObject> jsonObjectListOfMongo = new ArrayList<>();
		Iterator<Object> mongoResultIterator = resultFromMongoAsObject.iterator();
		while (mongoResultIterator.hasNext()) {
			JSONObject jsonObject = new JSONObject(jsonHelper.convertToJson(mongoResultIterator.next()));
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

	public  boolean findMongoDocumentAndDeleteIt(MongoClient mongoClient, String key, Object mongoValue,
													   String dataBaseName, String collectionName) {
		boolean mongoDeleteion = true;
		BasicDBObject query = getMongoDBQuery(key, mongoValue);
		MongoCollection mongoCollection = getCollection(mongoClient, dataBaseName, collectionName);
		List<Object> resultFromMongoAsObject = getResultFromMongo(mongoCollection, query);

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
	public  boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
											  String searchKey, String searchValue, Object mongoNewUpdatedObject) {
		boolean updateStatus = true;
		MongoCollection mongoCollection = null;
		try {
			mongoCollection = getCollection(mongoClient, dataBaseName, collectionName);
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
	 *
	 * This method find a document on the basis of search key & value and then
	 * update only single mongo document for the Map passed as mongoNewUpdatedObject(key value pair ) in request
	 * @param mongoClient
	 * @param collectionName
	 * @param dataBaseName
	 * @param searchKey
	 * @param searchValue
	 * @param mongoNewUpdatedObject
	 * @return
	 */
	public  boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
											  String searchKey, String searchValue, Map mongoNewUpdatedObject) {
		return updateMongoDocuments(mongoClient,collectionName,dataBaseName,searchKey,searchValue,mongoNewUpdatedObject,false);
	}



	/**
	 * this method find a document on the basis of search key & value and then
	 * update either single  mongo document or multiple mongo documents
	 * which is passed in parameter as mongoNewUpdatedObject as it is without converting it to string
	 * @param mongoClient
	 * @param collectionName
	 * @param dataBaseName
	 * @param searchKey
	 * @param searchValue
	 * @param mongoNewUpdatedObject
	 * @return
	 */
	public  boolean updateMongoDocuments(MongoClient mongoClient, String collectionName, String dataBaseName,
											   String searchKey, String searchValue, Map mongoNewUpdatedObject,boolean updateMultipleDocuments) {
		boolean updateStatus = true;
		MongoCollection mongoCollection = null;
		try {
			mongoCollection = getCollection(mongoClient, dataBaseName, collectionName);
			BasicDBObject query = new BasicDBObject();
			query.append(searchKey, searchValue);
			Bson newDocument = new Document("$set", mongoNewUpdatedObject);
			if (updateMultipleDocuments)mongoCollection.updateMany(query, newDocument);
			else
				mongoCollection.findOneAndUpdate(query, newDocument);
		} catch (Exception e) {
			updateStatus = false;
			ReportHelper.reporterLogging(false,
					"Error In updating Mongo Document datatabase " + dataBaseName + " collection  :  "
							+ mongoCollection +" with exception -> "+e);
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

		BasicDBObject query =prepareMongoQueryFromMultipleKeys(map);
		MongoCollection mongoCollection = getCollection(mongoClient, dataBaseName, collectionName);
		List<Object> mongoResult = getResultFromMongo(mongoCollection, query);
		return parseMongoDocuments(mongoResult);
	}

}