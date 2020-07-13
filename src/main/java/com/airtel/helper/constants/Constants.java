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
    List<Integer> SUCCESS_CODE = new ArrayList<>(Arrays.asList(200,202,204));

	interface ERROR_CODE {
		String CONFIGURATION_PROPERTY = "ERR_CFG_00";
	}

}
