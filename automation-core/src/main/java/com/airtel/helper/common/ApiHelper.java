package com.airtel.helper.common;

import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.testng.Assert;
import com.airtel.Models.LogItemReq;
import com.airtel.helper.constants.RequestType;
import com.airtel.helper.data.DataHelper;
import com.airtel.helper.report.FileHelper;
import com.airtel.helper.report.ReportHelper;
import com.report.SentinelLogger;

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

public class ApiHelper extends DataHelper {

	private RequestSpecification prepareRequestParams(Map<String, String> queryParams, List<Header> headers,
			String body, ContentType contentType, Map<String, String> formParams) {
		RequestSpecification request = RestAssured.given().relaxedHTTPSValidation().redirects().follow(false);
		if (null != queryParams) {
			request.queryParams(queryParams);
		}

		if (null != formParams) {
			request.formParams(formParams);
		}

		if (headers != null)
			request.headers(new Headers(headers));
		if (body != null && !body.isEmpty()) {
			request.body(body);
		}
		if (contentType != null)
			request.contentType(contentType);

		return request;
	}

	/**
	 * Generic method to fetch response for different type of API request
	 *
	 * @param requestUrl
	 * @param requestType
	 * @param body
	 * @param queryParams
	 * @param headers
	 * @param checkStatus
	 * @return
	 */
	public Response fetchApiResponse(String requestUrl, String requestType, String body,
			Map<String, String> queryParams, List<Header> headers, ContentType contentType, boolean checkStatus,
			Map<String, String> formParams) {

		LogItemReq logItemReq = null;
		Response apiResponse = null;
		String requestLog = null;
		String queryParamLog = "";
		RequestSpecification apiRequest = prepareRequestParams(queryParams, headers, body, contentType, formParams);

		requestLog = "Request body: " + body;

		String headerData = "";

		if (headers != null) {
			for (Header h : headers) {
				headerData = headerData + h.getName() + " : " + h.getValue();
			}
		}

		if (queryParams != null) {
            requestLog = requestLog + "Query Params:" + queryParams.toString();
            queryParamLog = queryParams.toString();
        }
		try {
			switch (RequestType.valueOf(requestType.toUpperCase())) {
			case GET:
				apiResponse = apiRequest.get(requestUrl);
				break;
			case POST:
				apiResponse = apiRequest.post(requestUrl);
				break;
			case PUT:
				apiResponse = apiRequest.put(requestUrl);
				break;
			case PATCH:
				apiResponse = apiRequest.patch(requestUrl);
				break;
			case DELETE:
				apiResponse = apiRequest.delete(requestUrl);
				break;
			default:
				apiResponse = apiRequest.head(requestUrl);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logRequestResponse(requestLog, null, requestUrl, 0, headerData);
			ReportHelper.reporterLogging(false, "" + e.getMessage());
			logItemReq = new LogItemReq();
			logItemReq.setHeaders(headerData);
			logItemReq.setTargetApiUrl(requestUrl);
			logItemReq.setResStatusCode(0);
			logItemReq.setMessage(null);
			logItemReq.setRequest(body);
			logItemReq.setQueryParams(queryParamLog);
			SentinelLogger.info(logItemReq);
			return apiResponse;
		}

		long responseTime = apiResponse.getTime();
		String resString = formatResponse(apiResponse);
		logRequestResponse(requestLog, resString, requestUrl, responseTime, headerData);

		logItemReq = new LogItemReq();
		logItemReq.setHeaders(headerData);
		logItemReq.setTargetApiUrl(requestUrl);
		logItemReq.setResStatusCode(apiResponse.statusCode());
		logItemReq.setMessage(resString);
		logItemReq.setRequest(body);
        logItemReq.setQueryParams(queryParamLog);
		SentinelLogger.info(logItemReq);
		if (checkStatus && ! (com.airtel.helper.constants.Constants.SUCCESS_CODE.contains(apiResponse.getStatusCode())))
		{
			ReportHelper.logValidationFailure("HTTP Status code is not in 2XX series. ", "2xx",
					Integer.toString(apiResponse.getStatusCode()), "HTTP status check failure");
			Assert.assertTrue(false);
		}
		return apiResponse;
	}

	/**
	 * This method will be used for getting response of GET request w/o headers.
	 * This method will always fail non 200 HTTP response.
	 *
	 * @param queryParams
	 * @param url
	 * @return response
	 */
	public Response getResponse(Map<String, String> queryParams, String url) {
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
	public Response getResponse(Map<String, String> queryParams, String url, boolean checkStatus) {
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
	public Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url) {
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
	public Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url,
			boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "GET", null, queryParams, headers, null, checkStatus, formParams);
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
	public Response getResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url,
			boolean checkStatus) {
		return fetchApiResponse(url, "GET", null, queryParams, headers, null, checkStatus, null);
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
    public Response deleteResponseWithHeaders(Map<String, String> queryParams, List<Header> headers, String url,
                                                     boolean checkStatus)
    {
        return fetchApiResponse(url, "DELETE", null, queryParams, headers, null, checkStatus,null);
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
	public Response postResponse(String body, ContentType contentType, String url) {
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
	public Response postResponse(String body, ContentType contentType, String url, boolean checkStatus) {
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
	public Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url) {
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
	public Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			boolean checkStatus) {
		return fetchApiResponse(url, "POST", body, null, headers, contentType, checkStatus, null);
	}

	/**
	 * This method will be used for getting response of POST request with headers
	 * with query params This method can pass/fail non-200 HTTP status code based on
	 * checkStatus field value as false/true.
	 *
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param queryParams
	 * @param checkStatus
	 * @return
	 */
	public Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus) {
		return fetchApiResponse(url, "POST", body, queryParams, headers, contentType, checkStatus, null);
	}

	/**
	 * This method will be used for getting response of POST request with headers
	 * with query params This method can pass/fail non-200 HTTP status code based on
	 * checkStatus field value as false/true.
	 *
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param queryParams
	 * @param checkStatus
	 * @return
	 */
	public Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "POST", body, queryParams, headers, contentType, checkStatus, formParams);
	}

	/**
	 * PATCH without headers. Fails on non-2xx when status check is enabled (default).
	 */
	public Response patchResponse(String body, ContentType contentType, String url) {
		return patchResponseWithHeaders(body, null, contentType, url, true);
	}

	/**
	 * PATCH without headers. Pass/fail non-2xx based on {@code checkStatus}.
	 */
	public Response patchResponse(String body, ContentType contentType, String url, boolean checkStatus) {
		return patchResponseWithHeaders(body, null, contentType, url, checkStatus);
	}

	/**
	 * PATCH with headers. Fails on non-2xx.
	 */
	public Response patchResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url) {
		return patchResponseWithHeaders(body, headers, contentType, url, true);
	}

