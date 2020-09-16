package com.airtel.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WiremockRequest {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("request")
    @Expose
    private MockRequest request;
    @SerializedName("response")
    @Expose
    private MockResponse response;
    @SerializedName("uuid")
    @Expose
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public  class MockRequest {

        @SerializedName("method")
        @Expose
        private String method;
        @SerializedName("urlPathPattern")
        @Expose
        private String urlPathPattern;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("queryParameters")
        @Expose
        private Map<String,Object> queryParameters;
        @SerializedName("headers")
        @Expose
        private Map<String, Object> headers;
        @SerializedName("bodyPatterns")
        @Expose
        private List<Map<String,Object>> bodyPatterns = null;


        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrlPath() {
            return urlPathPattern;
        }

        public void setUrlPath(String urlPath) {
            this.urlPathPattern = urlPathPattern;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String,Object> getQueryParameters() {
            return queryParameters;
        }

        public void setQueryParameters(Map<String,Object> queryParameters) {
            this.queryParameters = queryParameters;
        }

        public Map<String, Object> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }

        public List<Map<String,Object>> getBodyPatterns() {
            return bodyPatterns;
        }

        public void setBodyPatterns(List<Map<String,Object>> bodyPatterns) {
            this.bodyPatterns = bodyPatterns;
        }

    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public MockRequest getRequest() {
        return request;
    }

    public void setRequest(MockRequest request) {
        this.request = request;
    }

    public MockResponse getResponse() {
        return response;
    }

    public void setResponse(MockResponse response) {
        this.response = response;
    }

    public class MockResponse {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("headers")
        @Expose
        private Map<String,Object> headers;
        @SerializedName("body")
        @Expose
        private String body;
        @SerializedName("transformers")
        @Expose
        private List<String> transformers = null;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Map<String,Object> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String,Object> headers) {
            this.headers = headers;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<String> getTransformers() {
            return transformers;
        }

        public void setTransformers(List<String> transformers) {
            this.transformers = transformers;
        }

    }


    public MockRequest setRequest(Map<String,Object> queryParams, Map<String, Object> headers,
                                  List<Map<String,Object>> bodyPatterns, String httpMethod)
    {
        MockRequest request = new MockRequest();
        request.setBodyPatterns(bodyPatterns);
        request.setHeaders(headers);
        request.setMethod(httpMethod);
        request.setQueryParameters(queryParams);
        return request;
    }

    public MockResponse setResponse(Integer status,Map<String,Object> headers,String body)
    {
        MockResponse response = new MockResponse();
        response.setBody(body);
        response.setHeaders(headers);
        response.setStatus(status);
        List<String> transformers  = new ArrayList<>();
        transformers.add("response-template");
        response.setTransformers(transformers);
        return response;
    }
}
