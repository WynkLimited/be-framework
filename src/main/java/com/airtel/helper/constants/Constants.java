
package com.airtel.helper.constants;

public interface Constants {

	String AIRTEL_COMMON_CONFIG = "airtelCommon.config";
	String CONFIG_FOLDER = "config/";
	String LOG_NAME_KEY = "com.airtel.automation.log4j.property.path";
	String INFLUX_URL = "http://10.5.244.206:8086/";
	String INFLUX_DB_NAME = "api_automation";
	String INFLUX_USER = "automation";
	String INFLUX_PASS = "automation";
	String INFLUX_MEASUREMENT = "automation_stats";

	interface ERROR_CODE {
		String CONFIGURATION_PROPERTY = "ERR_CFG_00";
	}

}
