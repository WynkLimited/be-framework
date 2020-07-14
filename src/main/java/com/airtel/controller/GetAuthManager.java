package com.airtel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airtel.dto.GetAuthRequest;
import com.airtel.helper.constants.Constants;

import io.restassured.http.Header;

public class GetAuthManager implements Constants {

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

	public GetAuthRequest setRequest(String httpMethod, String request, String endPoint,
			Map<String, String> queryParams) {
		GetAuthRequest authRequest = new GetAuthRequest(httpMethod, endPoint, queryParams,request);
		return authRequest;
	}

}
