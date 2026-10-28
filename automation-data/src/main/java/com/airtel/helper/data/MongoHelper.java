package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airtel.helper.Interfaces.IMongoHelper;
import com.airtel.helper.report.ReportHelper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * MongoDB helper using the modern sync driver ({@link Document}, {@link Filters}).
 */
public class MongoHelper implements IMongoHelper {

	private static final Logger log = LoggerFactory.getLogger(MongoHelper.class);

	private final JsonParser jsonHelper = new JsonParser();
	private volatile MongoClient mongoClient;
	private volatile String activeConnectionString;

	@Override
	public MongoClient getMongoClient(String serverIp, Integer port) {
		return getMongoClientByUri("mongodb://" + serverIp + ":" + port);
	}

	@Override
	public MongoClient getMongoClient(String connectionString) {
		return getMongoClientByUri(connectionString);
	}

	/**
	 * Preferred connection API. Supports replica-set / auth URIs, e.g.
	 * {@code mongodb://user:pass@host1:27017,host2:27017,host3:27017/dbName}
	 */
	@Override
	public MongoClient getMongoClientByUri(String mongoUri) {
		Objects.requireNonNull(mongoUri, "mongoUri");
		String uri = mongoUri.trim();
		if (!(uri.startsWith("mongodb://") || uri.startsWith("mongodb+srv://"))) {
			throw new IllegalArgumentException(
					"Invalid Mongo URI. Expected mongodb:// or mongodb+srv:// but got: " + maskUri(uri));
		}

		MongoClient local = mongoClient;
		if (local != null && uri.equals(activeConnectionString)) {
			return local;
		}

		synchronized (this) {
			if (mongoClient != null && uri.equals(activeConnectionString)) {
				return mongoClient;
			}
			if (mongoClient != null) {
				try {
					mongoClient.close();
				} catch (Exception e) {
					log.warn("Error closing previous MongoClient", e);
				}
			}
			ConnectionString connectionString = new ConnectionString(uri);
			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.build();
			mongoClient = MongoClients.create(settings);
			activeConnectionString = uri;
			log.info("MongoClient created via URI {}", maskUri(uri));
			return mongoClient;
		}
	}

	@Override
	public MongoCollection<Document> getCollection(MongoClient client, String dataBase, String collection) {
		return client.getDatabase(dataBase).getCollection(collection);
	}

	/**
	 * Connects with the URI and opens the collection on the database named in the URI path
	 * (e.g. {@code .../uclm_audience_manager_preprod}).
	 */
	@Override
	public MongoCollection<Document> getCollectionByUri(String mongoUri, String collectionName) {
		MongoClient client = getMongoClientByUri(mongoUri);
		String databaseName = extractDatabaseName(mongoUri);
		if (databaseName == null || databaseName.isBlank()) {
			throw new IllegalArgumentException(
					"Mongo URI has no database name in the path. Example: mongodb://user:pass@host:27017/myDb");
		}
		return getCollection(client, databaseName, collectionName);
	}

	/** Database segment from URI path, or {@code null} if absent. */
	public String extractDatabaseName(String mongoUri) {
		return new ConnectionString(mongoUri).getDatabase();
	}

	private static String maskUri(String uri) {
		return uri.replaceAll("://([^:/@]+):([^@/]+)@", "://$1:****@");
	}

	@Override
	public Document getMongoDBQuery(String key, Object value) {
		return new Document(key, value);
	}

