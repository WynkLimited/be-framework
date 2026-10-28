package com.airtel.helper.report;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single reporting engine: <b>ChainTest</b>.
 * LOB picks style once; the matching ChainTest plugin is used.
 * <ul>
 * <li>{@link FrameworkType#TESTNG} → {@code chaintest-testng}
 * ({@link AirtelReportListener})</li>
 * <li>{@link FrameworkType#BDD} → {@code chaintest-cucumber-jvm}
 * (Cucumber plugin {@link #CUCUMBER_CHAINTEST_PLUGIN})</li>
 * </ul>
 *
 * <pre>
 * # src/test/resources/automation-report.properties
 * automation.report.framework=TESTNG
 * # or
 * automation.report.framework=BDD
 * </pre>
 *
 */
public final class ReportFrameworkConfig {

	public static final String SYSTEM_PROPERTY = "automation.report.framework";
	public static final String PROPERTIES_FILE = "automation-report.properties";
	public static final String PROPERTY_KEY = "automation.report.framework";

	/** Maven artifact for TestNG LOBs (already on framework classpath). */
	public static final String CHAINTTEST_TESTNG_ARTIFACT = "com.aventstack:chaintest-testng";

	/** Maven artifact for BDD LOBs — add explicitly (optional in framework POM). */
	public static final String CHAINTTEST_CUCUMBER_ARTIFACT = "com.aventstack:chaintest-cucumber-jvm";

	/** Cucumber {@code @CucumberOptions(plugin = ...)} value when framework=BDD. */
	public static final String CUCUMBER_CHAINTEST_PLUGIN =
			"com.aventstack.chaintest.plugins.ChainTestCucumberListener:";

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportFrameworkConfig.class);

	private static volatile FrameworkType cachedType;

	private ReportFrameworkConfig() {
	}

	public static FrameworkType getFrameworkType() {
		FrameworkType local = cachedType;
		if (local == null) {
			synchronized (ReportFrameworkConfig.class) {
				local = cachedType;
				if (local == null) {
					local = resolveType();
					cachedType = local;
					LOGGER.info("ChainTest mode: {} ({})", local,
							local == FrameworkType.BDD ? CHAINTTEST_CUCUMBER_ARTIFACT : CHAINTTEST_TESTNG_ARTIFACT);
				}
			}
		}
		return local;
	}

	public static void reload() {
		cachedType = null;
	}

	public static boolean isTestNg() {
		return getFrameworkType() == FrameworkType.TESTNG;
	}

	public static boolean isBdd() {
		return getFrameworkType() == FrameworkType.BDD;
	}

	/** ChainTest TestNG listener — when {@code automation.report.framework=TESTNG}. */
	public static boolean isChainTestTestNgEnabled() {
		return isTestNg();
	}

	/** ChainTest Cucumber plugin — when {@code automation.report.framework=BDD}. */
	public static boolean isChainTestCucumberEnabled() {
		return isBdd();
	}

	/** Old suite HTML lifecycle removed — ChainTest is the only suite report. */
	public static boolean isHtmlLifecycleEnabled() {
		return false;
	}

	private static FrameworkType resolveType() {
		String fromSystem = System.getProperty(SYSTEM_PROPERTY);
		if (fromSystem != null && !fromSystem.isBlank()) {
			return FrameworkType.from(fromSystem);
		}
		Properties props = loadProperties();
		if (props != null) {
			String fromFile = props.getProperty(PROPERTY_KEY);
			if (fromFile != null && !fromFile.isBlank()) {
				return FrameworkType.from(fromFile);
			}
		}
		return FrameworkType.TESTNG;
	}

	private static Properties loadProperties() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ReportFrameworkConfig.class.getClassLoader();
		}
		try (InputStream in = loader.getResourceAsStream(PROPERTIES_FILE)) {
			if (in == null) {
				return null;
			}
			Properties props = new Properties();
			props.load(in);
			return props;
		} catch (Exception e) {
			LOGGER.warn("Could not read {}: {}", PROPERTIES_FILE, e.getMessage());
			return null;
		}
	}
}
