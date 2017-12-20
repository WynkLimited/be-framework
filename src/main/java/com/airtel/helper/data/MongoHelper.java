package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Created by b0200202 on 12/6/17.
 */
public class MongoHelper {

	public static MongoClient connectToMongoDBServer(String serverIp, Integer port) {
		MongoClient mongoClient = new MongoClient(serverIp, port);

		return mongoClient;
	}

	public static MongoCollection<?> getCollection(MongoClient mongoClient, String dataBase, String collection) {

		MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBase);
		return mongoDatabase.getCollection(collection);

	}

	public static BasicDBObject getMongoDBQuery(String key, String value) {
		BasicDBObject searchQuery = new BasicDBObject();

		searchQuery.put(key, value);

		return searchQuery;
	}

	public static List<Object> getResultFromMongo(MongoCollection<?> mongoCollection, BasicDBObject searchQuery) {
		FindIterable<?> findIterable = mongoCollection.find(searchQuery);
		MongoCursor<?> mongoCursor = findIterable.iterator();
		List<Object> mongoAsObjectResult = new ArrayList<>();
		if (mongoCursor.hasNext())
			mongoAsObjectResult.add(mongoCursor.next());

		return mongoAsObjectResult;
	}
}
