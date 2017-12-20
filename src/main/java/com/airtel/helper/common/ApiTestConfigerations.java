package com.airtel.helper.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.testng.annotations.BeforeSuite;

import io.restassured.RestAssured;

/**
 * Created by b0200202 on 11/16/17.
 */
public class ApiTestConfigerations {

    static Properties properties = null;


    @BeforeSuite
    public void beforeSuite() {

        String file_name = null;

        System.out.println("Environment is >> "+System.getProperty("ENVIRONMENT"));
        String env_name = System.getProperty("ENVIRONMENT", "preprod");

        properties = new Properties();

        FileInputStream inputStream;

        try {
            if(env_name.equalsIgnoreCase("PROD"))
                file_name = "config/config_prod.properties";
            else if(env_name.equalsIgnoreCase("PREPROD"))
                file_name = "config/config_preprod.properties";
            else file_name = "config/config_test.properties";

            inputStream = new FileInputStream(file_name);
            properties.load(inputStream);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void configure(String key_start_name) {
        RestAssured.reset();

        Set<String> keys = properties.stringPropertyNames();

        for(String key : keys) {
            if(key.startsWith(key_start_name) && key.endsWith("IP"))
                RestAssured.baseURI = properties.getProperty(key);
            else if(key.startsWith(key_start_name) && key.endsWith("PORT"))
                RestAssured.port = Integer.parseInt(properties.getProperty(key));
//            else if(key.startsWith(key_start_name) && key.endsWith("BASE_PATH"))
//                RestAssured.basePath = properties.getProperty(key);
        }

    }

}
