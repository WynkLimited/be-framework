package com.airtel.helper.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.airtel.controller.GetAuthManager;
import com.airtel.dto.GetAuthResponse;
import com.airtel.helper.report.ReportHelper;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class AuthenticatedAppHeaders {
	private List<Header> headers;

	public AuthenticatedAppHeaders() {
	}

	public List<Header> getEncryptedHeader(String request, String mobileNumber, String httpMethod, String env,
			String endPoint, Map<String, String> queryParams) {
		headers = new ArrayList<>();
		Response apiResponse = getAuthResponse(request, mobileNumber, httpMethod, env, endPoint, queryParams);
		if (apiResponse.contentType().contains(ContentType.JSON.toString()) && apiResponse.asString() != null
				&& apiResponse.getStatusCode() == 200) {
			GetAuthResponse authResponse = new GetAuthResponse(apiResponse);
			headers.add(new Header("x-bsy-os", authResponse.getXBsyOs()));
			headers.add(new Header("x-bsy-did", authResponse.getXBsyDid()));
			headers.add(new Header("x-bsy-dt", authResponse.getXBsyDt()));
			headers.add(new Header("x-bsy-bn", authResponse.getXBsyBn()));
			headers.add(new Header("x-bsy-utkn", authResponse.getXBsyUtkn()));
		} else
			ReportHelper.logValidationFailure("User is not logged in", "Http Response code should be 200",
					"Http Response code should is 500", "API Failure");
		return headers;

	}

	private Response getAuthResponse(String request, String mobileNumber, String httpMethod, String env,
			String endPoint, Map<String, String> queryParams) {
		GetAuthManager authManager = new GetAuthManager(request, mobileNumber, httpMethod, env, endPoint, queryParams);
		Response authResponse = authManager.executeApi();
		return authResponse;
	}

}
