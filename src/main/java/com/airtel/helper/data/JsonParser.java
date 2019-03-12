package com.airtel.helper.data;

import com.airtel.helper.Interfaces.IJsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.*;

public class JsonParser implements IJsonParser {

    /**
     * This method convert a particular object to JSON. This is mostly used for
     * comparator.
     *
     * @param obj
     * @return
     */
    public String convertToJson(Object obj) {
        Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gsonConverter.toJson(obj);
    }

    /**
     * This method convert a json string to a particular DTO type specified as
     * input. This method returns object of a generic type and therefore casting is
     * not required with this method.
     *
     * @param jsonData
     * @param classType
     * @return Object of type specified as input class
     */
    public <T> T convertFromJson(String jsonData, Class<T> classType) {
        Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gsonConverter.fromJson(jsonData, classType);
    }

    /**
     * This method will be used to convert a dynamic class response from JSON
     *
     * @param jsonData
     * @param type
     * @return
     * @author b0201347
     */
    public <T> T convertFromJson(String jsonData, Type type) {
        Gson gsonConverter = (new GsonBuilder()).disableHtmlEscaping().setPrettyPrinting().create();
        return gsonConverter.fromJson(jsonData, type);
    }

    /**
     * Returns list of object converted from a JSON array string. Input of this
     * method is the Type of list and JSON Array string.
     *
     * @param jsonArray
     * @param listType
     * @return
     */
    public <T> List<T> convertFromJsonArray(String jsonArray, Type listType) {
        Gson gsonConverter = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gsonConverter.fromJson(jsonArray, listType);
    }

    /**
     * This method will fetch value from JSON at a particular JSON path. Value be
     * returned as an Object as the class type is not defined
     *
     * @param inputJson
     * @param keyName
     * @return
     */
    public Object fetchJPathVal(String inputJson, String keyName) {
        Configuration config = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        return Optional.ofNullable(inputJson).map(body -> JsonPath.using(config).parse(body).read(keyName.trim()))
                .orElse(null);
    }

    /**
     * This method will fetch value from JSON at a
     * particular JSON path. Value be returned for the class provided as input.
     *
     * @param inputJson
     * @param keyName
     * @return
     */
    public <T> T fetchJPathVal(String inputJson, String keyName, Class<T> classType) {
        Configuration config = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        return classType.cast(Optional.ofNullable(inputJson)
                .map(body -> JsonPath.using(config).parse(body).read(keyName.trim())).orElse(null));
    }

    /**
     * This method sort  array of object fetched from jspath and on the bases of value of json
     *
     * @param unsortedArrayObj
     * @return
     */
    public Object sortArray(Object unsortedArrayObj) {
        Set sortedArray = new TreeSet<>();
        JSONArray unsortedArray = (JSONArray) unsortedArrayObj;
        for (int arrayIterator = 0; arrayIterator < unsortedArray.size(); arrayIterator++) {
            sortedArray.add(unsortedArray.get(arrayIterator));
        }
        return sortedArray;
    }

    public boolean isStringFeildNotBlankInsideJSONObject(JSONObject jsonObject, String field) {

        return (!jsonObject.getString(field).isEmpty() && jsonObject.getString(field) != null);
    }

    public boolean isJsonObjectInsideJsonObjectNotBlank(JSONObject jsonObject, String field) {
        return (jsonObject.getJSONObject(field) != null && jsonObject.getJSONObject(field).length() != 0);
    }

    public boolean isJsonArrayNotBlank(JsonArray jsonArray) {
        return ((jsonArray != null) && jsonArray.size() > 0);

    }

    public boolean isListFieldBlank(JSONObject jsonObject, String field) {
        return ((jsonObject.getJSONArray(field).length() != 0) && (jsonObject.getJSONArray(field) != null));
    }

    /**
     * This method is used to convert object to jsonArray if
     * it is JsonArray else it will return null.
     *
     * @param object
     * @return
     */
    public org.json.JSONArray objectToJSONArray(Object object) {
        Object json = null;
        org.json.JSONArray jsonArray = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
           // apiHelperLog.log(Level.ERROR, "Json is not valid " + e.getStackTrace());
            e.printStackTrace();
        }
        if (json instanceof org.json.JSONArray) {
            jsonArray = (org.json.JSONArray) json;
        }
        return jsonArray;
    }

    /**
     * This method is used to convert Json Array Object
     * and its values to String Object
     *
     * @param object
     * @return
     */
    public Object convertJsonArrayObjToStringObj(Object object) {
        org.json.JSONArray jsonArray = objectToJSONArray(object);
        org.json.JSONArray destJsonarray = new org.json.JSONArray();

        if (jsonArray != null) {

            for (int i = 0; i < jsonArray.length(); i++) {
                destJsonarray.put(jsonArray.get(i).toString());
            }
            return destJsonarray.toString();
        } else {
            return object.toString();
        }
    }

}
