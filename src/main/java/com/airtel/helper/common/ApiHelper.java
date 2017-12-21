package com.airtel.helper.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * This class contains methods for generic functions over the API that includes
 * fetching response for GET/POST Request, marshalling & unmarshalling between
 * object & XML/JSON.
 * 
 * @author b0202849
 */
public class ApiHelper {
	private static ReportHelper reporter = new ReportHelper();
	private static FileHelper fileHelper = new FileHelper();

	private static RequestSpecification prepareGetRequest(Map<String, String> queryParams) {
		RequestSpecification request = RestAssured.given();
		if (null != queryParams) {
			for (Map.Entry<String, String> keyValue : queryParams.entrySet()) {
				request.param(keyValue.getKey(), keyValue.getValue());
			}
		}
		return request;
	}

	public static Response getResponse(Map<String, String> queryParams, String url) {
		RequestSpecification request = prepareGetRequest(queryParams);
		Response response = request.get(url);
		long responseTime = response.getTime();
		logRequestResponse(queryParams.toString().replace(", ", "&"), response.asString(), url, responseTime);
		if (response.getStatusCode() != 200) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(response.getStatusCode()), "HTTP status check failure");
			Reporter.log("Response has failed with HTTP status code - " + response.getStatusCode()
					+ " & Response Message -" + response.asString());
			Assert.assertTrue(false);
		}
		return response;
	}

	public static Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url) {
		Headers requestHeader = new Headers(headers);
		RequestSpecification request = prepareGetRequest(queryParams);

		Response response = request.relaxedHTTPSValidation().headers(requestHeader).get(url);
		long responseTime = response.getTime();
		logRequestResponse(queryParams.toString().replace(", ", "&"), response.asString(), url, responseTime);
		if (response.getStatusCode() != 200 && response.getStatusCode() != 204) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(response.getStatusCode()), "HTTP status check failure");
			Reporter.log("Response has failed with HTTP status code - " + response.getStatusCode()
					+ " & Response Message -" + response.asString());
			Assert.assertTrue(false);
		}
		return response;
	}

	public static Response postResponse(String body, String contentType, String url) {
		Response apiResp = RestAssured.given().contentType(contentType).body(body).post(url);
		long responseTime = apiResp.getTime();
		logRequestResponse(body, apiResp.asString(), url, responseTime);

		if (apiResp.getStatusCode() != 200) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(apiResp.getStatusCode()), "HTTP status check failure");
			Reporter.log("Response has failed with HTTP status code - " + apiResp.getStatusCode());
			Assert.assertTrue(false);
		}
		return apiResp;
	}

	public static Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType,
			String url) {
		Headers requestHeader = new Headers(headers);

		Response apiResp = RestAssured.given().relaxedHTTPSValidation().headers(requestHeader).contentType(contentType)
				.body(body).post(url);
		long responseTime = apiResp.getTime();
		System.out.println("Api response time ==>" + responseTime);
		logRequestResponse(body, apiResp.asString(), url, responseTime);
		if (apiResp.getStatusCode() != 200) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(apiResp.getStatusCode()), "HTTP status check failure");
			Reporter.log("Response has failed with HTTP status code - " + apiResp.getStatusCode());
			Assert.assertTrue(false);
		}
		return apiResp;
	}

	private static void logRequestResponse(String request, String response, String url, long responseTime) {
		try {
			String requestFilePath = fileHelper.createRequestJsonFile(request, "");
			String responseFilePath = fileHelper.createResponseJsonFile(response, "");

			reporter.appendresultHTMLReport(reporter.getResultFileStringPath(), url,
					"<a href='" + requestFilePath + "'>Request Data" + "</a>",
					"<a href='" + responseFilePath + "'>Response Data" + "</a>",
					"Response Time(in msec) :- " + String.valueOf(responseTime), "", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param requestId
	 *            - This is used to get data for a particular request
	 * @param waitTime
	 *            - This is used for specifying the wait time so that complete data
	 *            is populated in ES
	 */
	public static Response getEsLogsData(String requestId, int waitTime) {
		try {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("id", requestId);
			Thread.sleep(waitTime);
			return getResponse(queryParams, "data");

		} catch (InterruptedException e) {
			Reporter.log("Unable to wait for thread(ES logs not formed)");
		}
		return null;
	}

	public static String convertToJson(Object obj) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		String jsonData = gsonConverter.toJson(obj);
		return jsonData;
	}

	public static Object convertFromJson(String jsonData, Class<?> classType) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

		Object objFromJson = gsonConverter.fromJson(jsonData, classType);
		return objFromJson;
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

	public static String getValueOfProperty(String filePath, String keyName) {
		try {
			Properties prop = new Properties();
			InputStream inpStream = null;
			inpStream = new FileInputStream(filePath);
			prop.load(inpStream);
			return prop.getProperty(keyName);
		} catch (IOException e) {
			Reporter.log("Filepath mentioned is not correct and method breaks with exception " + e);
			return null;
		}
	}

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

	public static boolean validateString(String strVal) {
		if (strVal != null && !strVal.trim().isEmpty())
			return true;
		return false;
	}

	public static boolean verifyObject(JSONObject responseObj, String esFieldName) {
		if (responseObj.get(esFieldName) != null) {
			Reporter.log("<br/><font size='2'>ES Logs field " + esFieldName + " is null.</font>");
			return false;
		}
		return true;
	}

	public boolean isStringFeildNotBlankInsideJSONObject(JSONObject jsonObject, String feild) {

		if (!jsonObject.getString(feild).isEmpty() && jsonObject.getString(feild) != null)
			return true;
		else
			return false;
	}

	public boolean isJsonObjectInsideJsonObjectNotBlank(JSONObject jsonObject, String feild) {

		if (jsonObject.getJSONObject(feild) != null && jsonObject.getJSONObject(feild).length() != 0)
			return true;
		else
			return false;

	}

	public boolean isJsonArrayNotEmpty(JSONArray jsonArray) {

		if (jsonArray.length() != 0)
			return true;
		else
			return false;

	}

	public boolean isJsonArrayNotBlank(JSONArray jsonArray) {

		if (!jsonArray.equals(null) && jsonArray.length() > 0)
			return true;
		else
			return false;

	}

	public boolean isListFieldBlank(JSONObject jsonObject, String feild) {

		if ((jsonObject.getJSONArray(feild).length() != 0) && jsonObject.getJSONArray(feild) != null)
			return true;
		else
			return false;
	}

	public static String generateAPIUrl(String configFile, String serverInitials, String endpoint) {
		String serverIp = getValueOfProperty(configFile, serverInitials);
		String serviceUrl = serverIp + endpoint;
		return serviceUrl;
	}

	public static String[] getCommaSeperatedValues(String value) {
		return value.split(",");
	}

	public static boolean validateObject(Object obj) {
		if (obj != null && obj != "")
			return true;
		return false;
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
			System.out.println(list.item(index).getTextContent());
			value = list.item(index).getTextContent();
		} catch (ParserConfigurationException e) {
			Reporter.log("Exception In processing XML Response");
		} catch (SAXException e) {
			Reporter.log("Exception In processing XML Response");
		} catch (IOException e) {
			Reporter.log("Exception In processing XML Response");
		}
		return value;
	}

}
