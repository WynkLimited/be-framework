package com.airtel.helper.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.airtel.helper.report.ReportHelper;

/**
 * Shared file / property / XML helpers for LOB automation.
 */
public class DataHelper extends JsonParser {

	private static final Logger log = LoggerFactory.getLogger(DataHelper.class);

	public Object readJsonFile(String file) {
		JSONParser parser = new JSONParser();
		try (BufferedReader reader = Files.newBufferedReader(Path.of(file), StandardCharsets.UTF_8)) {
			return parser.parse(reader);
		} catch (Exception e) {
			log.error("Exception reading JSON file {}", file, e);
			return null;
		}
	}

	public String readFile(String file) {
		try {
			return Files.readString(Path.of(file), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error("Could not read file {}", file, e);
			return "";
		}
	}

	public List<String> executeShelCommand(String command) {
		List<String> outputData = new ArrayList<>();
		try {
			Process execCommand = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
			try (BufferedReader commandRespReader = new BufferedReader(
					new InputStreamReader(execCommand.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = commandRespReader.readLine()) != null) {
					outputData.add(line);
				}
			}
			execCommand.waitFor();
		} catch (Exception e) {
			log.error("Couldn't execute command: {}", command, e);
		}
		return outputData;
	}

	public Set<String> getAllProperty(String filePath) {
		try (InputStream in = Files.newInputStream(Path.of(filePath))) {
			Properties prop = new Properties();
			prop.load(in);
			return prop.stringPropertyNames();
		} catch (IOException e) {
			log.error("Invalid property file path {}", filePath, e);
			return new HashSet<>();
		}
	}

	public Map<String, String> fetchAllProperties(String filePath) {
		Map<String, String> propData = new HashMap<>();
		for (String prop : getAllProperty(filePath)) {
			propData.put(prop, Optional.ofNullable(getValueOfProperty(filePath, prop)).orElse(null));
		}
		return propData;
	}

	public String getValueOfProperty(String filePath, String keyName) {
		try (InputStream in = Files.newInputStream(Path.of(filePath))) {
			Properties prop = new Properties();
			prop.load(in);
			return prop.getProperty(keyName);
		} catch (IOException e) {
			log.error("Cannot read property {} from {}", keyName, filePath, e);
			return null;
		}
	}

	public void setValueOfProperty(String filePath, String keyName, String value) {
		Path path = Path.of(filePath);
		Properties prop = new Properties();
		try (InputStream in = Files.newInputStream(path)) {
			prop.load(in);
		} catch (IOException e) {
			log.error("Cannot load properties from {}", filePath, e);
			return;
		}
		prop.setProperty(keyName, value);
		try (OutputStream out = Files.newOutputStream(path)) {
			prop.store(out, null);
		} catch (IOException e) {
			log.error("Cannot store properties to {}", filePath, e);
		}
	}

	public String generateAPIUrl(String configFile, String serverInitials, String endpoint) {
		String serverIp = getValueOfProperty(configFile, serverInitials);
		if (serverIp == null) {
			return endpoint;
		}
		return endpoint != null ? serverIp + endpoint : serverIp;
	}

	public String[] getCommaSeperatedValues(String value) {
		return value == null ? new String[0] : value.split(",");
	}

	public boolean validateObject(Object obj) {
		return obj != null && !"".equals(obj);
	}

	public String convertToXml(Object obj) {
		StringWriter writer = new StringWriter();
		JAXB.marshal(obj, writer);
		return writer.toString();
	}

	public Object convertFromXml(String xmlData, Class<?> classType) {
		return JAXB.unmarshal(new StringReader(xmlData), classType);
	}

	public boolean validateString(String strVal) {
		return strVal != null && !strVal.trim().isEmpty();
	}

	public boolean verifyObject(JSONObject responseObj, String esFieldName) {
		if (responseObj == null || responseObj.isNull(esFieldName) || responseObj.opt(esFieldName) == null) {
			ReportHelper.logValidationFailure("ES LOG " + esFieldName, "object", "null", "ES LOG Field Found Null");
			return false;
		}
		return true;
	}

	public String getValueFromXLResponse(String xmlResponse, String tagName, int index) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			Document doc = factory.newDocumentBuilder()
					.parse(new InputSource(new StringReader(xmlResponse)));
			NodeList list = doc.getElementsByTagName(tagName);
			if (list == null || index < 0 || index >= list.getLength()) {
				return "";
			}
			return list.item(index).getTextContent();
		} catch (Exception e) {
			log.error("XML parse failure for tag {}", tagName, e);
			ReportHelper.logValidationFailure("xml response parse failure", "attribute Value", e.getMessage(),
					"Exception In XML Response");
			return "";
		}
	}
}
