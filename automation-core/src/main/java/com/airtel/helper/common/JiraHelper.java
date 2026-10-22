package com.airtel.helper.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airtel.dto.UpdateJira;
import com.airtel.dto.UpdateJira.SummaryJiraDTO;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class JiraHelper extends ApiHelper {

	public Response response = null;
	
	public Response getIssuebyId(boolean checkstatus, String url, String authToken, String issueId)
	{
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Authorization", "Basic " + authToken));
        
		Response response= getResponseWithHeaders(null, headers, url+"/rest/api/3/issue/"+ issueId, checkstatus);
		return response;
		
	}
	

	public Response editIssueById(boolean checkstatus, String url, String authToken, String issueId, String body)
	{	
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Authorization", "Basic " + authToken));
		
		Response response = putResponseWithHeaders(body, headers, ContentType.JSON, url+"/rest/api/3/issue/"+issueId);
		return response;
	}
	
	public Response updateIssueSummaryById(boolean checkstatus, String url, String authToken, String issueId, String summary)
	{	
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Authorization", "Basic " + authToken));
		
		UpdateJira obj = new UpdateJira();
		UpdateJira.SummaryJiraDTO summaryObj= obj.new SummaryJiraDTO();
		
		summaryObj.setSet(summary);
		obj.setUpdate(Arrays.asList(summaryObj));
		
		String body= convertToJson(obj);
		
		Response response = putResponseWithHeaders(body, headers, ContentType.JSON, url+"/rest/api/3/issue/"+issueId);
		return response;
	}
	
	public Response getZephyerTestCaseById(String authToken,boolean checkstatus, String testCaseId, String url)
	{
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Authorization", "Bearer " + authToken));
		
		Map<String, String> params= new HashMap<>();
		params.put("fields", "id,projectId,archived,key,name,objective,majorVersion,latestVersion,precondition,folder(id,fullName),status,priority,estimatedTime,averageTime,componentId,owner,labels,customFieldValues,testData,parameters(id,name,defaultValue,index),paramType");
		
		Response response= getResponseWithHeaders(params, headers, url+"/v2/testcases/"+ testCaseId, checkstatus);
		return response;
	
	}
	
	
	public Response updateZephyerTestCaseById(String authToken, String testCaseId, String url , Object body )
	{
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Authorization", "Bearer " + authToken));
		
		System.out.print("Body passing to update   "+ body);
		Response response = putResponseWithHeaders(body.toString(), headers, ContentType.JSON,url+"/v2/testcases/"+testCaseId);
		return response;
	}
	
	
	

	
	

}
