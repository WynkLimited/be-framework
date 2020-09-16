package com.airtel.helper.common;

import com.airtel.dto.*;
import java.util.*;
import com.airtel.helper.constants.Constants;
import com.google.common.net.HttpHeaders;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import io.restassured.response.Response;
import com.github.tomakehurst.wiremock.WireMockServer;


public class WiremockHelper extends ApiHelper {

	public Response response = null;
	private WireMockServer wireMockServer;

	private WiremockRequest.MockRequest prepareRequestParams(Map<String, Object> queryParams, Map<String, Object> requestHeaders,
															 List<Map<String, Object>> bodyPatterns, String url, String httpMethod) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockRequest request = mock.setRequest(queryParams, requestHeaders,
				bodyPatterns, httpMethod);
		switch (httpMethod) {
			case "POST":
				request.setUrl(url);
				break;
			case "GET":
				request.setUrlPath(url);
				break;
		}
		return request;
	}


	private WiremockRequest.MockResponse prepareResponseParams(Integer httpStatus, Map<String, Object> responseHeaders, String responseBody) {
		WiremockRequest mock = new WiremockRequest();
		WiremockRequest.MockResponse response = mock.setResponse(httpStatus, responseHeaders, responseBody);
		return response;
	}

	private String prepareStubRequest(Integer priority, Map<String, Object> queryParams, Map<String, Object> requestHeaders,
									  List<Map<String, Object>> bodyPatterns, String url, String httpMethod, Integer httpStatus, Map<String, Object> responseHeaders, String responseBody) {
		WiremockRequest request = new WiremockRequest();
		WiremockRequest.MockRequest mockRequest = prepareRequestParams(queryParams, requestHeaders, bodyPatterns, url, httpMethod);
		WiremockRequest.MockResponse mockResponse = prepareResponseParams(httpStatus, responseHeaders, responseBody);
		request.setPriority(priority);
		request.setRequest(mockRequest);
		request.setResponse(mockResponse);
		String wiremockRequest = convertToJson(request);
		return wiremockRequest;
	}


	public String createStubMapping(String mockUrl, Integer priority, Map<String, Object> queryParams, Map<String, Object> requestHeaders,
									List<Map<String, Object>> bodyPatterns, String requestUrl, String httpMethod, Integer httpStatus, Map<String, Object> responseHeaders, String responseBody) {
		String ID = null;
		String request = prepareStubRequest(priority, queryParams, requestHeaders, bodyPatterns, requestUrl, httpMethod, httpStatus, responseHeaders, responseBody);
		try {
			response = postResponseWithHeaders(request, null, ContentType.JSON, mockUrl + Constants.WIREMOCK_MAPPINGS_CREATE);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : " + response.getStatusCode());
					WiremockRequest responseDto = convertFromJson(response.asString(), WiremockRequest.class);
					ID = responseDto.getId();
			} else {
				Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage());
		}
		return ID;
	}

	public void saveStubMapping(String mockUrl)
	{
		response = postResponse(null,ContentType.JSON,mockUrl+Constants.WIREMOCK_MAPPINGS_SAVE);
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			Reporter.log("API : [ saveStubMapping ] Stub Mapping Saved. || Status Code : " + response.getStatusCode());
		} else {
			Reporter.log("API : [ saveStubMapping ] Stub Mapping not Created. || Status Code : " + response.getStatusCode());
		}
	}

	public void resetStubMappings(String mockUrl)
	{
		response = postResponse(null,ContentType.JSON,mockUrl+Constants.WIREMOCK_MAPPINGS_RESET);
		if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
			Reporter.log("API : [ resetStubMapping ] Reset Mapping Saved. || Status Code : " + response.getStatusCode());
		} else {
			Reporter.log("API : [ resetStubMapping ] Reset Mapping not Created. || Status Code : " + response.getStatusCode());
		}
	}

	public String createStubMapping(String mockUrl, String request) {
		String ID = null;
		try {
			response = postResponseWithHeaders(request, null, ContentType.JSON, mockUrl + Constants.WIREMOCK_MAPPINGS_CREATE);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : " + response.getStatusCode());
				WiremockResponseDTO responseDto = convertFromJson(response.asString(), WiremockResponseDTO.class);
				ID = responseDto.getId();
			} else {
				Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage());
		}
		return ID;
	}

	public WiremockResponseDTO getSpecificStubMapping(String mockUrl, String stubId) {
		WiremockResponseDTO responseDto = null;
		try {
			String endpoint = Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = getResponse(null, mockUrl + endpoint, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ getSpecificStubMapping ] get specific stub mapping. || Status Code : " + response.getStatusCode());
				responseDto = convertFromJson(response.asString(), WiremockResponseDTO.class);

			} else {
				Reporter.log("API : [ getSpecificStubMapping ] not able to get specific stub mapping. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ getSpecificStubMapping ] ::: " + error.getMessage());
		}
		return responseDto;
	}

	public void modifySpecificStubMapping(String mockUrl, String request, String stubId) {
		try {
			String endpoint = Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = putResponse(request, ContentType.JSON, endpoint, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ modifySpecificStubMapping ] modify specific stub mapping. || Status Code : " + response.getStatusCode());
			} else {
				Reporter.log("API : [ modifySpecificStubMapping ] not able to modify specific stub mapping. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ modifySpecificStubMapping ] ::: " + error.getMessage());
		}
	}

	public WiremockResponseDTO deleteSpecificStubMapping(String mockUrl, String stubId) {
		WiremockResponseDTO responseDto = null;
		try {
			String endpoint = Constants.GET_WIREMOCK_MAPPING.replace("$id", stubId);
			response = deleteResponseWithHeaders(null, null,mockUrl + endpoint, true);
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ deleteSpecificStubMapping ] delete specific stub mapping. || Status Code : " + response.getStatusCode());
				responseDto = convertFromJson(response.asString(), WiremockResponseDTO.class);

			} else {
				Reporter.log("API : [ deleteSpecificStubMapping ] not able to delete specific stub mapping. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ deleteSpecificStubMapping ] ::: " + error.getMessage());
		}
		return responseDto;
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
				Reporter.log("API : [ startWiremockRecording ] start wiremock recording. || Status Code : " + response.getStatusCode());
			} else {
				Reporter.log("API : [ startWiremockRecording ] not able to start wiremock recording. || Status Code : " + response.getStatusCode());
			}
		} catch (Exception error) {
			Reporter.log("Error : [ startWiremockRecording ] ::: " + error.getMessage());
		}
	}

	
	public WiremockRecordStopResponse stopWiremockRecording(String mockUrl) {
		WiremockRecordStopResponse recordResponse = null ;
		try {
			response = postResponse(null,ContentType.JSON,mockUrl+Constants.RECORDSTOP_WIREMOCK_MAPPING);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				recordResponse = convertFromJson(response.asString(),WiremockRecordStopResponse.class);
				Reporter.log("API : [ stopWiremockRecording ] stop wiremock recording. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ stopWiremockRecording ] not able to stop wiremock recording. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ stopWiremockRecording ] ::: " + error.getMessage() );
		}
		return recordResponse;
	}

	public static void main(String agrs[]) {
		WiremockHelper wiremockHelper = new WiremockHelper();
		Map<String,Object> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		headers.put(HttpHeaders.CONNECTION,"close");
		Map<String,Object> params = new HashMap<>();
		Map<String,String> lob = new HashMap<>();
		lob.put("matches","Mobility");
		params.put("lineOfBusiness",lob);

		List<Map<String,Object>> bodyPattern = new ArrayList<>();
		Map<String,Object> patterns = new HashMap<>();
		patterns.put("equalToJson","{\"msisdn\":\"919007819561\",\"applicationId\":\"BILLING\",\"otp\":\"000000\",\"token\":null,\"operationName\":\"UNAUTH_OUTSTANDING\",\"attributes\":null}");
		patterns.put("ignoreArrayOrder",true);
		patterns.put("ignoreExtraElements",true);
		bodyPattern.add(patterns);


		String id = wiremockHelper.createStubMapping("http://10.5.118.52:8085",1,params,null,
				bodyPattern,"v1/services/jain0001","GET",200,headers,"{\"status_info\":{\"code\":\"200\",\"message\":\"Success1111111\"},\"params\":{\"requestId\":\"6316949828900152880\",\"uri\":\"\\\\/v1\\\\/services\\\\/917494874665?lineOfBusiness=Mobility\"},\"services\":[{\"si\":\"917494874665\",\"cust_ac_no\":\"1-56442923248\",\"imsi\":\"204032220857675\",\"si_sts\":\"ACTIVE\",\"circle\":\"Haryana\",\"cust_cat\":\"1\",\"cust_class\":\"Corporate\",\"res_addrss_address1\":\"Sector 40\",\"res_addrss_address2\":\"Gurugram\",\"res_addrss_city\":\"Gurgaon\",\"res_addrss_state\":\"Haryana\",\"res_addrss_pincode\":\"122016\",\"res_addrss_country\":\"356\",\"inst_addrss_address1\":\"Sector 40\",\"inst_addrss_address2\":\"Gurugram\",\"inst_addrss_city\":\"Gurgaon\",\"inst_addrss_state\":\"Haryana\",\"inst_addrss_pincode\":\"122016\",\"inst_addrss_country\":\"356\",\"cors_addrss_address1\":\"Gurugram\",\"cors_addrss_address2\":\"Gurugram\",\"cors_addrss_address3\":\"dfdwt\",\"cors_addrss_city\":\"Gurgaon\",\"cors_addrss_state\":\"Haryana\",\"cors_addrss_pincode\":\"122016\",\"cors_addrss_country\":\"356\",\"cust_type\":\"B2B\",\"ivr_lang\":\"ENG\",\"act_date\":\"20200515152538\",\"adh_acquired\":\"N\",\"prod_type\":\"Airtel Mobility Data B2B\",\"ndnc\":[],\"parent_ac_no\":\"1-51512507585\",\"root_ac_no\":\"1-51509819112\",\"is_billable\":\"0\",\"si_lob\":\"POSTPAID\",\"si_is_btfly\":\"1\",\"payto_bill_ac\":\"1-51512507585\",\"devices\":{}}]}");
		wiremockHelper.saveStubMapping("http://10.5.118.52:8085");
		System.out.println(wiremockHelper.getSpecificStubMapping("10.5.118.52:8085",id));
	}



}
