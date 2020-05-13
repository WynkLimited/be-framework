package com.airtel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airtel.dto.GetAuthRequest;
import com.airtel.helper.common.BaseManager;
import com.airtel.helper.constants.Constants;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class GetAuthManager extends BaseManager implements Constants {

	public static final String defaultRequest = "{\"method\":\"\",\"endpoint\":\"/app/wl-service/airtel-selfcare/rest/ir/landingpage\",    \"params\":{\"siNumber\":\"9599330841\"},    \"body\":\"\"}";
	private List<Header> headerList = new ArrayList<Header>();
	private Map<String, String> queryParams = new HashMap<String, String>();
	private Response response;
	private String request;
	private GetAuthRequest authRequest;

	public GetAuthManager(String request, String mobileNumber, String httpMethod,String env,String endPoint,Map<String,String> queryParams) {
		authRequest = convertFromJson(defaultRequest, GetAuthRequest.class);
		this.queryParams.put("number", mobileNumber);
		this.queryParams.put("env", env);
		this.request = setRequest(httpMethod,request,endPoint,queryParams);
	}

	public Response executeApi() {
		String url = GETAUTH_URL + GETAUTH_ENDPOINT;
		this.headerList.add(new Header("Authorization", "Basic Ymxvb2Rob3VuZDpGaXNoeVJlZEhlcnJpbmc="));
		response = postResponseWithHeaders(this.request, this.headerList, ContentType.JSON, url, this.queryParams,
				false);
		return response;
	}

	private String setRequest(String httpMethod,String request,String endPoint,Map<String,String> queryParams) {
		authRequest.setMethod(httpMethod);
		authRequest.setEndpoint(endPoint);
		authRequest.setParams(queryParams);
		authRequest.setBody(request);
		request = convertToJson(authRequest);
		return request;
	}

}
