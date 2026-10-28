package com.airtel.helper.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import com.airtel.helper.constants.Constants;
import com.airtel.helper.exception.ErrorCode;
import com.airtel.helper.exception.resourceloader.ConfigServiceException;
import com.airtel.helper.exception.resourceloader.EmptyConfigFileException;
import com.airtel.helper.exception.resourceloader.ResourceNotFoundException;

/**
 * Helper to read/write LOB config property files under {@link Constants#CONFIG_FOLDER}.
 */
@SuppressWarnings("rawtypes")
public class AirtelConfigService {

	private static HashMap serviceProperty = new HashMap();

	public static void putProperty(String serviceName, String key, String value) throws ConfigServiceException {

		if (null == serviceName || serviceName.trim().length() == 0 || null == key || key.trim().length() == 0
				|| null == value || value.trim().length() == 0) {
			throw new ConfigServiceException("invalid parameters", ErrorCode.CONFIGURATION_PROPERTY);
		}

		synchronized (AirtelConfigService.class) {
			Properties prop = getProperties(serviceName);
			prop.put(key, value);
			File f = new File(Constants.CONFIG_FOLDER + serviceName);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(f);
				prop.store(out, "key [" + key + "] added");
				readFile(serviceName);
			} catch (FileNotFoundException e) {
				throw new ResourceNotFoundException("Config file: '" + serviceName + "' was not found.");
			} catch (Exception e) {
				throw new ResourceNotFoundException("Unable to write to config file: '" + serviceName + "'.");
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException e) {
						out = null;
					}
				}
			}
		}
	}

	public static String getProperty(String serviceName, String key) throws ConfigServiceException {

		try {
			@SuppressWarnings("rawtypes")
			HashMap data = getServiceProperties(serviceName);

			if (data != null && data.size() > 0 && data.containsKey(key)) {
				return data.get(key) + "";
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new ConfigServiceException(e.getMessage(), ErrorCode.CONFIGURATION_PROPERTY, e);
		}
	}

	public static HashMap getServiceProperties(String serviceName)
			throws EmptyConfigFileException, ResourceNotFoundException {

		if (serviceProperty.containsKey(serviceName)) {
			return (HashMap) serviceProperty.get(serviceName);
		} else if (readFile(serviceName) && serviceProperty.containsKey(serviceName)) {
			return (HashMap) serviceProperty.get(serviceName);
		}
		return null;
	}

	private static Properties getProperties(String serviceName) throws ResourceNotFoundException {
		Properties prop = new Properties();
		FileInputStream fis = null;
		File f = new File(Constants.CONFIG_FOLDER + serviceName);
		try {
			fis = new FileInputStream(f);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException("Config file: '" + serviceName + "' was not found.");
		} catch (Exception e) {
			throw new ResourceNotFoundException("Unable to read config file: '" + serviceName + "'.");
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					fis = null;
				}
			}
		}
		return prop;
	}

	@SuppressWarnings("unchecked")
	private static boolean readFile(String serviceName) throws EmptyConfigFileException, ResourceNotFoundException {
		HashMap serviceData = null;

		Properties properties = getProperties(serviceName);
		Enumeration bundleKeys = properties.propertyNames();

		serviceData = new HashMap();

		while (bundleKeys.hasMoreElements()) {
			String key = (String) bundleKeys.nextElement();
			String value = properties.getProperty(key);
			serviceData.put(key.trim(), value.trim());
		}

		if (serviceData != null && serviceData.size() > 0) {
			serviceProperty.put(serviceName, serviceData);
		}

		return true;
	}
}
