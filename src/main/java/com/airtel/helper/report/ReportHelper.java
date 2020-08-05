package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.airtel.Models.Constants;
import com.airtel.Models.DebugLogItemReq;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.airtel.helper.data.InfluxDbHelper;
import com.airtel.helper.logging.AirtelLoggingMgr;
import com.airtel.listener.testng.Vangaurd;
import com.report.SentinelLogger;

@Listeners(Vangaurd.class)
public class ReportHelper implements ReportConstants {

	static int count = 0;
	static int passCount = 0;
	static int failCount = 0;
	String newDate = null;
	Date startTime = null;
	private static String resultFolderpath;
	private static Logger logger = AirtelLoggingMgr.getLogger(ReportHelper.class);
	private static ThreadLocal<String> threadLocalResultFileStringPath = new InheritableThreadLocal<String>();

	public static String getResultFolderpath() {
		return resultFolderpath;
	}

	public void setResultFolderpath(String resultFolderpath) {
		ReportHelper.resultFolderpath = resultFolderpath;
	}

	private static String suiteFileStringPath;
	// private static String resultFileStringPath;

	public static String getResultFileStringPath() {
		return threadLocalResultFileStringPath.get();
	}

	public void setResultFileStringPath(String resultFileStringPath) {
		threadLocalResultFileStringPath.set(resultFileStringPath);
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		String resultsFolderPath = "./Results";
		try {
			File resultFolder = new File(resultsFolderPath);
			FileUtils.deleteDirectory(resultFolder);
			resultFolder.mkdir();

			startTime = new Date();
			SimpleDateFormat oDateFormat = new SimpleDateFormat("dd_MM_YY");
			newDate = oDateFormat.format(startTime);

			resultFolderpath = resultsFolderPath + "/Result_" + newDate;
			File resultFileFolder = new File(resultFolderpath);
			resultFileFolder.mkdir();

			suiteFileStringPath = resultsFolderPath + "/AirtelTestResult_" + newDate + ".html";
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, false));
			resultFile.append(REPORT_START_TEMPLATE);
			resultFile.append(DETAILED_REPORT_TITLE);
			resultFile.append(DETAILED_REPORT_BODY_START + "Test Scenario" + REPORT_COLUMN_END);
			resultFile.append(DETAILED_REPORT_BODY_START + "Test Scenario File" + REPORT_COLUMN_END);
			resultFile.append(DETAILED_REPORT_BODY_START + "Status" + REPORT_COLUMN_END);
			resultFile.close();
			setResultFolderpath(resultFolderpath);
		} catch (IOException ex) {
			logger.error("Result file creation failed with exception - " + ex);
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite(ITestContext context) {
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true));
			Date testEndTime = new Date();
			long testDuration = testEndTime.getTime() - startTime.getTime();
			String testSuiteName = context.getCurrentXmlTest().getSuite().getFileName();
			int index = testSuiteName.lastIndexOf('/');
			testSuiteName = testSuiteName.substring(index + 1).replace(".xml", "");
			String git_commit_id = Optional.ofNullable(System.getProperty("git_commit_id")).orElse("preprod_run");
			InfluxDbHelper.populateAutomationStats(testDuration, testSuiteName, git_commit_id, passCount, failCount);

			resultFile.append("</table>");
			resultFile.append(SUMMARY_REPORT_TEMPLATE);

			resultFile.append(SUMMARY_REPORT_ROW_START + "Run Start Time" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + startTime + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Run End Time" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + testEndTime + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Test Run Duration(in ms)" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + testDuration + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Total Tests executed" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + String.valueOf(passCount + failCount) + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Total Pass Test count" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + String.valueOf(passCount) + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Total Fail Test count" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + String.valueOf(failCount) + "</b></td></tr>");

			resultFile.append("</table></body></html>");
			resultFile.close();
		} catch (Exception ex) {
			logger.error("Unable to close result file - " + ex);
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void createinitialHTMLReport(Method method) {
		try {
			UUID randNo = UUID.randomUUID();
			setResultFileStringPath(resultFolderpath + "/" + method.getName().toString() + "_" + randNo + ".html");
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(getResultFileStringPath(), true));
			resultFile.append(REQ_RESP_LOG_TEMPLATE);
			resultFile.close();
			// setResultFileStringPath(resultFileStringPath);
		} catch (Exception e) {
			setResultFileStringPath(null);
			logger.error("Initial error report not created - " + e);
		}
	}

	@AfterMethod(alwaysRun = true)
	public void appendFinalHTMLReport(ITestResult result) {

		ITestNGMethod testNGMethod = result.getMethod();
		try {
			int number = testNGMethod.getCurrentInvocationCount();
			String status = "Pass";
			String scenario = "";

			if (result.getParameters().length != 0) {
				for (Object parameter : result.getParameters()) {
					if (parameter instanceof Map && ((((Map) (parameter)).containsKey("TC_DESCRIPTION"))
							|| ((Map) (parameter)).containsKey("TCDESCRIPTION"))) {

						if (((Map) (parameter)).containsKey("TC_DESCRIPTION"))
							scenario = ((Map) (parameter)).get("TC_DESCRIPTION").toString();
						else
							scenario = ((Map) (parameter)).get("TCDESCRIPTION").toString();

						break;
					}
				}
			}
			if (scenario.equals("")) {
				if (result.getMethod().getDescription() != null) {
					scenario = result.getMethod().getDescription();
				}
			}

			if (System.getProperty("Test Status") != null
					&& TESTRESULT_WARNING.equalsIgnoreCase(System.getProperty("Test Status"))) {
				status = TESTRESULT_WARNING;
				passCount = passCount + 1;
			} else if (result.getStatus() == ITestResult.FAILURE) {
				status = "Fail";
				failCount = failCount + 1;
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				passCount = passCount + 1;
			}

			count = count + 1;
			String resultFilePath = getResultFileStringPath().split("/Results")[1];
			resultFilePath = "." + resultFilePath;

			if (result.getStatus() != ITestResult.SKIP) {
				appendMethodInformationInSuite(scenario,
						result.getMethod().getMethodName() + "_" + String.valueOf(number), resultFilePath, status);
				BufferedWriter resultFile = new BufferedWriter(new FileWriter(getResultFileStringPath(), true));

				resultFile.append("</table></body></html>");
				resultFile.close();
			}
			System.clearProperty("Test Status");
		} catch (IOException e) {
			logger.error("Report not completed - " + e);
		}
	}

	public void appendMethodInformationInSuite(String scenarioName, String methodName, String methodFilePath,
			String status) {
		try {
			String statusTag = null;
			if (status.equalsIgnoreCase("Pass")) {
				statusTag = TEST_PASS_TAG;
			} else if (status.equalsIgnoreCase("Fail")) {
				statusTag = TEST_FAIL_TAG;
			} else if (status.equalsIgnoreCase(TESTRESULT_WARNING)) {
				statusTag = TEST_WARN_TAG;
			} else {
				statusTag = "<td>";
			}
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true));
			resultFile.append("<tr>");
			resultFile.append("<td>" + scenarioName + "</td>");
			resultFile.append("<td><a href='" + methodFilePath + "' />" + methodName + "</td>");
			resultFile.append(statusTag + status + "</td>");
			resultFile.append("</tr>");
			resultFile.close();
		} catch (IOException e) {
			logger.error("Error in appending information to suite - " + e);
		}
	}

	public static void appendresultHTMLReport(String resultFileStringPath, String apiURL, String requestFile,
			String responseFile, String message, String status, String comment, String headers) {
		try {

			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			String statusTag = null;
			if (status.equalsIgnoreCase("True") || status.equalsIgnoreCase("Pass")) {
				statusTag = TEST_PASS_TAG;
			} else if (status.equalsIgnoreCase("false") || status.equalsIgnoreCase("Fail")) {
				statusTag = TEST_FAIL_TAG;
			} else if (status.equalsIgnoreCase(TESTRESULT_WARNING)) {
				statusTag = TEST_WARN_TAG;
			} else {
				statusTag = "<td>";
			}

			resultFile.append("<tr>");
			resultFile.append("<td width=30% style=WORD-break:BREAK-all>" + apiURL + "</td>");
			resultFile.append("<td width=10% style=WORD-break:BREAK-all>" + headers + "</td>");
			resultFile.append("<td width=10% style=WORD-break:BREAK-all>" + requestFile + "</td>");
			resultFile.append("<td width=10% style=WORD-break:BREAK-all>" + responseFile + "</td>");
			resultFile.append("<td width=30% style=WORD-break:BREAK-all>" + message + "</td>");
			resultFile.append(statusTag + status + "</td>");
			resultFile.append("<td style=word-wrap: break-word>" + comment + "</td>");
			resultFile.append("</tr>");
			resultFile.close();

		} catch (Exception e) {
			logger.error("Append to HTML failed - " + e.getStackTrace());
		}
	}

	public static boolean reporterLogging(Boolean status, String sMessage) {
		return reporterLogging(String.valueOf(status), sMessage);
	}

	public static boolean reporterLogging(String status, String sMessage) {
        DebugLogItemReq debugLogItemReq = new DebugLogItemReq();
		try {
			appendresultHTMLReport(getResultFileStringPath(), "", "", "", sMessage, String.valueOf(status),
					Thread.currentThread().getStackTrace()[2].getMethodName(), "");
			Reporter.log("<br>");
			if (status.equalsIgnoreCase("True")) {
				debugLogItemReq.setAssertStatus("SUCCESS");
				debugLogItemReq.setMessage(sMessage);
				Reporter.log("<Font Color=#008000> PASS </Font>" + sMessage);
				SentinelLogger.info(debugLogItemReq);
			} else if (status.equalsIgnoreCase(TESTRESULT_WARNING)) {
				debugLogItemReq.setAssertStatus("WARNING");
				debugLogItemReq.setMessage(TESTRESULT_WARNING + ": " + sMessage);
				Reporter.log("<Font Color=#FFEA62> WARN </Font>" + sMessage);
				SentinelLogger.info(debugLogItemReq);
			} else if (status.equalsIgnoreCase(TESTRESULT_HTMLREPORT)) {
				debugLogItemReq.setAssertStatus("FAILED");
				debugLogItemReq.setMessage(sMessage);
				Reporter.log("<Font Color=red> FAIL </Font> " + sMessage);
			}
			else {
				debugLogItemReq.setAssertStatus("FAILED");
				debugLogItemReq.setMessage(sMessage);
				Reporter.log("<Font Color=red> FAIL </Font> " + sMessage);
				SentinelLogger.info(debugLogItemReq);
			}

		} catch (Exception e) {
			logger.error("Not able to log in report - " + e);
		}
		return Boolean.getBoolean(status);
	}

	/**
	 * This method will be used to log failures for interactive testing of api.
	 *
	 * @param fieldName     - Name of the field for which comparison has failed
	 * @param expectedValue - Expected value of the field
	 * @param actualValue   - Actual value of the field
	 * @param failureType   - Type of failure that will be the hyperlink for report.
	 *                      For e.g. - Interactive failure, ES logs failure,
	 */
	public static void logValidationFailure(String fieldName, String expectedValue, String actualValue,
			String failureType) {
		logValidationFailureWithRequest(fieldName, expectedValue, actualValue, failureType, null);
	}

	/**
	 * This method will be used to log failures for interactive testing of api along
	 * with request.
	 *
	 * @param fieldName     - Name of the field for which comparison has failed
	 * @param expectedValue - Expected value of the field
	 * @param actualValue   - Actual value of the field
	 * @param failureType   - Type of failure that will be the hyperlink for report.
	 *                      For e.g. - Interactive failure, ES logs failure,
	 * @param request       - Request for which the validation has failed
	 */
	public static void logValidationFailureWithRequest(String fieldName, String expectedValue, String actualValue,
			String failureType, String request) {
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		String compareFilePath = null;
		String linkCompareFilePath = null;
		if (request == null) {
			compareFilePath = compareHtmlObj.createCompartarReport();
		} else {
			compareFilePath = compareHtmlObj.createCompartorReport(request);
		}
		try {
			linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];
			compareHtmlObj.appendresultHTMLReport(compareFilePath,
					"<textarea disabled rows='7' cols='55'>" + failureType + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + fieldName + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + expectedValue + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + actualValue + "</textarea>");
			compareHtmlObj.appendFinalHTMLReport(compareFilePath);
			String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>" + failureType + "</a>";
			ReportHelper.reporterLogging(TESTRESULT_HTMLREPORT, differenceHTMLLink);
		} catch (Exception e) {
			logger.error("File path is not specified correctly - " + e);
		}
	}
}