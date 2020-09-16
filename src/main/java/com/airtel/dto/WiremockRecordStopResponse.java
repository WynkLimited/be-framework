package com.airtel.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WiremockRecordStopResponse {


    @SerializedName("mappings")
    @Expose
    private List<Mapping> mappings = null;

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public class Mapping {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("request")
        @Expose
        private MappingRequest request;
        @SerializedName("response")
        @Expose
        private MappingResponse response;
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("persistent")
        @Expose
        private Boolean persistent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public MappingRequest getRequest() {
            return request;
        }

        public void setRequest(MappingRequest request) {
            this.request = request;
        }

        public MappingResponse getResponse() {
            return response;
        }

        public void setResponse(MappingResponse response) {
            this.response = response;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Boolean getPersistent() {
            return persistent;
        }

        public void setPersistent(Boolean persistent) {
            this.persistent = persistent;
        }

        public class MappingRequest {

            @SerializedName("url")
            @Expose
            private String url;
            @SerializedName("method")
            @Expose
            private String method;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

        }

        public class MappingResponse {

            @SerializedName("status")
            @Expose
            private Integer status;
            @SerializedName("body")
            @Expose
            private String body;
            @SerializedName("headers")
            @Expose
            private Headers headers;

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public Headers getHeaders() {
                return headers;
            }

            public void setHeaders(Headers headers) {
                this.headers = headers;
            }

            public class Headers {

                @SerializedName("Content-Type")
                @Expose
                private String contentType;
                @SerializedName("Cache-control")
                @Expose
                private String cacheControl;
                @SerializedName("Pragma")
                @Expose
                private String pragma;
                @SerializedName("Connection")
                @Expose
                private String connection;
                @SerializedName("Date")
                @Expose
                private String date;
                @SerializedName("Status-Code")
                @Expose
                private String statusCode;

                public String getContentType() {
                    return contentType;
                }

                public void setContentType(String contentType) {
                    this.contentType = contentType;
                }

                public String getCacheControl() {
                    return cacheControl;
                }

                public void setCacheControl(String cacheControl) {
                    this.cacheControl = cacheControl;
                }

                public String getPragma() {
                    return pragma;
                }

                public void setPragma(String pragma) {
                    this.pragma = pragma;
                }

                public String getConnection() {
                    return connection;
                }

                public void setConnection(String connection) {
                    this.connection = connection;
                }

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getStatusCode() {
                    return statusCode;
                }

                public void setStatusCode(String statusCode) {
                    this.statusCode = statusCode;
                }

            }

        }

    }
}
