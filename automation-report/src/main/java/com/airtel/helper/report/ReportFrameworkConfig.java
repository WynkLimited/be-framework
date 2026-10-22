package com.airtel.helper.report;

import java.io.InputStream;
import java.util.Properties;

import com.airtel.helper.logging.AirtelLoggingMgr;

/**
 * Resolves which report stack to use for the LOB.
 * <p>
 * Priority (highest first):
 * <ol>
 * <li>System property {@value #SYSTEM_PROPERTY}</li>
 * <li>Classpath {@value #PROPERTIES_FILE} key {@value #PROPERTY_KEY}</li>
 * <li>Default: {@link FrameworkType#TESTNG}</li>
 * </ol>
 *
 * Example {@code automation-report.properties}:
 * <pre>
 * automation.report.framework=BDD
 * </pre>
 * Or: {@code -Dautomation.report.framework=BDD}
 */
public final class ReportFrameworkConfig {

	public static final String SYSTEM_PROPERTY = "automation.report.framework";
	public static final String PROPERTIES_FILE = "automation-report.properties";
	public static final String PROPERTY_KEY = "automation.report.framework";

	/** Cucumber plugin entry for ChainTest (use in {@code @CucumberOptions}). */
	public static final String CUCUMBER_CHAINTEST_PLUGIN =
			"com.aventstack.chaintest.plugins.ChainTestCucumberListener:";

	private static final org.apache.logging.log4j.Logger LOGGER =
			AirtelLoggingMgr.getLogger(ReportFrameworkConfig.class);

	private static volatile FrameworkType cached;

	private ReportFrameworkConfig() {
	}

	public static FrameworkType getFrameworkType() {
		FrameworkType local = cached;
		if (local == null) {
			synchronized (ReportFrameworkConfig.class) {
				local = cached;
				if (local == null) {
					local = resolve();
					cached = local;
					LOGGER.info("Report framework type resolved to: {}", local);
				}
			}
		}
		return local;
	}

	/** Clears cache — for tests or when LOB sets the system property late. */
	public static void reload() {
		cached = null;
	}

	public static boolean isTestNg() {
		return getFrameworkType() == FrameworkType.TESTNG;
	}

	public static boolean isBdd() {
		return getFrameworkType() == FrameworkType.BDD;
	}

	/**
	 * HTML {@link ReportHelper} suite/method lifecycle is intended for TestNG LOBs.
	 * Helper methods ({@code reporterLogging}, etc.) remain usable in both modes.
	 */
	public static boolean isHtmlLifecycleEnabled() {
		return isTestNg();
	}

	/** ChainTest TestNG listener — for {@link FrameworkType#TESTNG}. */
	public static boolean isChainTestTestNgEnabled() {
		return isTestNg();
	}

	/** ChainTest Cucumber plugin — for {@link FrameworkType#BDD}. */
	public static boolean isChainTestCucumberEnabled() {
		return isBdd();
	}

	private static FrameworkType resolve() {
		String fromSystem = System.getProperty(SYSTEM_PROPERTY);
		if (fromSystem != null && !fromSystem.isBlank()) {
			return FrameworkType.from(fromSystem);
		}
		String fromFile = loadFromClasspath();
		if (fromFile != null && !fromFile.isBlank()) {
			return FrameworkType.from(fromFile);
		}
		return FrameworkType.TESTNG;
	}

	private static String loadFromClasspath() {
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
			return props.getProperty(PROPERTY_KEY);
		} catch (Exception e) {
			LOGGER.warn("Could not read {}: {}", PROPERTIES_FILE, e.getMessage());
			return null;
		}
	}
}
