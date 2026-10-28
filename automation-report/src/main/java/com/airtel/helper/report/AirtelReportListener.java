package com.airtel.helper.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.chaintest.plugins.ChainTestListener;

/**
 * TestNG wiring for ChainTest when {@code automation.report.framework=TESTNG}.
 * <p>
 * For BDD, do <b>not</b> rely on this for reporting — use
 * {@link ReportFrameworkConfig#CUCUMBER_CHAINTEST_PLUGIN} in {@code @CucumberOptions}
 * and depend on {@code chaintest-cucumber-jvm}.
 */
public class AirtelReportListener implements ITestListener, ISuiteListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AirtelReportListener.class);

	private final CatchExceptionListener catchExceptionListener = new CatchExceptionListener();
	private final ChainTestListener chainTestListener;

	public AirtelReportListener() {
		if (ReportFrameworkConfig.isChainTestTestNgEnabled()) {
			chainTestListener = new ChainTestListener();
			LOGGER.info("ChainTest (TestNG) active — dependency {}",
					ReportFrameworkConfig.CHAINTTEST_TESTNG_ARTIFACT);
		} else {
			chainTestListener = null;
			LOGGER.info(
					"BDD mode: ChainTest Cucumber plugin expected ({}). Add dependency {} and plugin '{}'",
					ReportFrameworkConfig.getFrameworkType(),
					ReportFrameworkConfig.CHAINTTEST_CUCUMBER_ARTIFACT,
					ReportFrameworkConfig.CUCUMBER_CHAINTEST_PLUGIN);
		}
	}

	@Override
	public void onStart(ISuite suite) {
		if (chainTestListener != null) {
			chainTestListener.onStart(suite);
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		if (chainTestListener != null) {
			chainTestListener.onFinish(suite);
		}
	}

	@Override
	public void onStart(ITestContext context) {
		if (chainTestListener != null) {
			chainTestListener.onStart(context);
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		if (chainTestListener != null) {
			chainTestListener.onFinish(context);
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		if (chainTestListener != null) {
			chainTestListener.onTestStart(result);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		if (chainTestListener != null) {
			chainTestListener.onTestSuccess(result);
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		catchExceptionListener.onTestFailure(result);
		if (chainTestListener != null) {
			chainTestListener.onTestFailure(result);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		if (chainTestListener != null) {
			chainTestListener.onTestSkipped(result);
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		if (chainTestListener != null) {
			chainTestListener.onTestFailedButWithinSuccessPercentage(result);
		}
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
		catchExceptionListener.onTestFailure(result);
		if (chainTestListener != null) {
			chainTestListener.onTestFailedWithTimeout(result);
		}
	}
}
