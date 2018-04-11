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
		return Optional.ofNullable(inputJson).map(body -> JsonPath.using(config).parse(body).read(keyName))
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

	public static void main(String[] args) {
		String response1 = "{\"productId\":\"1251\",\"archived\":\"N\",\"productType\":\"PRODUCT\",\"manufacture\":\"Airtel\",\"model\":\"Airtel Mifi\",\"url\":\"/airtel-mifi\",\"adminAdditionStatus\":\"CONFIRMED\",\"blcSkusForDefaultProduct\":{\"skuId\":2201,\"archived\":\"N\",\"longDescription\":\"<div class=\\\"mb-15 color-grey fs-12\\\">Features\\n</div>\\n<ul class=\\\"pl-15 mb-20\\\">\\n\\t<li> Enjoy 4G speeds even on non-4G devices </li>\\n\\t<li> Connect up to 10 Wi-Fi enabled devices</li>\\n\\t<li> Powerful battery</li>\\n        <li> Better signal reception</li>\\n      </li>   Stay connected on-the-go </li>\\n</ul>\\n\",\"name\":\"Airtel Mifi\"},\"airtelProductDetails\":{\"airtelProductId\":1251,\"deviceNo\":\"Airtel Mifi\",\"internalMemory\":1,\"isPlanproduct\":false,\"modelNo\":\"Airtel Mifi\",\"style\":\"Regular Fulfilled\",\"warranty\":\"1 year manufacturer warranty for device and 6 months manufacturer warranty for in-box accessories including batteries from the date of purchase\",\"airtelPlanByCocpPlanId\":0,\"airtelPlanByPlanId\":0},\"blcSkusForAddlProductIdList\":[2202],\"productAttributes\":[{\"productAttributeId\":1256,\"name\":\"WIRELESS LAN\",\"value\":\"4 Ghz Wi-fi with Auto channel selection WPA & WPS\",\"archived\":\"N\"},{\"productAttributeId\":1255,\"name\":\"SUPPORTS\\t\",\"value\":\"Windows 7, Windows 8, Windows 8.1, Windows 10 (does not support Windows RT), MAC OS X 10.7, 10.8, 10.9, 10.10 and 10.11 with latest upgrades\",\"archived\":\"N\"},{\"productAttributeId\":1252,\"name\":\"BATTERY CAPACITY\\t\",\"value\":\"1800 mAh\",\"archived\":\"N\"},{\"productAttributeId\":1253,\"name\":\"DATA SPEEDS\\t\",\"value\":\"LTE TDD (Category 3)\",\"archived\":\"N\"},{\"productAttributeId\":1251,\"name\":\"POWER SUPPLY AC\",\"value\":\"100–240 V; DC: 5 V, 1 A\",\"archived\":\"N\"},{\"productAttributeId\":1258,\"name\":\"PORT\",\"value\":\"Dual antenna extension ports\",\"archived\":\"N\"},{\"productAttributeId\":1254,\"name\":\"NETWORK TYPE\",\"value\":\"LTE /3G/2G\",\"archived\":\"N\"},{\"productAttributeId\":1257,\"name\":\"WAN\",\"value\":\"LTE (2300/1800/900MHz)\",\"archived\":\"N\"}],\"categoryProductXrefList\":[{\"categoryProductId\":1251,\"blcProductId\":1251,\"blcCategoryId\":202,\"archived\":\"N\"}]}";
		String response2 = "{\"id\":\"2010\",\"title\":\"Bundle-2202[- S01][-O06]\",\"description\":\"Airtel Mifi\",\"storage\":\"1GB\",\"pricingFormula\":\"skuPrice + (-1 * 0.0*499.0)\",\"price\":999.0,\"rank\":21,\"isFeatured\":false,\"isArchived\":false,\"inStock\":true,\"productId\":\"1251\",\"brand\":\"Airtel MiFi\",\"leafCategoryId\":\"202\",\"leafCategoryName\":\"Airtel MiFi\",\"categoryIds\":[\"10000052\",\"10000055\",\"202\"],\"categoryNames\":[\"Devices\",\"Mobile Phones\",\"Airtel MiFi\"],\"categoryTree\":\"Devices >> Mobile Phones >> Airtel MiFi\",\"modelNo\":\"Airtel Mifi\",\"checksum\":\"s1;o6;\",\"skuDetails\":[{\"skuTitle\":\"Airtel Mifi\",\"skuType\":\"primary_sku\",\"colour\":\"White\",\"colourCode\":\"#ffffff\",\"skuId\":\"2202\",\"skuMrp\":\"999\",\"skuSellingPrice\":\"999\",\"skuPrimaryImageUrl\":\"/devices/mifi/airtel-mifi1.png\",\"skuSecondaryImageUrl\":[\"/devices/mifi/airtel-mifi4.png\",\"/devices/mifi/airtel-mifi2.png\",\"/devices/mifi/airtel-mifi3.png\",\"/devices/mifi/airtel-mifi1.png\"],\"skuProductId\":\"1251\"}],\"technicalDetails\":[{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"BATTERY CAPACITY\\t\",\"technicalDetailsValue\":\"1800 mAh\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"SUPPORTS\\t\",\"technicalDetailsValue\":\"Windows 7, Windows 8, Windows 8.1, Windows 10 (does not support Windows RT), MAC OS X 10.7, 10.8, 10.9, 10.10 and 10.11 with latest upgrades\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"WAN\",\"technicalDetailsValue\":\"LTE (2300/1800/900MHz)\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"POWER SUPPLY AC\",\"technicalDetailsValue\":\"100–240 V; DC: 5 V, 1 A\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"DATA SPEEDS\\t\",\"technicalDetailsValue\":\"LTE TDD (Category 3)\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"WIRELESS LAN\",\"technicalDetailsValue\":\"4 Ghz Wi-fi with Auto channel selection WPA & WPS\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"PORT\",\"technicalDetailsValue\":\"Dual antenna extension ports\"},{\"technicalDetailsTitle\":\"Technical Details\",\"technicalDetailsKey\":\"NETWORK TYPE\",\"technicalDetailsValue\":\"LTE /3G/2G\"}],\"serviceDetails\":[{\"serviceId\":\"1\",\"serviceType\":\"PLAN\",\"serviceTypeDisplayLabel\":\"iphone 8 plan\",\"serviceDescription\":\"<div> <h4 class=\\\"mb-40 color-grey\\\">Airtel Plan Includes</h4> <div class=\\\"row specialOffers_box\\\"> <div class=\\\"col-md-5 col-sm-6 mb-40 col-xs-12 specialOffers_box_col\\\"> <div class=\\\"row mb-xs-20\\\"> <div class=\\\"col-xs-2\\\"> <i class=\\\"icon-outlined-data text-strong-orange fs-50\\\"></i> </div><div class=\\\"col-xs-10\\\"> <h6 class=\\\"text-strong-orange mb-10\\\">80 GB Data per month</h6> </div></div><p class=\\\"fs-xs-12\\\">Enjoy 80 GB data every month.</p></div><div class=\\\"col-md-5 col-sm-6 mb-40 col-xs-12 specialOffers_box_col pull-right\\\" class=\\\"pull-right\\\"> <div class=\\\"row mb-xs-20\\\"> <div class=\\\"col-xs-2\\\"> <i class=\\\"icon-outlined-movies text-strong-orange fs-50\\\"></i> </div><div class=\\\"col-xs-10\\\"> <h6 class=\\\"text-strong-orange mb-10\\\">Amazon Prime</h6> </div></div><p class=\\\"fs-xs-12\\\">Free Amazon Prime one year subscription</p></div><div class=\\\"col-md-5 col-sm-6 mb-40 col-xs-12 specialOffers_box_col\\\" ng-class=\\\"pull-right\\\"> <div class=\\\"row mb-xs-20\\\"> <div class=\\\"col-xs-2\\\"> <i class=\\\"icon-outlined-handset-damage text-strong-orange fs-50\\\"></i> </div><div class=\\\"col-xs-10\\\"> <h6 class=\\\"text-strong-orange mb-10\\\">Free Handset Damage Protection</h6> </div></div><p class=\\\"fs-xs-12\\\">Get Free mobile repairs in case of accidental or physical damage.</p></div><div class=\\\"col-md-5 col-sm-6 mb-40 col-xs-12 specialOffers_box_col pull-right\\\" ng-class=\\\"pull-right\\\"> <div class=\\\"row mb-xs-20\\\"> <div class=\\\"col-xs-2\\\"> <i class=\\\"icon-outlined-unlimited-calling text-strong-orange fs-50\\\"></i> </div><div class=\\\"col-xs-10\\\"> <h6 class=\\\"text-strong-orange mb-10\\\">Unlimited Calling</h6> </div></div><p class=\\\"fs-xs-12\\\">All calls across India are Free including the calls on National Roaming.</p></div></div></div>\",\"servicePrice\":499.0,\"serviceImpactPrice\":499.0,\"serviceDisplayable\":false,\"serviceAppliedOn\":\"1521608486000\",\"serviceInstallationCost\":0.0,\"serviceDownPayment\":0.0,\"serviceProcessingCharge\":0.0}],\"offerDetails\":[{\"offerId\":\"6\",\"offerType\":\"SUBSCRIPTION\",\"offerTypeDisplayLabel\":\"test\",\"offerDescription\":\"dummy\",\"offerPrice\":100,\"offerPricesPercentage\":-1.0,\"offerImpactPrice\":100.5,\"offerDisplayable\":true,\"offerstatus\":\"true\",\"offerNature\":\"DISCOUNTED\",\"offerLob\":\"MOBILITY\",\"offerSubscriptionPeriod\":\"1\"}]}";
		mappedResponseValidator(response1, response2, "config/json.properties");
	}

}