	@Override
	public Bson prepareMongoQueryFromMultipleKeys(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return new Document();
		}
		List<Bson> filters = map.entrySet().stream()
				.map(e -> Filters.eq(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
		return filters.size() == 1 ? filters.get(0) : Filters.and(filters);
	}

	@Override
	public List<Document> getResultFromMongo(MongoCollection<Document> mongoCollection, Bson searchQuery) {
		List<Document> results = new ArrayList<>();
		mongoCollection.find(searchQuery).into(results);
		return results;
	}

	@Override
	public List<JSONObject> parseMongoDocuments(List<Document> documents) {
		List<JSONObject> jsonObjects = new ArrayList<>();
		if (documents == null) {
			return jsonObjects;
		}
		for (Document document : documents) {
			jsonObjects.add(new JSONObject(jsonHelper.convertToJson(document)));
		}
		return jsonObjects;
	}

	@Override
	public boolean findMongoDocumentAndDeleteIt(MongoClient client, String key, Object mongoValue, String dataBaseName,
			String collectionName) {
		try {
			MongoCollection<Document> collection = getCollection(client, dataBaseName, collectionName);
			DeleteResult result = collection.deleteMany(Filters.eq(key, mongoValue));
			boolean deleted = result.getDeletedCount() > 0;
			if (!deleted) {
				ReportHelper.logValidationFailure("MongoDeletion", "to delete", "not able to delete",
						"No document matched key=" + key + " in " + dataBaseName + "." + collectionName);
			}
			return deleted;
		} catch (Exception e) {
			log.error("Mongo delete failed for {}.{} key={}", dataBaseName, collectionName, key, e);
			ReportHelper.logValidationFailure("MongoDeletion", "to delete", "not able to delete",
					"Error deleting from " + dataBaseName + "." + collectionName + ": " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean updateMongoDocument(MongoClient client, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Object mongoNewUpdatedObject) {
		try {
			Document setFields = Document.parse(String.valueOf(mongoNewUpdatedObject));
			return applyUpdate(client, dataBaseName, collectionName, searchKey, searchValue, setFields, false);
		} catch (Exception e) {
			log.error("Mongo update (object) failed for {}.{}", dataBaseName, collectionName, e);
			ReportHelper.logValidationFailure("MongoUpdation", "to update", "not able to updated",
					"Error updating " + dataBaseName + "." + collectionName + ": " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean updateMongoDocument(MongoClient client, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Map<String, Object> mongoNewUpdatedObject) {
		return updateMongoDocuments(client, collectionName, dataBaseName, searchKey, searchValue, mongoNewUpdatedObject,
				false);
	}

	@Override
	public boolean updateMongoDocuments(MongoClient client, String collectionName, String dataBaseName, String searchKey,
			String searchValue, Map<String, Object> mongoNewUpdatedObject, boolean updateMultipleDocuments) {
		try {
			Document setFields = new Document(mongoNewUpdatedObject);
			return applyUpdate(client, dataBaseName, collectionName, searchKey, searchValue, setFields,
					updateMultipleDocuments);
		} catch (Exception e) {
			log.error("Mongo update (map) failed for {}.{}", dataBaseName, collectionName, e);
			ReportHelper.reporterLogging(false, "Error updating " + dataBaseName + "." + collectionName + ": " + e);
			return false;
		}
	}

	@Override
	public List<JSONObject> fetchDataFromMongoForMultipleKeys(MongoClient client, Map<String, String> map,
			String dataBaseName, String collectionName) {
		Bson query = prepareMongoQueryFromMultipleKeys(map);
		MongoCollection<Document> collection = getCollection(client, dataBaseName, collectionName);
		return parseMongoDocuments(getResultFromMongo(collection, query));
	}

	private boolean applyUpdate(MongoClient client, String dataBaseName, String collectionName, String searchKey,
			String searchValue, Document setFields, boolean updateMany) {
		if (setFields == null || setFields.isEmpty()) {
			return false;
		}
		MongoCollection<Document> collection = getCollection(client, dataBaseName, collectionName);
		Bson filter = Filters.eq(searchKey, searchValue);
		// Same semantics as legacy: { $set: { ...fields } }
		Bson update = new Document("$set", setFields);

		if (updateMany) {
			UpdateResult result = collection.updateMany(filter, update);
			return result.getModifiedCount() > 0 || result.getMatchedCount() > 0;
		}
		Document updated = collection.findOneAndUpdate(filter, update);
		return updated != null;
	}
}