	/**
	 * PATCH with headers. Pass/fail non-2xx based on {@code checkStatus}.
	 */
	public Response patchResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			boolean checkStatus) {
		return fetchApiResponse(url, "PATCH", body, null, headers, contentType, checkStatus, null);
	}

	/**
	 * PATCH with headers and query params. Pass/fail non-2xx based on {@code checkStatus}.
	 */
	public Response patchResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus) {
		return fetchApiResponse(url, "PATCH", body, queryParams, headers, contentType, checkStatus, null);
	}

	/**
	 * PATCH with headers, query params and form params. Pass/fail non-2xx based on {@code checkStatus}.
	 */
	public Response patchResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "PATCH", body, queryParams, headers, contentType, checkStatus, formParams);
	}

	/**
	 * PATCH with headers and expected status code (exact match).
	 */
	public Response patchResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			int statusCode, Map<String, String> formParams) {
		return fetchApiResponse(url, "PATCH", body, null, headers, contentType, statusCode, formParams);
	}

	/**
	 * PATCH with query params, headers and form params. Pass/fail non-2xx based on {@code checkStatus}.
	 */
	public Response patchResponseWithHeaders(String body, Map<String, String> queryParams, List<Header> headers,
			ContentType contentType, String url, boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "PATCH", body, queryParams, headers, contentType, checkStatus, formParams);
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
	public Response putResponse(String body, ContentType contentType, String url) {
		return putResponseWithHeaders(body, null, contentType, url, true);
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
	public Response putResponse(String body, ContentType contentType, String url, boolean checkStatus) {
		return putResponseWithHeaders(body, null, contentType, url, checkStatus);
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
	public Response putResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url) {
		return putResponseWithHeaders(body, headers, contentType, url, true);
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
	public Response putResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			boolean checkStatus) {
		return fetchApiResponse(url, "PUT", body, null, headers, contentType, checkStatus, null);
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
	 * @param formParams
	 * @return
	 */
	public Response putResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "PUT", body, null, headers, contentType, checkStatus, formParams);
	}

	/**
	 * This method will be used for getting response of POST request with headers
	 * with query params This method can pass/fail non-200 HTTP status code based on
	 * checkStatus field value as false/true.
	 *
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param queryParams
	 * @param checkStatus
	 * @return
	 */
	public Response putResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus) {
		return fetchApiResponse(url, "PUT", body, queryParams, headers, contentType, checkStatus, null);
	}

	/**
	 * This method will be used for getting response of POST request with headers
	 * with query params This method can pass/fail non-200 HTTP status code based on
	 * checkStatus field value as false/true.
	 *
	 * @param body
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param queryParams
	 * @param checkStatus
	 * @return
	 */
	public Response putResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			Map<String, String> queryParams, boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "PUT", body, queryParams, headers, contentType, checkStatus, formParams);
	}

	private void logRequestResponse(String request, String response, String url, long responseTime, String headers) {
		String requestFilePath = "";
		if (request != null)
			requestFilePath = FileHelper.createRequestJsonFile(request, "");
		String responseFilePath = FileHelper.createResponseJsonFile(response, "");
		String reportFolder = ReportHelper.getResultFileStringPath();

		ReportHelper.appendresultHTMLReport(reportFolder, url, "<a href='" + requestFilePath + "'>Request Data" + "</a>",
				"<a href='" + responseFilePath + "'>Response Data" + "</a>",
				"Response Time(in msec) :- " + String.valueOf(responseTime), "", "", headers);

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
	public String fetchEsLogsResponse(String server, String collectionName, String keyName, String value) {
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
	private String formatResponse(Response apiResponse) {
		String responseString = null;
		if (apiResponse.contentType().contains(ContentType.JSON.toString()) && apiResponse.asString() != null) {
			responseString = formatResponse(apiResponse.asString());
			if (null == responseString && apiResponse.getHeaders().hasHeaderWithName("googleCookie")) {
				String decryptResponse = decryptAPIResponse(apiResponse.getHeader("googleCookie"),
						apiResponse.asString());
				responseString = decryptResponse;
			}
		} else
			responseString = apiResponse.asString();
		return responseString;

	}

	/**
	 * This method decrpyts api response on the basis of googleCookie in Response
	 * header
	 *
	 * @param key
	 * @param encrypted
	 * @return
	 */
	public String decryptAPIResponse(String key, String encrypted) {
		try {
			KeySpec keySpec = new DESKeySpec(key.getBytes());
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String encryptedData = encrypted.replaceAll("\"", "");
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
			return new String(original);
		} catch (Exception e) {

			ReportHelper.reporterLogging(false,
					"Error in decrypting API response. Key = " + key + " Response = " + encrypted);
			return "";
		}
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
	 * @param statusCode
	 * @return
	 */
	public Response postResponseWithHeaders(String body, List<Header> headers, ContentType contentType, String url,
			int statusCode, Map<String, String> formParams) {
		return fetchApiResponse(url, "POST", body, null, headers, contentType, statusCode, formParams);
	}

	/**
	 * Generic method to fetch response for different type of API request
	 *
	 * @param requestUrl
	 * @param requestType
	 * @param body
	 * @param queryParams
	 * @param headers
	 * @param statusCode
	 * @return
	 */
	public Response fetchApiResponse(String requestUrl, String requestType, String body,
			Map<String, String> queryParams, List<Header> headers, ContentType contentType, int statusCode,
			Map<String, String> formParams) {

		Response apiResponse = null;
		String requestLog = null;
		String queryParamsLog = "";
		RequestSpecification apiRequest = prepareRequestParams(queryParams, headers, body, contentType, formParams);

		requestLog = "Request body: " + body;

		if (queryParams != null) {
            requestLog = requestLog + "Query Params:" + queryParams.toString();
            queryParamsLog=queryParams.toString();
        }

		String headerData = null;
		if (headers != null) {
			headerData = "";
			for (Header h : headers) {
				headerData = headerData + h.getName() + " : " + h.getValue();
			}
		}

		switch (requestType) {
		case "GET":
			apiResponse = apiRequest.get(requestUrl);
			break;
		case "POST":
			apiResponse = apiRequest.post(requestUrl);
			break;
		case "PUT":
			apiResponse = apiRequest.put(requestUrl);
			break;
		case "PATCH":
			apiResponse = apiRequest.patch(requestUrl);
			break;
		case "DELETE":
			apiResponse = apiRequest.delete(requestUrl);
			break;
		default:
			apiResponse = apiRequest.head(requestUrl);
			break;
		}

		long responseTime = apiResponse.getTime();
		logRequestResponse(requestLog, formatResponse(apiResponse), requestUrl, responseTime, headerData);

		LogItemReq logItemReq = new LogItemReq();
		logItemReq.setHeaders(headerData);
		logItemReq.setTargetApiUrl(requestUrl);
		logItemReq.setResStatusCode(apiResponse.statusCode());
		logItemReq.setMessage(formatResponse(apiResponse));
		logItemReq.setRequest(body);
		logItemReq.setQueryParams(queryParamsLog);
		SentinelLogger.info(logItemReq);

		if (apiResponse.getStatusCode() != statusCode) {
			ReportHelper.logValidationFailure("HTTP Status code not " + Integer.toString(statusCode),
					Integer.toString(statusCode), Integer.toString(apiResponse.getStatusCode()),
					"HTTP status check failure");
			Assert.assertTrue(false);
		}
		return apiResponse;
	}

	/**
	 * This method will be used for getting response of POST request with headers
	 * and query parameters. This method can pass/fail non-200 HTTP status code
	 * based on checkStatus field value as false/true.
	 *
	 * @param body
	 * @param queryParams
	 * @param headers
	 * @param contentType
	 * @param url
	 * @param checkStatus
	 * @return
	 */
	public Response postResponseWithHeaders(String body, Map<String, String> queryParams, List<Header> headers,
			ContentType contentType, String url, boolean checkStatus, Map<String, String> formParams) {
		return fetchApiResponse(url, "POST", body, queryParams, headers, contentType, checkStatus, formParams);
	}

}