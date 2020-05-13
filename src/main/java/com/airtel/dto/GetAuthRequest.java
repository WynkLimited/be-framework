package com.airtel.dto;


import java.util.Map;

import com.airtel.helper.common.BaseManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetAuthRequest extends BaseManager{

	@SerializedName("method")
	@Expose
	public String method;
	@SerializedName("endpoint")
	@Expose
	public String endpoint;
	@SerializedName("params")
	@Expose
	public Map<String, String> params;
	@SerializedName("body")
    @Expose
    public String body;
	@SerializedName("request")
    @Expose
    public String request;
	

}
