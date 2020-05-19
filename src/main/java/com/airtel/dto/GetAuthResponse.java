package com.airtel.dto;

import org.json.JSONObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetAuthResponse {
	@SerializedName("x-bsy-bn")
	@Expose
	private String xBsyBn;
	@SerializedName("x-bsy-did")
	@Expose
	private String xBsyDid;
	@SerializedName("x-bsy-dt")
	@Expose
	private String xBsyDt;
	@SerializedName("x-bsy-os")
	@Expose
	private String xBsyOs;
	@SerializedName("x-bsy-utkn")
	@Expose
	private String xBsyUtkn;

	public GetAuthResponse(Response response) {
		JSONObject json = new JSONObject(response.asString());
		xBsyBn = json.optString("x-bsy-bn");
		xBsyDid = json.optString("x-bsy-did");
		xBsyDt = json.optString("x-bsy-dt");
		xBsyOs = json.optString("x-bsy-os");
		xBsyUtkn = json.optString("x-bsy-utkn");
	}

	

}
