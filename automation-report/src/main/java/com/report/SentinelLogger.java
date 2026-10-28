package com.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentinelLogger {

	private static final Logger logger = LoggerFactory.getLogger(SentinelLogger.class);

	public static void info(Object message) {
		logger.info("{}", message);
	}

	public static void debug(Object message) {
		logger.debug("{}", message);
	}

	public static void error(Object message) {
		logger.error("{}", message);
	}

	public static void trace(Object message) {
		logger.trace("{}", message);
	}
}
