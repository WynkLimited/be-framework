package com.airtel.helper.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Constants {

	String AIRTEL_COMMON_CONFIG = "airtelCommon.config";
	String CONFIG_FOLDER = "config/";
	String LOG_NAME_KEY = "com.airtel.automation.log4j.property.path";
	String INFLUX_URL = "http://10.5.244.206:8086/";
	String INFLUX_DB_NAME = "api_automation";
	String INFLUX_USER = "automation";
	String INFLUX_PASS = "automation";
	String INFLUX_MEASUREMENT = "automation_stats";
	String WIREMOCK_MAPPINGS_CREATE = "/__admin/mappings";
	String WIREMOCK_MAPPINGS_SAVE = "/__admin/mappings/save";
	String WIREMOCK_MAPPINGS_RESET = "/__admin/requests/reset";
	String GET_WIREMOCK_MAPPING = "/__admin/mappings/$id";
	String RECORDSTART_WIREMOCK_MAPPING = "/__admin/recordings/start";
	String RECORDSTOP_WIREMOCK_MAPPING = "/__admin/recordings/stop";
	List<Integer> SUCCESS_CODE = new ArrayList<>(Arrays.asList(200, 201, 202, 204));

}
