package com.airtel.helper.common;

import java.sql.Connection;

import org.testng.Reporter;
import org.testng.annotations.AfterClass;

import com.aerospike.client.AerospikeClient;
import com.mongodb.MongoClient;

public class BaseTestClass {

	public static Connection connection = null;
	public static MongoClient mongoClient = null;
	public static AerospikeClient aeroSpikeClient = null;

	@AfterClass
	public void closeDBConnection() {
		try {
			if (connection != null)
				connection.close();
			if (mongoClient != null)
				mongoClient.close();
			if (aeroSpikeClient != null)
				aeroSpikeClient.close();
		} catch (Exception e) {
			Reporter.log("Exception In closing DB Connection In CommonBaseClass");
			e.getStackTrace();
		}
	}

}
