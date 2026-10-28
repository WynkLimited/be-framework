package com.airtel.helper.Interfaces;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

public interface IMongoHelper {

	MongoClient getMongoClient(String serverIp, Integer port);

	MongoClient getMongoClient(String connectionString);

	/**
	 * Connect using a full MongoDB URI, e.g.
	 * {@code mongodb://user:pass@host1:27017,host2:27017/databaseName}
	 */
	MongoClient getMongoClientByUri(String mongoUri);

	MongoCollection<Document> getCollection(MongoClient mongoClient, String dataBase, String collectionName);

	/** Uses the database name embedded in the URI path. */
	MongoCollection<Document> getCollectionByUri(String mongoUri, String collectionName);

	Document getMongoDBQuery(String key, Object value);

	Bson prepareMongoQueryFromMultipleKeys(Map<String, String> map);

	List<Document> getResultFromMongo(MongoCollection<Document> mongoCollection, Bson searchQuery);

	List<JSONObject> parseMongoDocuments(List<Document> documents);

	boolean findMongoDocumentAndDeleteIt(MongoClient mongoClient, String key, Object mongoValue, String dataBaseName,
			String collectionName);

	boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Object mongoNewUpdatedObject);

	boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Map<String, Object> mongoNewUpdatedObject);

	boolean updateMongoDocuments(MongoClient mongoClient, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Map<String, Object> mongoNewUpdatedObject, boolean updateMultipleDocuments);

	List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient mongoClient, Map<String, String> map,
			String dataBaseName, String collectionName);
}
