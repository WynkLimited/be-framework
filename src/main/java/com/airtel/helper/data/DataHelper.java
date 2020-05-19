package com.airtel.helper.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.airtel.helper.report.ReportHelper;

public class DataHelper {
    public Object readJsonFile(String file) {
        JSONParser parser = new JSONParser();
        Object object = null;
        try {
            object = parser.parse(new FileReader(file));

        } catch (Exception e) {
            Reporter.log("Exception in Reading JSON  file " + file + e.getStackTrace());
        }

        return object;
    }

    public String readFile(String file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            reader.close();
        } catch (IOException e) {
            Reporter.log("Could not find file " + file);
        }
        return stringBuilder.toString();
    }

    public List<String> executeShelCommand(String command) {
        List<String> outputData = new ArrayList<>();
        try {
            String dataString = null;
            Process execCommand = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            BufferedReader commandRespReader = new BufferedReader(new InputStreamReader(execCommand.getInputStream()));
            while ((dataString = commandRespReader.readLine()) != null) {
                outputData.add(dataString);
            }
        } catch (Exception e) {
            Reporter.log("Couldn't execute command");
        }
        return outputData;
    }

    /**
     * This method retrieves name of all properties present in a property file.
     *
     * @param filePath
     * @return Set<String> - containing all properties
     */
    public Set<String> getAllProperty(String filePath) {
        try {
            Properties prop = new Properties();
            InputStream inpStream = null;
            inpStream = new FileInputStream(filePath);
            prop.load(inpStream);
            return prop.stringPropertyNames();
        } catch (IOException e) {
            Reporter.log("Filepath mentioned is not correct and method breaks with exception " + e);
            return new HashSet<>();
        }
    }

    /**
     * This method will load all properties and it's value to a map.
     *
     * @param filePath
     * @return
     */
    public Map<String, String> fetchAllProperties(String filePath) {
        Map<String, String> propData = new HashMap<>();
        Set<String> propertyName = getAllProperty(filePath);
        for (String prop : propertyName) {
            propData.put(prop, Optional.ofNullable(getValueOfProperty(filePath, prop)).orElse(null));
        }
        return propData;
    }


    /**
     * This method helps in retrieving value of a particular property in a property
     * file.
     *
     * @param filePath
     * @param keyName
     * @return
     */
    public String getValueOfProperty(String filePath, String keyName) {
        try {
            Properties prop = new Properties();
            InputStream inpStream = null;
            inpStream = new FileInputStream(filePath);
            prop.load(inpStream);
            return prop.getProperty(keyName);
        } catch (IOException e) {
            //  ReportHelper.log(Level.ERROR, "Filepath mentioned is not correct " + e.getStackTrace());
            return null;
        }
    }

    /**
     * This method is used to set a property in a property file.
     *
     * @param filePath
     * @param keyName
     * @param value
     */
    public void setValueOfProperty(String filePath, String keyName, String value) {
        try {
            Properties prop = new Properties();

            FileInputStream in = new FileInputStream(filePath);
            prop.load(in);
            in.close();

            OutputStream outStream = null;
            outStream = new FileOutputStream(filePath);
            prop.setProperty(keyName, value);
            prop.store(outStream, null);
            outStream.close();
        } catch (IOException e) {
            Reporter.log("Filepath mentioned is not correct and method breaks with exception " + e);
        }
    }

    /**
     * Returns a generated URL based on the input parameters. This will be used by
     * the method that fetch data for a particular API.
     *
     * @param configFile
     * @param serverInitials
     * @param endpoint
     * @return
     */
    public String generateAPIUrl(String configFile, String serverInitials, String endpoint) {
        String serverIp = getValueOfProperty(configFile, serverInitials);
        String serviceUrl;
        if (endpoint != null)
            serviceUrl = serverIp + endpoint;
        else
            serviceUrl = serverIp;
        return serviceUrl;
    }

    public String[] getCommaSeperatedValues(String value) {
        return value.split(",");
    }

    public boolean validateObject(Object obj) {
        return (obj != null && obj != "");
    }

    public String convertToXml(Object obj) {
        String xmlData = null;
        JAXB.marshal(obj, xmlData);
        return xmlData;
    }

    public Object convertFromXml(String xmlData, Class<?> classType) {
        Object objFromXml = null;
        objFromXml = JAXB.unmarshal(xmlData, classType);
        return objFromXml;
    }

    public boolean validateString(String strVal) {
        return (strVal != null && !strVal.trim().isEmpty());
    }

    public boolean verifyObject(JSONObject responseObj, String esFieldName) {
        if (responseObj.get(esFieldName) != null) {
            ReportHelper.logValidationFailure("ES LOG " + esFieldName, "object", "null", "ES LOG Field Found Null");
            return false;
        }
        return true;
    }

    public String getValueFromXLResponse(String xmlResponse, String tagName, int index) {
        String value = "";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        InputSource inputSource;
        try {
            documentBuilder = factory.newDocumentBuilder();
            inputSource = new InputSource(new StringReader(xmlResponse));
            Document doc = documentBuilder.parse(inputSource);
            NodeList list = doc.getElementsByTagName(tagName);
            value = list.item(index).getTextContent();
        } catch (Exception e) {
            ReportHelper.logValidationFailure("xml response parse failure", "attribute Value", e.getMessage(),
                    "Exception In  XML Response");

        }
        return value;
    }
}

