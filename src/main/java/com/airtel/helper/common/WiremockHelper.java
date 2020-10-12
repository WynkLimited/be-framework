package com.airtel.helper.common;

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.Reporter;
import com.airtel.dto.WiremockRecordStartRequest;
import com.airtel.dto.WiremockRecordStopResponse;
import com.airtel.dto.WiremockRequest;
import com.airtel.helper.constants.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class WiremockHelper extends ApiHelper {

	public Response response = null;

	private WiremockRequest.MockRequest prepareRequestParams(Map<String, Object> queryParams,
			Map<String, Object> requestHeaders, List<Map<String, Object>> bodyPatterns, String url, String httpMethod) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockRequest request = mock.setRequest(queryParams, requestHeaders, bodyPatterns, httpMethod);
		switch (httpMethod) {
		case "POST":
			request.setUrl(url);
			break;
		case "GET":
			request.setUrlPath(url);
			break;
		case "ANY":
			request.setUrlPath(url);
			break;
		default:
			request.setUrl(url);
			break;
		}
		return request;
	}

	private WiremockRequest.MockRequest prepareRequestParams(String url, String httpMethod) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockRequest request = mock.setRequest(httpMethod);
		switch (httpMethod) {
		case "POST":
			request.setUrl(url);
			break;
		case "GET":
			request.setUrlPath(url);
			break;
		case "ANY":
			request.setUrlPath(url);
			break;
		default:
			request.setUrl(url);
			break;
		}
		return request;
	}

	private WiremockRequest.MockResponse prepareResponseParams(Integer httpStatus, Map<String, Object> responseHeaders,
			String responseBody) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockResponse response = mock.setResponse(httpStatus, responseHeaders, responseBody);
		return response;
	}

	private WiremockRequest.MockResponse prepareResponseParams(String proxyBaseUrl) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockResponse response = mock.setResponse(proxyBaseUrl);
		return response;
	}

	private String prepareStubRequest(Integer priority, Map<String, Object> queryParams,
			Map<String, Object> requestHeaders, List<Map<String, Object>> bodyPatterns, String url, String httpMethod,
			Integer httpStatus, Map<String, Object> responseHeaders, String responseBody) {
		WiremockRequest request = new WiremockRequest();
		WiremockRequest.MockRequest mockRequest = prepareRequestParams(queryParams, requestHeaders, bodyPatterns, url,
				httpMethod);
		WiremockRequest.MockResponse mockResponse = prepareResponseParams(httpStatus, responseHeaders, responseBody);
		request.setPriority(priority);
		request.setRequest(mockRequest);
		request.setResponse(mockResponse);
		String wiremockRequest = convertToJson(request);
		return wiremockRequest;
	}

	public String createStubMapping(String mockUrl, Integer priority, Map<String, Object> queryParams,
			Map<String, Object> requestHeaders, List<Map<String, Object>> bodyPatterns, String requestUrl,
			String httpMethod, Integer httpStatus, Map<String, Object> responseHeaders, String responseBody,boolean isSave) {
		String ID = null;
		String request = prepareStubRequest(priority, queryParams, requestHeaders, bodyPatterns, requestUrl, httpMethod,
				httpStatus, responseHeaders, responseBody);
		try {
			response = postResponseWithHeaders(request, null, ContentType.JSON,
					mockUrl + Constants.WIREMOCK_MAPPINGS_CREATE);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : "
						+ response.getStatusCode());
				WiremockRequest responseDto = convertFromJson(response.asString(), WiremockRequest.class);
				ID = responseDto.getUuid();
			} else {
				Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(false);
			}
			if(isSave)
				saveStubMapping(mockUrl);
		} catch (Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
		return ID;
	}

	public void saveStubMapping(String mockUrl) {
		response = postResponse(null, ContentType.JSON, mockUrl + Constants.WIREMOCK_MAPPINGS_SAVE);
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			Reporter.log("API : [ saveStubMapping ] Stub Mapping Saved. || Status Code : " + response.getStatusCode());
		} else {
			Reporter.log(
					"API : [ saveStubMapping ] Stub Mapping not Created. || Status Code : " + response.getStatusCode());
			Assert.assertTrue(false);
		}
	}

	public void resetStubMappings(String mockUrl) {
		response = postResponse(null, ContentType.JSON, mockUrl + Constants.WIREMOCK_MAPPINGS_RESET);
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			Reporter.log(
					"API : [ resetStubMapping ] Reset Mapping Saved. || Status Code : " + response.getStatusCode());
		} else {
			Reporter.log("API : [ resetStubMapping ] Reset Mapping not Created. || Status Code : "
					+ response.getStatusCode());
			Assert.assertTrue(false);
		}
	}

	public String createStubMapping(String mockUrl, String request,boolean isSave) {
		String ID = null;
		try {
			response = postResponseWithHeaders(request, null, ContentType.JSON,
					mockUrl + Constants.WIREMOCK_MAPPINGS_CREATE);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : "
						+ response.getStatusCode());
				WiremockRequest responseDto = convertFromJson(response.asString(), WiremockRequest.class);
				ID = responseDto.getId();
			} else {
				Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(	false);
			}
			if(isSave)
				saveStubMapping(mockUrl);
		} catch (Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
		return ID;
	}

	private String prepareStubRequest(Integer priority, String httpMethod, String urlPathPattern, String proxyBaseUrl) {
		WiremockRequest request = new WiremockRequest();
		WiremockRequest.MockRequest mockRequest = prepareRequestParams(urlPathPattern, httpMethod);
		WiremockRequest.MockResponse mockResponse = prepareResponseParams(proxyBaseUrl);
		request.setPriority(priority);
		request.setRequest(mockRequest);
		request.setResponse(mockResponse);
		String wiremockRequest = convertToJson(request);
		return wiremockRequest;
	}

	public String createStubMapping(String mockUrl, Integer priority, String httpMethod, String urlPathPattern,
			String proxyBaseUrl,boolean isSave) {
		String ID = null;
		String request = prepareStubRequest(priority, httpMethod, urlPathPattern, proxyBaseUrl);
		try {
			response = postResponseWithHeaders(request, null, ContentType.JSON,
					mockUrl + Constants.WIREMOCK_MAPPINGS_CREATE);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : "
						+ response.getStatusCode());
				WiremockRequest responseDto = convertFromJson(response.asString(), WiremockRequest.class);
				ID = responseDto.getId();
			} else {
				Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(false);
			}
			if(isSave)
				saveStubMapping(mockUrl);
		} catch (Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
		return ID;
	}

	public WiremockRequest getSpecificStubMapping(String mockUrl, String stubId) {
		WiremockRequest responseDto = null;
		try {
			String url = mockUrl + Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = getResponse(null, url, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ getSpecificStubMapping ] get specific stub mapping. || Status Code : "
						+ response.getStatusCode());
				responseDto = convertFromJson(response.asString(), WiremockRequest.class);

			} else {
				Reporter.log("API : [ getSpecificStubMapping ] not able to get specific stub mapping. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(false);
			}
		} catch (Exception error) {
			Reporter.log("Error : [ getSpecificStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
		return responseDto;
	}

	public void modifySpecificStubMapping(String mockUrl, String request, String stubId) {
		try {
			String endpoint = Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = putResponse(request, ContentType.JSON, mockUrl + endpoint, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ modifySpecificStubMapping ] modify specific stub mapping. || Status Code : "
						+ response.getStatusCode());
				saveStubMapping(mockUrl);
			} else {
				Reporter.log(
						"API : [ modifySpecificStubMapping ] not able to modify specific stub mapping. || Status Code : "
								+ response.getStatusCode());
				Assert.assertTrue(false);
			}
		} catch (Exception error) {
			Reporter.log("Error : [ modifySpecificStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
	}

	public void deleteSpecificStubMapping(String mockUrl, String stubId) {
		try {
			String endpoint = Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = deleteResponseWithHeaders(null, null, mockUrl + endpoint, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ deleteSpecificStubMapping ] delete specific stub mapping. || Status Code : "
						+ response.getStatusCode());
			} else {
				Reporter.log(
						"API : [ deleteSpecificStubMapping ] not able to delete specific stub mapping. || Status Code : "
								+ response.getStatusCode());
				Assert.assertTrue(false);
			}
		} catch (Exception error) {
			Reporter.log("Error : [ deleteSpecificStubMapping ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
	}

	private String setRecordStartParams(String targetApiUrl) {
		WiremockRecordStartRequest recordStart = new WiremockRecordStartRequest();
		recordStart.setTargetBaseUrl(targetApiUrl);
		String request = convertToJson(recordStart);
		return request;
	}

	public void startWiremockRecording(String mockUrl, String targetApiUrl) {
		try {
			String url = mockUrl + Constants.RECORDSTART_WIREMOCK_MAPPING;
			response = postResponse(setRecordStartParams(targetApiUrl), ContentType.JSON, url);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ startWiremockRecording ] start wiremock recording. || Status Code : "
						+ response.getStatusCode());
			} else {
				Reporter.log("API : [ startWiremockRecording ] not able to start wiremock recording. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(false);
			}
		} catch (Exception error) {
			Reporter.log("Error : [ startWiremockRecording ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
	}

	public WiremockRecordStopResponse stopWiremockRecording(String mockUrl) {
		WiremockRecordStopResponse recordResponse = null;
		try {
			response = postResponse(null, ContentType.JSON, mockUrl + Constants.RECORDSTOP_WIREMOCK_MAPPING);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				recordResponse = convertFromJson(response.asString(), WiremockRecordStopResponse.class);
				Reporter.log("API : [ stopWiremockRecording ] stop wiremock recording. || Status Code : "
						+ response.getStatusCode());
			} else {
				Reporter.log("API : [ stopWiremockRecording ] not able to stop wiremock recording. || Status Code : "
						+ response.getStatusCode());
				Assert.assertTrue(false);
			}
		} catch (Exception error) {
			Reporter.log("Error : [ stopWiremockRecording ] ::: " + error.getMessage());
			Assert.assertTrue(false);
		}
		return recordResponse;
	}

}