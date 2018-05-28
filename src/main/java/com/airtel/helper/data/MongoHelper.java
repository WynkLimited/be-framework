package com.airtel.helper.data;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.common.BaseTestClass;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public static BasicDBObject prepareMongoQueryFromMultipleKeys(Map<String,String> map) {

        BasicDBObject andQuery = new BasicDBObject();
        BasicDBList basicDBList= new BasicDBList();
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

		if (mongoCursor.hasNext())
			mongoAsObjectResult.add(mongoCursor.next());

		return mongoAsObjectResult;
	}

	@SuppressWarnings("rawtypes")
	public static List<JSONObject> parseMongoDocuments(List<Object> resultFromMongoAsObject) {

		List<JSONObject> jsonObjectListOfMongo = new ArrayList<>();
		Iterator mongoResultIterator = resultFromMongoAsObject.iterator();
		while (mongoResultIterator.hasNext()) {
			JSONObject jsonObject = new JSONObject(ApiHelper.convertToJson(mongoResultIterator.next()));
			jsonObjectListOfMongo.add(jsonObject);
		}

		return jsonObjectListOfMongo;
	}
}
