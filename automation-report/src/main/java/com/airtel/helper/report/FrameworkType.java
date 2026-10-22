package com.airtel.helper.report;

/**
 * LOB test style. Set via {@code automation.report.framework} so the framework
 * activates the matching report stack.
 */
public enum FrameworkType {

	/** Classic TestNG tests — HTML {@link ReportHelper} + ChainTest TestNG listener. */
	TESTNG,

	/** Cucumber / BDD — ChainTest Cucumber plugin (scenario/step report). */
	BDD;

	public static FrameworkType from(String value) {
		if (value == null || value.isBlank()) {
			return TESTNG;
		}
		String normalized = value.trim().toUpperCase();
		if ("BDD".equals(normalized) || "CUCUMBER".equals(normalized)) {
			return BDD;
		}
		if ("TESTNG".equals(normalized) || "TEST_NG".equals(normalized)) {
			return TESTNG;
		}
		throw new IllegalArgumentException(
				"Unknown automation.report.framework='" + value + "'. Use TESTNG or BDD.");
	}
}
