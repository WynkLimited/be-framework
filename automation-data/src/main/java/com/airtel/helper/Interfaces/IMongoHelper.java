package com.airtel.helper.Interfaces;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface IMongoHelper {

    MongoClient getMongoClient (String serverIp, Integer port);

    MongoCollection getCollection(MongoClient mongoClient, String dataBase, String collectionName);

    BasicDBObject getMongoDBQuery(String key, Object value);

    BasicDBObject prepareMongoQueryFromMultipleKeys(Map<String, String> map);

    List<Object> getResultFromMongo(MongoCollection mongoCollection, BasicDBObject searchQuery);

    List<JSONObject> parseMongoDocuments(List<Object> resultFromMongoAsObject);

    boolean findMongoDocumentAndDeleteIt(MongoClient mongoClient, String key, Object mongoValue,
                                         String dataBaseName, String collectionName);

    boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
                                String searchKey, String searchValue, Object mongoNewUpdatedObject);

   boolean updateMongoDocument(MongoClient mongoClient, String collectionName, String dataBaseName,
                        String searchKey, String searchValue, Map mongoNewUpdatedObject);

    List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient mongoClient, Map<String, String> map, String dataBaseName, String collectionName);

    boolean updateMongoDocuments(MongoClient mongoClient, String collectionName, String dataBaseName,
                                 String searchKey, String searchValue, Map mongoNewUpdatedObject,boolean updateMultipleDocuments);
}