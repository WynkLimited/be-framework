
package com.airtel.common.helper.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.LogConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import com.airtel.common.helper.constants.Constants;
import com.airtel.common.helper.exception.resourceloader.ConfigServiceException;
import com.airtel.common.helper.resourceloader.AirtelConfigService;

public class AirtelLoggingMgr {

	private static Logger logger = LogManager.getLogger(AirtelLoggingMgr.class);

	public static File logXMLConfigFile;

	public static String templateFile;

	public static String templateDir;

	private static LoggerContext loggerContext;

	static {
		initializeLogger();
	}

	/**
	 * 
	 */
	private static void initializeLogger() {
		try {
			configureAndWatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IOException
	 * @throws ConfigServiceException
	 */
	private synchronized static void configureAndWatch() throws IOException, ConfigServiceException {
		String logFileName = AirtelConfigService.getProperty(Constants.AIRTEL_COMMON_CONFIG, Constants.LOG_NAME_KEY);
		if (logFileName != null) {
			File f = new File(Constants.CONFIG_FOLDER + logFileName);
			setLoggerContext(f);

		} else {
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {

		Logger logger = null;

		try {

			return LogManager.getLogger(name);

		} catch (LogConfigurationException ex) {
			ex.printStackTrace();
		}
		return logger;

	}

	public static void setLoggerContext(File configFile) throws IOException {
			loggerContext = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
			InputStream in = new FileInputStream(configFile);
			ConfigurationSource source = new ConfigurationSource(in);
			Configuration updated = ConfigurationFactory.getInstance().getConfiguration(loggerContext, source);
			loggerContext.start(updated);
		

	}

	

	public static void main(String args[]) {
//		 Log log = AirtelLoggingMgr.getLogger(AirtelLoggingMgr.class.getName());
//		 log.info("Saurabh");
//		 log.debug("Saurabh debug");
	}
}
