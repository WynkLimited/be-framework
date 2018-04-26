package com.airtel.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.report.ReportHelper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

/**
 * This class contains methods relevant for comparison of two different JSON
 * with mapping of keys of those JSON.
 * 
 * @author b0202849
 *
 */
public class GenericComparator extends ReportHelper {

	static final String EXPECTED_RESULT = "expectedMap";
	static final String ACTUAL_RESULT = "actualMap";

	/**
	 * This method will fetch value from JSON at a particular JSON path. Value be
	 * returned as an Object as the class type is not defined
	 * 
	 * @param inputJson
	 * @param keyName
	 * @return
	 */
	private static Object fetchJPathVal(String inputJson, String keyName) {
		Configuration config = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
		return Optional.ofNullable(inputJson).map(body -> JsonPath.using(config).parse(body).read(keyName.trim()))
				.orElse(null);
	}

	/**
	 * This method is used for mapping the values of 2 different JSON into a parent
	 * JSON of same structure. This will help in comparison of those JSON.
	 * 
	 * @param apiResp
	 * @param logData
	 * @param mapperProperties
	 */
	public static Map<String, Map<String, Object>> fieldMapper(String expectedApiResp, String actualApiResp,
			Map<String, String> mapperProperties) {
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		Map<String, Object> expectedResp = new HashMap<>();
		Map<String, Object> actualResp = new HashMap<>();
		for (Map.Entry<String, String> mapperProp : mapperProperties.entrySet()) {
			String actualRespField = mapperProp.getValue();
			String fieldName = actualRespField.replace("$.", "").replace("[*]", "").replace("=true", "");
			Object expectedVal = fetchJPathVal(expectedApiResp, mapperProp.getKey());
			Object actualVal = fetchJPathVal(actualApiResp, actualRespField.replace("=true", ""));
			if (actualRespField.endsWith("=true")) {
				expectedVal = ApiHelper.sortArray(expectedVal);
				actualVal = ApiHelper.sortArray(actualVal);
			}
			expectedResp.put(fieldName, expectedVal);
			actualResp.put(fieldName, actualVal);
		}
		resultMap.put(EXPECTED_RESULT, expectedResp);
		resultMap.put(ACTUAL_RESULT, actualResp);
		return resultMap;
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
		return mappedResponseValidator(expectedJson, actualJson, propertyFilePath, null);
	}

	/**
	 * his method is used to compare 2 JSON with different key name for same values.
	 * Mapping property file needs to contain the JSONpath of both the JSON that
	 * needs to be compared <br/>
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
	 * @param testCaseName
	 * @return
	 */
	public static boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath,
			String testCaseName) {
		Map<String, String> mapperProperties = new HashMap<>();
		if (testCaseName != null) {
			Map<String, String> masterMappings = ApiHelper.fetchAllProperties(propertyFilePath);
			for (Map.Entry<String, String> masterMapping : masterMappings.entrySet()) {
				if (masterMapping.getKey().startsWith(testCaseName)) {
					String[] responseMap = masterMapping.getValue().split("=");
					mapperProperties.put(responseMap[0], responseMap[1]);
				}
			}
		} else {
			mapperProperties = ApiHelper.fetchAllProperties(propertyFilePath);
		}

		Map<String, Map<String, Object>> resultMap = fieldMapper(expectedJson, actualJson, mapperProperties);
		String expectedResp = ApiHelper.convertToJson(resultMap.get(EXPECTED_RESULT));
		String actualResp = ApiHelper.convertToJson(resultMap.get(ACTUAL_RESULT));

		return ApiResponseValidator.compareJson(expectedResp, actualResp, null, null, "move");
	}

}
