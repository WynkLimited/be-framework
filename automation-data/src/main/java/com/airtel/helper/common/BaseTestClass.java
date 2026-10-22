package com.airtel.helper.common;

import java.sql.Connection;

import com.airtel.helper.data.JsonParser;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;

import com.aerospike.client.AerospikeClient;
import com.mongodb.client.MongoClient;

public class BaseTestClass {

    public BaseTestClass() {
        jsonHelper = new JsonParser();
    }

    protected Connection connection = null;
    protected MongoClient mongoClient = null;
    protected AerospikeClient aeroSpikeClient = null;
    protected JsonParser jsonHelper = null;


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
