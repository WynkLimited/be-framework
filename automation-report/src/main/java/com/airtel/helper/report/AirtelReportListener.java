package com.airtel.helper.report;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.chaintest.plugins.ChainTestListener;
import com.airtel.helper.logging.AirtelLoggingMgr;

/**
 * Single TestNG entry-point for LOBs. Picks reporters from
 * {@link ReportFrameworkConfig}:
 * <ul>
 * <li>{@link FrameworkType#TESTNG} — ChainTest TestNG + {@link CatchExceptionListener};
 * HTML lifecycle comes from extending {@link ReportHelper}</li>
 * <li>{@link FrameworkType#BDD} — skips ChainTest TestNG (use Cucumber plugin instead);
 * still runs {@link CatchExceptionListener} if scenarios run under TestNG</li>
 * </ul>
 *
 * <pre>
 * {@code
 * @Listeners(AirtelReportListener.class)
 * public class ClmBaseTest extends ReportHelper { }
 * }
 * </pre>
 */
public class AirtelReportListener implements ITestListener, ISuiteListener {

	private static final org.apache.logging.log4j.Logger LOGGER =
			AirtelLoggingMgr.getLogger(AirtelReportListener.class);

	private final CatchExceptionListener catchExceptionListener = new CatchExceptionListener();
	private final ChainTestListener chainTestListener;

	public AirtelReportListener() {
		if (ReportFrameworkConfig.isChainTestTestNgEnabled()) {
			chainTestListener = new ChainTestListener();
			LOGGER.info("AirtelReportListener: TESTNG mode — ChainTest TestNG + CatchException active");
		} else {
			chainTestListener = null;
			LOGGER.info(
					"AirtelReportListener: BDD mode — register Cucumber plugin '{}' in @CucumberOptions; CatchException still active",
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
