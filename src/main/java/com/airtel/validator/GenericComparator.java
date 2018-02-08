package com.airtel.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.Test;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.report.ReportHelper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import junit.framework.Assert;

/**
 * This class contains methods relevant for comparison of two different JSON
 * with mapping of keys of those JSON.
 * 
 * @author b0202849
 *
 */
public class GenericComparator extends ReportHelper {
	private static Map<String, Object> expectedResp = new HashMap<>();
	private static Map<String, Object> actualResp = new HashMap<>();
	private static String defFieldName = "baseObjField";
	private static int fieldCounter = 0;

	private static Object fetchJPathVal(String inputJson, String keyName) {
		Configuration config = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
		return Optional.ofNullable(inputJson).map(body -> JsonPath.using(config).parse(body).read(keyName))
				.orElse(null);
	}

	public static void fieldMapper(String apiResp, String logData, Map<String, String> mapperProperties) {
		for (Map.Entry<String, String> mapperProp : mapperProperties.entrySet()) {
			Object expectedVal = fetchJPathVal(apiResp, mapperProp.getKey());
			Object actualVal = fetchJPathVal(logData, mapperProp.getValue());
			String fieldName = defFieldName + fieldCounter;
			expectedResp.put(fieldName, expectedVal);
			actualResp.put(fieldName, actualVal);
		}
	}

	/**
	 * This method is used to compare 2 JSON with different key name for same
	 * values. Mapping property file needs to contain the JSONpath of both the json
	 * that needs to be compared <br/>
	 * <br/>
	 * <b>Example of property file entry:</b> <br/>
	 * <br/>
	 * <i>Single field mapping: </i> json1.field1 = json2.field2 <br/>
	 * <i>List mapping:</i> json1.field3[*] = json2.field4[*] <br/>
	 * <i>Object mapping:</i> json1.object1 = json2.object2 <br/>
	 * <br/>
	 * 
	 * @param expectedJson
	 * @param actualJson
	 * @param propertyFilePath
	 * @return result of comparison b/w the 2 json based on mapping
	 */
	public static boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath) {
		Map<String, String> mapperProperties = ApiHelper.fetchAllProperties(propertyFilePath);
		fieldMapper(expectedJson, actualJson, mapperProperties);
		return ApiResponseValidator.compareJson(ApiHelper.convertToJson(expectedResp),
				ApiHelper.convertToJson(actualResp), null, null, "move");

	}

}
