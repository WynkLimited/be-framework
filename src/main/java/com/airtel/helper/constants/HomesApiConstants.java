package com.airtel.helper.constants;

public interface HomesApiConstants {

	final static String FEASIBILITY_API_PATH = "/feasibility/rest/feasibility/findFeasiblePlans";
	final static String FEASIBILITY_ES_LOGS_PATH = "http://10.5.198.56:8080/testFramework/rest/logger/findLogsById";
	final static String COMPARATOR_IGNORE_FILE_PATH = "config/ignoreFromComparison.properties";
	final static String CONFIGURATION_FILE_PATH = "config/envConfig.properties";
	final static String ES_LOG_PARENT_DATA = "data";
	final static String ES_LOG_PATH = "data";
	final static String VALIDATE_FEASIBILITY_REQUEST = "/feasibility/rest/feasibility/validateFeasibilityRequest";
	final  static String ESLOGS_INCOMING_REQUEST="FEASIBILITY_INCOMING_REQUEST";
	final  static String ESLOGS_OUTGOING_RESPONSE="FEASIBILITY_OUTGOING_RESPONSE";
	final  static  String CREATE_HOMES_PROFILE_PATH="/homesDb/crm/createHomesProfile";
	final  static  String GET_RSU_CITIES="/feasibility/rest/feasibility/getRSUCities";
	final  static  String NDS_LOOKUP_SERVICE="/NDSLookupService/services/NDSLookupService";
}
