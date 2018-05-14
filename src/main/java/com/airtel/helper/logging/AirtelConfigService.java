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
import com.airtel.helper.exception.resourceloader.ConfigServiceException;
import com.airtel.helper.exception.resourceloader.EmptyConfigFileException;
import com.airtel.helper.exception.resourceloader.ResourceNotFoundException;

/**
 *
 * This is a helper services to read config files.
 */
@SuppressWarnings("rawtypes")
public class AirtelConfigService {

	
	private static HashMap serviceProperty = new HashMap();

	/**
	 * Put key and value in a service properties file
	 * 
	 * @param serviceName
	 * @param key
	 * @param value
	 * @throws ConfigServiceException
	 */
	public static void putProperty(String serviceName, String key, String value) throws ConfigServiceException {

		if (null == serviceName || serviceName.trim().length() == 0 || null == key || key.trim().length() == 0
				|| null == value || value.trim().length() == 0) {
			throw new ConfigServiceException("invalid parameters", Constants.ERROR_CODE.CONFIGURATION_PROPERTY);
		}

		synchronized (AirtelConfigService.class) {
			Properties prop = getProperties(serviceName);
			prop.put(key, value);
			File f = new File(Constants.CONFIG_FOLDER + serviceName);
			FileOutputStream out = null;
			try {
				// get file
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
			} // end finally
		} // end synchronized block
	}

	/**
	 * Returns the value of the key from the service properties file
	 * 
	 * @param serviceName
	 *            Properties file name for a service
	 * @param key
	 *            Key
	 * @return Value of the key
	 * @throws ConfigServiceException
	 */
	public static String getProperty(String serviceName, String key) throws ConfigServiceException {

		try {
			// get hashmap for this service
			HashMap data = getServiceProperties(serviceName);

			// check if we have this data
			if (data != null && data.size() > 0 && data.containsKey(key)) {
				return data.get(key) + ""; // return the value
			} else {
				// Print error
				return null;
			}
		} catch (Exception e) {
			throw new ConfigServiceException(e.getMessage(), Constants.ERROR_CODE.CONFIGURATION_PROPERTY);
		}
	}

	/**
	 * Returns the hashmap having all the keys for a service
	 * 
	 * @param serviceName
	 *            Properties file name for a service
	 * @return HashMap having all the keys for a service
	 * @throws EmptyConfigFileException
	 * @throws ResourceNotFoundException
	 */
	public static HashMap getServiceProperties(String serviceName)
			throws EmptyConfigFileException, ResourceNotFoundException {
		// check if the data for requested file exist

		if (serviceProperty.containsKey(serviceName)) {
			// File exist
			return (HashMap) serviceProperty.get(serviceName);
		} else if (readFile(serviceName) && serviceProperty.containsKey(serviceName)) {

			// File found after trying to read again
			return (HashMap) serviceProperty.get(serviceName);
		}
		return null;
	}

	/**
	 * Reads the config file in name value pair
	 * 
	 * @param serviceName
	 *            Properties file name for a service
	 * @return Object of Properties
	 * @throws ResourceNotFoundException
	 */
	private static Properties getProperties(String serviceName) throws ResourceNotFoundException {
		// Getting properties from .config or .properties file
		Properties prop = new Properties();
		FileInputStream fis = null;
		File f = new File(Constants.CONFIG_FOLDER + serviceName);
		try {
			// get file
			fis = new FileInputStream(f);
			// Load the property using the file stream
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

		// Return the property object
		return prop;
	}

	/**
	 * Adds a hashmap having all the keys and values for a service from it's
	 * properties file
	 * 
	 * @param serviceName
	 *            Properties file name for a service
	 * @return true if the file is found
	 * @throws EmptyConfigFileException
	 * @throws ResourceNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static boolean readFile(String serviceName) throws EmptyConfigFileException, ResourceNotFoundException {
		HashMap serviceData = null;

		Properties properties = getProperties(serviceName);
		Enumeration bundleKeys = properties.propertyNames(); // get all properties

		serviceData = new HashMap();

		// read each value
		while (bundleKeys.hasMoreElements()) {
			String key = (String) bundleKeys.nextElement();
			String value = properties.getProperty(key);
			serviceData.put(key.trim(), value.trim());// add to HashMap
		}

		// add to static hashmap if the data exist
		if (serviceData != null && serviceData.size() > 0) {
			serviceProperty.put(serviceName, serviceData);
		}

		return true;
	}
}
