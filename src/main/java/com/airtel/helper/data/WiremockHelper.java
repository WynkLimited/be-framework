package com.airtel.helper.data;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.common.BaseTestClass;

import java.util.List;
import org.testng.Reporter;
import java.util.Map;
import java.io.IOException;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WiremockHelper extends BaseTestClass {
	
	public Response response = null;
    private WireMockServer wireMockServer;
	
	
	public void createStubMapping (String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"POST", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ createStubMapping ] Stub Mapping Created. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ createStubMapping ] Stub Mapping not Created. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ createStubMapping ] ::: " + error.getMessage() );
		}
	}
	
	public void getAllStubMapping(String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"GET", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ getAllStubMapping ] get all stub mapping. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ getAllStubMapping ] not able to stub mapping. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ getAllStubMapping ] ::: " + error.getMessage() );
		}
	}
	
	public void getSpecificStubMapping(String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"GET", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ getSpecificStubMapping ] get specific stub mapping. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ getSpecificStubMapping ] not able to get specific stub mapping. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ getSpecificStubMapping ] ::: " + error.getMessage() );
		}
	}
	
	public void modifiedSpecificStubMapping(String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"PUT", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ getSpecificStubMapping ] modify specific stub mapping. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ getSpecificStubMapping ] not able to modify specific stub mapping. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ modifiedSpecificStubMapping ] ::: " + error.getMessage() );
		}
	}
	
	public void startWiremockRecording(String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"POST", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ startWiremockRecording ] start wiremock recording. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ startWiremockRecording ] not able to start wiremock recording. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ startWiremockRecording ] ::: " + error.getMessage() );
		}
	}
	
	public void stopWiremockRecording(String url, String body, List<Header> headers ) {
		try {
			ApiHelper apiHelper = new ApiHelper();
			response = apiHelper.responseWithAllParameter(body,"POST", headers, url);
			if(response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				Reporter.log("API : [ stopWiremockRecording ] stop wiremock recording. || Status Code : " + response.getStatusCode());
	        }else {
	        	Reporter.log("API : [ stopWiremockRecording ] not able to stop wiremock recording. || Status Code : " + response.getStatusCode());
	        }
		} catch(Exception error) {
			Reporter.log("Error : [ stopWiremockRecording ] ::: " + error.getMessage() );
		}
	}

}
