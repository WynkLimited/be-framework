package com.airtel.helper.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.airtel.helper.logging.AirtelLoggingMgr;
import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * This class contains methods for generic functions over the API that includes
 * fetching response for GET/POST Request, marshaling & unmarshalling between
 * object & XML/JSON.
 * 
 * @author b0202849
 */
public class ApiHelper {
	private static Logger apiHelperLog = AirtelLoggingMgr.getLogger(ApiHelper.class);
	private static ReportHelper reporter = new ReportHelper();
	private static FileHelper fileHelper = new FileHelper();

	private static RequestSpecification prepareGetRequest(Map<String, String> queryParams) {
		RequestSpecification request = RestAssured.given().redirects().follow(false);
		if (null != queryParams) {
			for (Map.Entry<String, String> keyValue : queryParams.entrySet()) {
				request.param(keyValue.getKey(), keyValue.getValue());
			}
		}
		return request;
	}

	/**
	 * This method will be used for getting response of GET request w/o headers.
	 * This method will always fail non 200 HTTP response.
	 * 
	 * @param queryParams
	 * @param url
	 * @return response
	 */
	public static Response getResponse(Map<String, String> queryParams, String url) {
		return getResponseWithHeaders(queryParams, null, url, true);
	}

	/**
	 * This method will be used for getting response of GET request w/o headers.
	 * This method can pass/fail non-200 HTTP status code based on checkStatus field
	 * value as false/true.
	 * 
	 * @param queryParams
	 * @param url
	 * @param checkStatus
	 * @return
	 */
	public static Response getResponse(Map<String, String> queryParams, String url, boolean checkStatus) {
		return getResponseWithHeaders(queryParams, null, url, checkStatus);
	}

