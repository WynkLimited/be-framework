package com.airtel.helper.Interfaces;

import java.util.Map;

public interface IResponseValidator {

     Map<String, Map<String, Object>> fieldMapper(String expectedApiResp, String actualApiResp,
                                                        Map<String, String> mapperProperties);

     Map<String, Map<String, Object>> fieldMapperString(String expectedApiResp, String actualApiResp,
                                                              Map<String, String> mapperProperties);

    boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath);

    boolean mappedResponseValidator(String expectedJson, String actualJson, String propertyFilePath,
                                    String testCaseName);

    boolean compareJson(String responseExpected, String responseActual);

    boolean compareJson(String responseExpected, String responseActual, String propFilePath);

    boolean compareJsonWithRequest(String responseExpected, String responseActual, String request,
                                   String propFilePath);




}
