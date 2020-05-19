package com.airtel.dto;


import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAuthRequest{

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
	
	
	public GetAuthRequest(String method, String endpoint, Map<String,String> params,String body) {
        this.method = method;	
        this.endpoint = endpoint;
        this.params = params;
        this.body = body;
    }
	
	
}