	/**
	 * This method will be used for getting response of GET request with headers.
	 * This method can pass/fail non-200 HTTP status code based on checkStatus field
	 * value as false/true.
	 * 
	 * @param queryParams
	 * @param headers
	 * @param url
	 * @return
	 */
	public static Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url) {
		return getResponseWithHeaders(queryParams, headers, url, true);
	}

	/**
	 * This method will be used for getting response of GET request with headers.
	 * This method will fail non 200 HTTP response.
	 * 
	 * @param queryParams
	 * @param headers
	 * @param url
	 * @param checkStatus
	 * @return
	 */
	public static Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url,
			boolean checkStatus) {

		RequestSpecification request = prepareGetRequest(queryParams).relaxedHTTPSValidation();
		Response response = null;
		if (headers != null && !headers.isEmpty()) {
			Headers requestHeader = new Headers(headers);
			request.headers(requestHeader);
		}
		response = request.get(url);
		long responseTime = response.getTime();
		if (queryParams != null) {
			logRequestResponse(queryParams.toString().replace(", ", "&"), formatResponse(response), url, responseTime);
		} else {
			logRequestResponse(null, formatResponse(response), url, responseTime);
		}

		if (checkStatus && response.getStatusCode() != 200 && response.getStatusCode() != 204) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(response.getStatusCode()), "HTTP status check failure");
			Assert.assertTrue(false);
		}
		return response;
	}

	/**
	 * This method will be used for getting response of POST request w/o headers.
	 * This method will always fail non 200 response.
	 * 
	 * @param body
	 * @param contentType
	 * @param url
	 * @return
	 */
	public static Response postResponse(String body, ContentType contentType, String url) {
		return postResponseWithHeaders(body, null, contentType, url, true);
	}

	/**
	 * This method will be used for getting response of POST request w/o headers.
	 * This method can pass/fail non-200 HTTP status code based on checkStatus field
	 * value as false/true.
	 * 
	 * @param body
	 * @param contentType
	 * @param url
	 * @param checkStatus
	 * @return
	 */
	public static Response postResponse(String body, ContentType contentType, String url, boolean checkStatus) {
		return postResponseWithHeaders(body, null, contentType, url, checkStatus);
	}

	/**
	 * This method will be used for getting response of POST request with headers.
	 * This method will always fail non 200 response.
	 * 
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @return
	 */
	public static Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType,
			String url) {
		return postResponseWithHeaders(body, headers, contentType, url, true);
	}

	/**
	 * This method will be used for getting response of POST request with headers.
	 * This method can pass/fail non-200 HTTP status code based on checkStatus field
	 * value as false/true.
	 * 
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param checkStatus
	 * @return
	 */
	public static Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType,
			String url, boolean checkStatus) {
		RequestSpecification request = RestAssured.given().relaxedHTTPSValidation();
		Response apiResp = null;

		if (headers != null && !headers.isEmpty()) {
			Headers requestHeader = new Headers(headers);
			request.headers(requestHeader);
		}
		if (body != null && !body.isEmpty()) {
			request.body(body);
		}

		if (contentType != null)
			request.contentType(contentType);

		apiResp = request.post(url);
		long responseTime = apiResp.getTime();
		logRequestResponse(body, formatResponse(apiResp), url, responseTime);

		if (checkStatus && apiResp.getStatusCode() != 200) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(apiResp.getStatusCode()), "HTTP status check failure");
			Reporter.log("Response has failed with HTTP status code - " + apiResp.getStatusCode());
			Assert.assertTrue(false);
		}
		return apiResp;
	}

	private static void logRequestResponse(String request, String response, String url, long responseTime) {
		String requestFilePath = "";
		if (request != null)
			requestFilePath = fileHelper.createRequestJsonFile(request, "");

		String responseFilePath = fileHelper.createResponseJsonFile(response, "");
		String reportFolder = reporter.getResultFileStringPath();

		reporter.appendresultHTMLReport(reportFolder, url, "<a href='" + requestFilePath + "'>Request Data" + "</a>",
				"<a href='" + responseFilePath + "'>Response Data" + "</a>",
				"Response Time(in msec) :- " + String.valueOf(responseTime), "", "");
	}

	/**
	 * This method convert a particular object to JSON. This is mostly used for
	 * comparator.
	 * 
	 * @param obj
	 * @return
	 */
	public static String convertToJson(Object obj) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		return gsonConverter.toJson(obj);
	}

	/**
	 * This method convert a json string to a particular DTO type specified as
	 * input. This method returns object of a generic type and therefore casting is
	 * not required with this method.
	 * 
	 * @param jsonData
	 * @param classType
	 * @return Object of type specified as input class
	 */
	public static <T> T convertFromJson(String jsonData, Class<T> classType) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		return gsonConverter.fromJson(jsonData, classType);
	}
	
	/**
	 * This method will be used to convert a dynamic class response from JSON
	 * @author b0201347
	 * @param jsonData
	 * @param type
	 * @return
	 */
	public static <T> T convertFromJson(String jsonData, Type type) {
	    Gson gsonConverter = (new GsonBuilder()).disableHtmlEscaping().setPrettyPrinting().create();
	    return gsonConverter.fromJson(jsonData, type);
	}

	/**
	 * Returns list of object converted from a JSON array string. Input of this
	 * method is the Type of list and JSON Array string.
	 * 
	 * @param jsonArray
	 * @param listType
	 * @return
	 */
	public static <T> List<T> convertFromJsonArray(String jsonArray, Type listType) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		return gsonConverter.fromJson(jsonArray, listType);
	}

	/**
	 * Method to fetch logs from ES for a particular request ID. This method will be
	 * used before comparison of logs in ES & api response. Collection name will be
	 * the name of es logs collections where logs will be present.
	 * 
	 * @param server
	 * @param collectionName
	 * @param keyName
	 * @param value
	 * @return
	 */
	public static String fetchEsLogsResponse(String server, String collectionName, String keyName, String value) {
		StringBuilder urlBuilder = new StringBuilder();
		String url = urlBuilder.append("http://").append(server).append(":9200/").append(collectionName)
				.append("_search").toString();
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("q", keyName + ":" + value);
		Response esResponse = getResponse(queryParams, url);
		return esResponse.asString();
	}

	/**
	 * Format JSON and pretty print the response
	 * 
	 * @param apiResponse
	 * @return
	 */
	private static String formatResponse(Response apiResponse) {
		String responseString = null;

		if (apiResponse.contentType().contains(ContentType.JSON.toString()) && apiResponse.asString() != null) {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(apiResponse.asString());
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			if (jsonElement instanceof JsonObject) {
				JsonObject json = jsonElement.getAsJsonObject();				
				responseString = gson.toJson(json);
			} else if (jsonElement instanceof JsonArray) {
				JsonArray json = jsonElement.getAsJsonArray();
				responseString = gson.toJson(json);
			}
		} else
			responseString = apiResponse.asString();
		return responseString;
	}

	public static String convertToXml(Object obj) {
		String xmlData = null;
		JAXB.marshal(obj, xmlData);
		return xmlData;
	}

	public static Object convertFromXml(String xmlData, Class<?> classType) {
		Object objFromXml = null;
		objFromXml = JAXB.unmarshal(xmlData, classType);
		return objFromXml;
	}

	/**
	 * This method helps in retrieving value of a particular property in a property
	 * file.
	 * 
	 * @param filePath
	 * @param keyName
	 * @return
	 */
	public static String getValueOfProperty(String filePath, String keyName) {
		try {
			Properties prop = new Properties();
			InputStream inpStream = null;
			inpStream = new FileInputStream(filePath);
			prop.load(inpStream);
			return prop.getProperty(keyName);
		} catch (IOException e) {
			apiHelperLog.log(Level.ERROR,
					"Filepath mentioned is not correct and method breaks with exception " + e.getStackTrace());
			return null;
		}
	}

	/**
	 * This method retrieves name of all properties present in a property file.
	 * 
	 * @param filePath
	 * @return Set<String> - containing all properties
	 */
	public static Set<String> getAllProperty(String filePath) {
		try {
			Properties prop = new Properties();
			InputStream inpStream = null;
			inpStream = new FileInputStream(filePath);
			prop.load(inpStream);
			return prop.stringPropertyNames();
		} catch (IOException e) {
			Reporter.log("Filepath mentioned is not correct and method breaks with exception " + e);
			return null;
		}
	}

	/**
	 * This method will load all properties and it's value to a map.
	 * 
	 * @param filePath
	 * @return
	 */
	public static Map<String, String> fetchAllProperties(String filePath) {
		Map<String, String> propData = new HashMap<>();
		Set<String> propertyName = getAllProperty(filePath);
		for (String prop : propertyName) {
			propData.put(prop, Optional.ofNullable(getValueOfProperty(filePath, prop)).orElse(null));
		}
		return propData;
	}

	/**
	 * This method is used to set a property in a property file.
	 * 
	 * @param filePath
	 * @param keyName
	 * @param value
	 */
	public static void setValueOfProperty(String filePath, String keyName, String value) {
		try {
			Properties prop = new Properties();

			FileInputStream in = new FileInputStream(filePath);
			prop.load(in);
			in.close();

			OutputStream outStream = null;
			outStream = new FileOutputStream(filePath);
			prop.setProperty(keyName, value);
			prop.store(outStream, null);
			outStream.close();
		} catch (IOException e) {
			Reporter.log("Filepath mentioned is not correct and method breaks with exception " + e);
		}
	}

	public static boolean validateString(String strVal) {
		return (strVal != null && !strVal.trim().isEmpty());
	}

	public static boolean verifyObject(JSONObject responseObj, String esFieldName) {
		if (responseObj.get(esFieldName) != null) {
			ReportHelper.logValidationFailure("ES LOG " + esFieldName, "object", "null", "ES LOG Field Found Null");
			return false;
		}
		return true;
	}

	public boolean isStringFeildNotBlankInsideJSONObject(JSONObject jsonObject, String feild) {

		return (!jsonObject.getString(feild).isEmpty() && jsonObject.getString(feild) != null);
	}

	public boolean isJsonObjectInsideJsonObjectNotBlank(JSONObject jsonObject, String feild) {
		return (jsonObject.getJSONObject(feild) != null && jsonObject.getJSONObject(feild).length() != 0);
	}

	public boolean isJsonArrayNotBlank(JSONArray jsonArray) {
		return ((jsonArray != null) && jsonArray.length() > 0);

	}

	public boolean isListFieldBlank(JSONObject jsonObject, String feild) {
		return ((jsonObject.getJSONArray(feild).length() != 0) && (jsonObject.getJSONArray(feild) != null));
	}

	/**
	 * Returns a generated URL based on the input parameters. This will be used by
	 * the method that fetch data for a particular API.
	 * 
	 * @param configFile
	 * @param serverInitials
	 * @param endpoint
	 * @return
	 */
	public static String generateAPIUrl(String configFile, String serverInitials, String endpoint) {
		String serverIp = getValueOfProperty(configFile, serverInitials);
		String serviceUrl;
		if (endpoint != null)
			serviceUrl = serverIp + endpoint;
		else
			serviceUrl = serverIp;
		return serviceUrl;
	}

	public static String[] getCommaSeperatedValues(String value) {
		return value.split(",");
	}

	public static boolean validateObject(Object obj) {
		return (obj != null && obj != "");
	}

	public static String getValueFromXLResponse(String xmlResponse, String tagName, int index) {
		String value = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		InputSource inputSource;
		try {
			documentBuilder = factory.newDocumentBuilder();
			inputSource = new InputSource(new StringReader(xmlResponse));
			Document doc = documentBuilder.parse(inputSource);
			NodeList list = doc.getElementsByTagName(tagName);
			value = list.item(index).getTextContent();
		} catch (Exception e) {
			ReportHelper.logValidationFailure("xml response parse failure", "attribute Value", e.getMessage(),
					"Exception In  XML Response");

		}
		return value;
	}
}
