package com.airtel.helper.Interfaces;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

public interface IJsonParser {

	/**
	 * This method convert a particular object to JSON. This is mostly used for
	 * comparator.
	 *
	 * @param obj
	 * @return
	 */
	String convertToJson(Object obj);

	/**
	 * This method convert a json string to a particular DTO type specified as
	 * input. This method returns object of a generic type and therefore casting is
	 * not required with this method.
	 *
	 * @param jsonData
	 * @param classType
	 * @return Object of type specified as input class
	 */
	<T> T convertFromJson(String jsonData, Class<T> classType);

	/**
	 * This method will be used to convert a dynamic class response from JSON
	 *
	 * @param jsonData
	 * @param type
	 * @return
	 * @author b0201347
	 */
	<T> T convertFromJson(String jsonData, Type type);

	/**
	 * Returns list of object converted from a JSON array string. Input of this
	 * method is the Type of list and JSON Array string.
	 *
	 * @param jsonArray
	 * @param listType
	 * @return
	 */
	<T> List<T> convertFromJsonArray(String jsonArray, Type listType);

	/**
	 * This method will fetch value from JSON at a particular JSON path. Value be
	 * returned as an Object as the class type is not defined
	 *
	 * @param inputJson
	 * @param keyName
	 * @return
	 */
	Object fetchJPathVal(String inputJson, String keyName);

	/**
	 * This method will fetch value from JSON at a particular JSON path. Value be
	 * returned for the class provided as input.
	 *
	 * @param inputJson
	 * @param keyName
	 * @return
	 */
	<T> T fetchJPathVal(String inputJson, String keyName, Class<T> classType);

	/**
	 * This method sort array of object fetched from jspath and on the bases of
	 * value of json
	 *
	 * @param unsortedArrayObj
	 * @return
	 */
	Object sortArray(Object unsortedArrayObj);

	boolean isStringFeildNotBlankInsideJSONObject(JSONObject jsonObject, String field);

	boolean isJsonObjectInsideJsonObjectNotBlank(JSONObject jsonObject, String field);

	boolean isJsonArrayNotBlank(JsonArray jsonArray);

	boolean isListFieldBlank(JSONObject jsonObject, String field);

	/**
	 * This method is used to convert object to jsonArray if it is JsonArray else it
	 * will return null.
	 *
	 * @param object
	 * @return
	 */
	org.json.JSONArray objectToJSONArray(Object object);

	/**
	 * This method is used to convert Json Array Object and its values to String
	 * Object
	 *
	 * @param object
	 * @return
	 */
	Object convertJsonArrayObjToStringObj(Object object);

	/**
	 * Parses the specified JSON string into a parse tree
	 *
	 * @param json JSON text
	 * @return a parse tree of {@link JsonElement}s corresponding to the specified
	 *         JSON
	 * @throws JsonParseException if the specified text is not valid JSON
	 * @since 1.3
	 */
	JsonElement parse(String json);

	/**
	 * Parses the specified JSON string into a parse tree
	 *
	 * @param json JSON text
	 * @return a parse tree of {@link JsonElement}s corresponding to the specified
	 *         JSON
	 * @throws JsonParseException if the specified text is not valid JSON
	 * @since 1.3
	 */
	JsonElement parse(Reader json);

	/**
	 * Returns the next value from the JSON stream as a parse tree.
	 *
	 * @throws JsonParseException if there is an IOException or if the specified
	 *                            text is not valid JSON
	 * @since 1.6
	 */
	JsonElement parse(JsonReader json);
	
	
	/**
	 * @param String response
	 * Returns the formatted response in Pretty Print
	 */
	String formatResponse(String response);
}
