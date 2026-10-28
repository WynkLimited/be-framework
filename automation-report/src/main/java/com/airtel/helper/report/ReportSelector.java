package com.airtel.helper.report;

/**
 * Single entry for LOBs: pick ChainTest flavor from {@link ReportFrameworkConfig}.
 *
 * <pre>
 * // TESTNG
 * {@literal @}Listeners(ReportSelector.testNgListener())
 * public class BaseTest { }
 *
 * // BDD / Cucumber (must be a compile-time constant)
 * {@literal @}CucumberOptions(plugin = { ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN })
 * public class RunCukes extends AbstractTestNGCucumberTests { }
 * </pre>
 */
public final class ReportSelector {

	private ReportSelector() {
	}

	public static FrameworkType current() {
		return ReportFrameworkConfig.getFrameworkType();
	}

	public static boolean isBdd() {
		return ReportFrameworkConfig.isBdd();
	}

	public static boolean isTestNg() {
		return ReportFrameworkConfig.isTestNg();
	}

	/**
	 * ChainTest Cucumber plugin id for {@code @CucumberOptions}.
	 * Only valid when {@link #isBdd()} — otherwise returns empty string.
	 */
	public static String cucumberPlugin() {
		return ReportFrameworkConfig.isChainTestCucumberEnabled()
				? ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN
				: "";
	}

	/**
	 * Always the same constant for compile-time {@code @CucumberOptions}
	 * (use when LOB is BDD-only).
	 */
	public static String cucumberPluginRequired() {
		return ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN;
	}

	/** TestNG LOBs: registers ChainTest TestNG (+ optional CatchException). */
	public static Class<AirtelReportListener> testNgListener() {
		return AirtelReportListener.class;
	}

	/** Legacy HTML {@link ReportHelper} lifecycle (off by default). */
	public static boolean shouldExtendReportHelper() {
		return ReportFrameworkConfig.isHtmlLifecycleEnabled();
	}

	/** Maven coordinate the LOB must have for the active mode. */
	public static String requiredChainTestDependency() {
		return ReportFrameworkConfig.isBdd()
				? ReportFrameworkConfig.CHAINTTEST_CUCUMBER_ARTIFACT
				: ReportFrameworkConfig.CHAINTTEST_TESTNG_ARTIFACT;
	}
}
