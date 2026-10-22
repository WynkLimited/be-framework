package com.airtel.validator;

import com.airtel.helper.Interfaces.IResponseValidator;
import com.airtel.helper.data.DataHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods relevant for comparison of two different JSON
 * with mapping of keys of those JSON.
 *
 * @author b0202849
 */
public class GenericComparator extends ApiResponseValidator implements IResponseValidator {

    final String EXPECTED_RESULT = "expectedMap";
    final String ACTUAL_RESULT = "actualMap";

    /**
     * This method is used for mapping the values of 2 different JSON into a parent
     * JSON of same structure with json Value of Object DataType. This will help in comparison of those JSON.
     *
     * @param mapperProperties
     */
    @Override

    public Map<String, Map<String, Object>> fieldMapper(String expectedApiResp, String actualApiResp,
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
                expectedVal = sortArray(expectedVal);
                actualVal = sortArray(actualVal);
            }
            expectedResp.put(fieldName, expectedVal);
            actualResp.put(fieldName, actualVal);
        }
        resultMap.put(EXPECTED_RESULT, expectedResp);
        resultMap.put(ACTUAL_RESULT, actualResp);
        return resultMap;
    }

    /**
     * This method is used for mapping the values of 2 different JSON into a parent
     * JSON of same structure with json Value of String DataType. This will help in comparison of those JSON.
     *
     * @param mapperProperties
     */
    @Override
    public Map<String, Map<String, Object>> fieldMapperString(String expectedApiResp, String actualApiResp,
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
                expectedVal = sortArray(expectedVal);
                actualVal = sortArray(actualVal);
            }

            if (expectedVal == null) {
                expectedResp.put(fieldName, expectedVal);
            } else {
                expectedResp.put(fieldName, convertJsonArrayObjToStringObj(expectedVal));
            }

            if (actualVal == null) {
                actualResp.put(fieldName, actualVal);
            } else {
                actualResp.put(fieldName, convertJsonArrayObjToStringObj(actualVal));
            }

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
    @Override
    public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath) {
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
    @Override
    public boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath,
                                           String testCaseName) {
        return mappedResponseValidator(expectedJson, actualJson, propertyFilePath, testCaseName, false);
    }

    /**
     * This method is used to compare 2 JSON with different key name for same values
     * with flag isDataTypeComparisonRequired which if set true will return false if
     * dataTypes of two Jsons are different.
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
    private boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath,
                                            String testCaseName, boolean isDataTypeComparisonRequired) {
        Map<String, String> mapperProperties = new HashMap<>();
        Map<String, Map<String, Object>> resultMap;

        if (testCaseName != null) {
            Map<String, String> masterMappings = new DataHelper().fetchAllProperties(propertyFilePath);
            for (Map.Entry<String, String> masterMapping : masterMappings.entrySet()) {
                if (masterMapping.getKey().startsWith(testCaseName)) {
                    String[] responseMap = masterMapping.getValue().split("=");
                    mapperProperties.put(responseMap[0], responseMap[1]);
                }
            }
        } else {
            mapperProperties = new DataHelper().fetchAllProperties(propertyFilePath);
        }

        if (isDataTypeComparisonRequired) {
            resultMap = fieldMapper(expectedJson, actualJson, mapperProperties);

        } else {
            resultMap = fieldMapperString(expectedJson, actualJson, mapperProperties);
        }

        String expectedResp = convertToJson(resultMap.get(EXPECTED_RESULT));
        String actualResp = convertToJson(resultMap.get(ACTUAL_RESULT));


        return compareJson(expectedResp, actualResp, null, null, "move");
    }

}
