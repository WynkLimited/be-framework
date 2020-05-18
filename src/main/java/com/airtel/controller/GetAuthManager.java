package com.airtel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airtel.dto.GetAuthRequest;
import com.airtel.helper.constants.Constants;

import io.restassured.http.Header;

public class GetAuthManager implements Constants {

	public static final String defaultRequest = "{\"method\":\"\",\"endpoint\":\"/\",    \"params\":{\"siNumber\":\"\"},    \"body\":\"\"}";
	private List<Header> headerList = new ArrayList<Header>();
	private Map<String, String> queryParams = new HashMap<String, String>();
	private String url;

	public String setUrl() {
		url = GETAUTH_URL + GETAUTH_ENDPOINT;
		return url;
	}

	public Map<String, String> setQueryParams(String env, String rtn) {
		queryParams.put("number", rtn);
		queryParams.put("env", env);
		return queryParams;
	}

	public List<Header> setHeaders() {
		headerList.add(new Header("Authorization", APP_AUTHENTICATION_KEY));
		return headerList;
	}

	public void setRequest(GetAuthRequest authRequest, String httpMethod, String request, String endPoint,
			Map<String, String> queryParams) {
		authRequest.setMethod(httpMethod);
		authRequest.setEndpoint(endPoint);
		authRequest.setParams(queryParams);
		authRequest.setBody(request);
	}

}
