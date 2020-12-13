package com.airtel.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WiremockRecordStartRequest {

        @SerializedName("targetBaseUrl")
        @Expose
        private String targetBaseUrl;

        public String getTargetBaseUrl() {
            return targetBaseUrl;
        }

        public void setTargetBaseUrl(String targetBaseUrl) {
            this.targetBaseUrl = targetBaseUrl;
        }

}
