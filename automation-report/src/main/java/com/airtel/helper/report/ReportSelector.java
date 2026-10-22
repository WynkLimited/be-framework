package com.airtel.helper.report;

/**
 * Facade for LOBs: which report to wire for the configured {@link FrameworkType}.
 */
public final class ReportSelector {

	private ReportSelector() {
	}

	public static FrameworkType current() {
		return ReportFrameworkConfig.getFrameworkType();
	}

	/**
	 * Cucumber plugin string when framework is BDD; empty string otherwise
	 * (so LOBs can always pass it into {@code @CucumberOptions} safely if they check
	 * {@link #isBdd()}).
	 */
	public static String cucumberPlugin() {
		return ReportFrameworkConfig.isChainTestCucumberEnabled()
				? ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN
				: "";
	}

	public static boolean isBdd() {
		return ReportFrameworkConfig.isBdd();
	}

	public static boolean isTestNg() {
		return ReportFrameworkConfig.isTestNg();
	}

	/**
	 * TestNG LOBs: use {@link AirtelReportListener} (ChainTest + CatchException).
	 * BDD LOBs: still register it only if you run Cucumber via TestNG and want
	 * {@link CatchException}; ChainTest HTML comes from the Cucumber plugin.
	 */
	public static Class<AirtelReportListener> testNgListener() {
		return AirtelReportListener.class;
	}

	/** Whether LOB should extend {@link ReportHelper} for suite HTML lifecycle. */
	public static boolean shouldExtendReportHelper() {
		return ReportFrameworkConfig.isHtmlLifecycleEnabled();
	}
}
