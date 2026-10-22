package com.airtel.helper.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.LogConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import com.airtel.helper.constants.Constants;
import com.airtel.helper.exception.resourceloader.ConfigServiceException;
import com.google.common.base.MoreObjects;

public class AirtelLoggingMgr {

	private static Logger logger = LogManager.getLogger(AirtelLoggingMgr.class);

	public static File logXMLConfigFile;

	public static String templateFile;

	public static String templateDir;

	private static LoggerContext loggerContext;

	static {
		initializeLogger();
	}

	private static void initializeLogger() {
		try {
			configureAndWatch();
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
	}

	private static synchronized void configureAndWatch() throws IOException, ConfigServiceException {
		Properties properties = loadClasspathProperties(Constants.AIRTEL_COMMON_CONFIG);
		String logFileName = properties.getProperty(Constants.LOG_NAME_KEY);
		if (logFileName != null) {
			ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(),
					AirtelLoggingMgr.class.getClassLoader());
			URL logFilepath = loader.getResource(logFileName);
			if (logFilepath != null) {
				setLoggerContext(new File(logFilepath.getFile()));
			}
		}
	}

	private static Properties loadClasspathProperties(String resourceName) throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(),
				AirtelLoggingMgr.class.getClassLoader());
		try (InputStream in = loader.getResourceAsStream(resourceName)) {
			if (in != null) {
				properties.load(in);
			}
		}
		return properties;
	}

	public static <T> Logger getLogger(Class<T> name) {
		Logger logger = null;
		try {
			return LogManager.getLogger(name);
		} catch (LogConfigurationException ex) {
			ex.printStackTrace();
		}
		return logger;
	}

	public static void setLoggerContext(File configFile) throws IOException {
		loggerContext = (LoggerContext) LogManager.getContext(false);
		InputStream in = new FileInputStream(configFile);
		ConfigurationSource source = new ConfigurationSource(in, configFile);
		Configuration updated = ConfigurationFactory.getInstance().getConfiguration(loggerContext, source);
		loggerContext.start(updated);
	}
}
