package com.airtel.helper.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
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

	/**
	 * This method will be used for getting response of GET request w/o headers.
	 * 
	 * @param queryParams
	 * @param url
	 * @return response
	 */
	public static Response getResponse(Map<String, String> queryParams, String url) {
		Response response = getResponseWithHeaders(queryParams, null, url);
		return response;
	}

	public static Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url) {

		RequestSpecification request = prepareGetRequest(queryParams).relaxedHTTPSValidation();
		Response response = null;
		if (headers != null && headers.size() == 0) {
			Headers requestHeader = new Headers(headers);
			request.headers(requestHeader);
		}
		response = request.get(url);
		long responseTime = response.getTime();

		if (queryParams != null) {
			logRequestResponse(queryParams.toString().replace(", ", "&"), response.asString(), url, responseTime);
		} else {
			logRequestResponse(null, response.asString(), url, responseTime);
		}

		if (response.getStatusCode() != 200 && response.getStatusCode() != 204) {
			ReportHelper.logValidationFailure("HTTP Status code not 200", "200",
					Integer.toString(response.getStatusCode()), "HTTP status check failure");
			Assert.assertTrue(false);
		}
		return response;
	}

	public static Response postResponse(String body, ContentType contentType, String url) {
		Response apiResp = postResponseWithHeaders(body, null, contentType, url);
		return apiResp;
	}

	public static Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType,
			String url) {
		RequestSpecification request = RestAssured.given().relaxedHTTPSValidation();
		Response apiResp = null;

		if (headers != null && headers.size() != 0) {
			Headers requestHeader = new Headers(headers);
			request.headers(requestHeader);
		}
		if (body != null && !body.isEmpty()) {
			request.body(body);
		}
		apiResp = request.contentType(contentType).post(url);
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

	public static String convertToJson(Object obj) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		String jsonData = gsonConverter.toJson(obj);
		return jsonData;
	}

	public static <T> T convertFromJson(String jsonData, Class<T> classType) {
		Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		T objFromJson = gsonConverter.fromJson(jsonData, classType);
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

	public static void main(String[] args) {

	}
}
