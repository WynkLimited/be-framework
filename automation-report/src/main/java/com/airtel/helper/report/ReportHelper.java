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
import java.util.Scanner;
import java.util.UUID;

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
import org.testng.annotations.Test;
import org.testng.internal.Utils;

import com.airtel.Models.DebugLogItemReq;
import com.airtel.helper.data.InfluxDbHelper;
import com.airtel.helper.logging.AirtelLoggingMgr;
import com.report.SentinelLogger;

//@Listeners(Vangaurd.class)
public class ReportHelper implements ReportConstants {

	static int count = 0;
	static int passCount = 0;
	static int failCount = 0;
	static int warnCount = 0;
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

	public static String getResultFileStringPath() {
		return threadLocalResultFileStringPath.get();
	}

	public void setResultFileStringPath(String resultFileStringPath) {
		threadLocalResultFileStringPath.set(resultFileStringPath);
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() throws IOException {
		if (!ReportFrameworkConfig.isHtmlLifecycleEnabled()) {
			logger.info("HTML ReportHelper lifecycle skipped (automation.report.framework=BDD). Use ChainTest Cucumber plugin.");
			return;
		}
		String resultsFolderPath = "./Results";
		File resultFolder = new File(resultsFolderPath);
		try {
			if (resultFolder.exists()) {
				FileUtils.deleteDirectory(resultFolder);
			}
			resultFolder.mkdirs();

			startTime = new Date();
			// Keep year as 'yy' rather than 'YY' (week-year can jump early).
			SimpleDateFormat folderDateFormat = new SimpleDateFormat("dd_MM_yy");
			newDate = folderDateFormat.format(startTime);

			resultFolderpath = resultsFolderPath + "/Result_" + newDate;
			new File(resultFolderpath).mkdirs();
			suiteFileStringPath = resultsFolderPath + "/AirtelTestResult_" + newDate + ".html";
			setResultFolderpath(resultFolderpath);

			SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
			String sRunStartTime = displayFormat.format(startTime);
			String sUserReq = Optional.ofNullable(System.getenv("ComputerName"))
					.orElse(Optional.ofNullable(System.getProperty("user.name")).orElse("N/A"));
			String sEnvName = System.getProperty("os.name");
			String sReleaseName = "API Automation";

			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, false))) {
				resultFile.append(SUITE_HEAD);
				resultFile.append("<div class=\"hero\">");
				resultFile.append("<h1>API Automation Test Report</h1>");
				resultFile.append("<p>Execution overview with pass/fail analytics</p>");
				resultFile.append("</div>");

				resultFile.append("<div class=\"meta-grid\">");
				resultFile.append(metaCard("Run Start", sRunStartTime));
				resultFile.append(metaCard("User", sUserReq));
				resultFile.append(metaCard("Environment", sEnvName));
				resultFile.append(metaCard("Release", sReleaseName));
				resultFile.append("</div>");

				resultFile.append("<div class=\"section-title\">Detailed Report</div>");
				resultFile.append("<div class=\"card\" style=\"padding:0;overflow:hidden;\">");
				resultFile.append("<table class=\"data\" id=\"suite-results\"><thead><tr>");
				resultFile.append("<th>Test Scenario</th>");
				resultFile.append("<th>Test Scenario File</th>");
				resultFile.append("<th>Status</th>");
				resultFile.append("<th>Testcase Id</th>");
				resultFile.append("<th>Exception</th>");
				resultFile.append("</tr></thead><tbody>");
			}
		} catch (IOException ex) {
			logger.error("Result file creation failed with exception - " + ex);
			resultFolder.mkdirs();
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite(ITestContext context) {
		if (!ReportFrameworkConfig.isHtmlLifecycleEnabled()) {
			return;
		}
		try {
			Date testEndTime = new Date();
			long testDuration = testEndTime.getTime() - startTime.getTime();
			String testSuiteName = context.getCurrentXmlTest().getSuite().getFileName();
			int index = Math.max(testSuiteName.lastIndexOf('/'), testSuiteName.lastIndexOf('\\'));
			testSuiteName = testSuiteName.substring(index + 1).replace(".xml", "");
			String git_commit_id = Optional.ofNullable(System.getProperty("git_commit_id")).orElse("preprod_run");
			InfluxDbHelper.populateAutomationStats(testDuration, testSuiteName, git_commit_id, passCount, failCount);

			int purePass = Math.max(0, passCount - warnCount);
			int totalExecuted = passCount + failCount;

			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true))) {
				resultFile.append("</tbody></table></div>");

				resultFile.append("<div class=\"section-title\">Summary</div>");
				resultFile.append("<div class=\"kpi-grid\">");
				resultFile.append(kpiCard("total", "Total Executed", String.valueOf(totalExecuted)));
				resultFile.append(kpiCard("pass", "Passed", String.valueOf(purePass)));
				resultFile.append(kpiCard("fail", "Failed", String.valueOf(failCount)));
				resultFile.append(kpiCard("warn", "Warnings", String.valueOf(warnCount)));
				resultFile.append("</div>");

				resultFile.append("<div class=\"meta-grid\">");
				resultFile.append(metaCard("Run Start Time", String.valueOf(startTime)));
				resultFile.append(metaCard("Run End Time", String.valueOf(testEndTime)));
				resultFile.append(metaCard("Duration (ms)", String.valueOf(testDuration)));
				resultFile.append(metaCard("Suite", testSuiteName));
				resultFile.append("</div>");

				resultFile.append("<div class=\"chart-grid\">");
				resultFile.append("<div class=\"card\"><div class=\"section-title\" style=\"margin-top:0;\">Result Distribution</div>");
				resultFile.append("<div class=\"chart-wrap\"><canvas id=\"resultPieChart\"></canvas></div></div>");
				resultFile.append("<div class=\"card\"><div class=\"section-title\" style=\"margin-top:0;\">Result Counts</div>");
				resultFile.append("<div class=\"chart-wrap\"><canvas id=\"resultBarChart\"></canvas></div></div>");
				resultFile.append("</div>");

				resultFile.append("<div class=\"footer-note\">Generated by API Automation Framework</div>");
				resultFile.append(chartScript(purePass, failCount, warnCount));
				resultFile.append("</div></body></html>");
			}
		} catch (Exception ex) {
			logger.error("Unable to close result file - " + ex);
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void createinitialHTMLReport(Method method) {
		if (!ReportFrameworkConfig.isHtmlLifecycleEnabled()) {
			return;
		}
		try {
			UUID randNo = UUID.randomUUID();
			setResultFileStringPath(resultFolderpath + "/" + method.getName() + "_" + randNo + ".html");
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(getResultFileStringPath(), false))) {
				resultFile.append(REQ_RESP_LOG_TEMPLATE);
			}
		} catch (Exception e) {
			setResultFileStringPath(null);
			logger.error("Initial error report not created - " + e);
		}
	}

	@AfterMethod(alwaysRun = true)
	public void appendFinalHTMLReport(ITestResult result) {
		if (!ReportFrameworkConfig.isHtmlLifecycleEnabled()) {
			return;
		}
		ITestNGMethod testNGMethod = result.getMethod();
		try {
			int number = testNGMethod.getCurrentInvocationCount();
			String status = "Pass";
			String scenario = "";
			String testName = "";

			if (result.getParameters().length != 0) {
				for (Object parameter : result.getParameters()) {
					if (parameter instanceof Map && ((((Map<?, ?>) (parameter)).containsKey("TC_DESCRIPTION"))
							|| ((Map<?, ?>) (parameter)).containsKey("TCDESCRIPTION"))) {
						if (((Map<?, ?>) (parameter)).containsKey("TC_DESCRIPTION")) {
							scenario = ((Map<?, ?>) (parameter)).get("TC_DESCRIPTION").toString();
						} else {
							scenario = ((Map<?, ?>) (parameter)).get("TCDESCRIPTION").toString();
						}
						break;
					}
				}
			}
			if (scenario.isEmpty() && result.getMethod().getDescription() != null) {
				scenario = result.getMethod().getDescription();
			}

			if (result.getTestName() == null) {
				Method method = result.getMethod().getConstructorOrMethod().getMethod();
				Test test = method.getAnnotation(Test.class);
				if (test != null) {
					testName = test.testName();
				}
			}

			if (System.getProperty("Test Status") != null
					&& TESTRESULT_WARNING.equalsIgnoreCase(System.getProperty("Test Status"))) {
				status = TESTRESULT_WARNING;
				passCount = passCount + 1;
				warnCount = warnCount + 1;
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
				appendMethodInformationInSuite(scenario, testName,
						result.getMethod().getMethodName() + "_" + number, resultFilePath, status,
						result.getThrowable());
				try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(getResultFileStringPath(), true))) {
					resultFile.append("</tbody></table></div>");
					resultFile.append("<div class=\"footer-note\">Generated by API Automation Framework</div>");
					resultFile.append("</div></body></html>");
				}
			}
			System.clearProperty("Test Status");
		} catch (IOException e) {
			logger.error("Report not completed - " + e);
		}
	}

	public String logException(String methodName, Throwable exception) {
		String exceptionFilePath = "";
		try {
			UUID randNo = UUID.randomUUID();
			exceptionFilePath = resultFolderpath + "/" + methodName + "_Exception_" + randNo + ".html";
			String str = Utils.longStackTrace(exception, true);
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(exceptionFilePath, false))) {
				resultFile.append(METHOD_HEAD);
				resultFile.append("<div class=\"method-hero\"><h1>Exception Stack Trace</h1>");
				resultFile.append("<p>").append(methodName).append("</p></div>");
				resultFile.append("<div class=\"card\"><pre style=\"white-space:pre-wrap;margin:0;font-size:.85rem;\">");
				try (Scanner scanner = new Scanner(str)) {
					while (scanner.hasNextLine()) {
						resultFile.append(escapeHtml(scanner.nextLine())).append("\n");
					}
				}
				resultFile.append("</pre></div></div></body></html>");
			}
		} catch (Exception e) {
			logger.error("Exception not reported - " + e);
		}
		return exceptionFilePath;
	}

	public void appendMethodInformationInSuite(String scenarioName, String testName, String methodName,
			String methodFilePath, String status, Throwable exception) {
		boolean hasThrowable = exception != null;
		String exceptionFilePath = "";
		if (hasThrowable) {
			exceptionFilePath = logException(methodName, exception);
			exceptionFilePath = "." + exceptionFilePath.split("/Results")[1];
		}
		try {
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true))) {
				resultFile.append("<tr>");
				resultFile.append("<td>").append(escapeHtml(nullToEmpty(scenarioName))).append("</td>");
				resultFile.append("<td><a href='").append(methodFilePath).append("'>")
						.append(escapeHtml(methodName)).append("</a></td>");
				resultFile.append(statusBadgeCell(status));
				resultFile.append("<td>").append(escapeHtml(nullToEmpty(testName))).append("</td>");
				if (!exceptionFilePath.isEmpty()) {
					resultFile.append("<td><a href='").append(exceptionFilePath).append("'>Exception</a></td>");
				} else {
					resultFile.append("<td></td>");
				}
				resultFile.append("</tr>");
			}
		} catch (IOException e) {
			logger.error("Error in appending information to suite - " + e);
		}
	}

	public static void appendresultHTMLReport(String resultFileStringPath, String apiURL, String requestFile,
			String responseFile, String message, String status, String comment, String headers) {
		try {
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true))) {
				resultFile.append("<tr>");
				resultFile.append("<td>").append(nullToEmpty(apiURL)).append("</td>");
				resultFile.append("<td>").append(nullToEmpty(headers)).append("</td>");
				resultFile.append("<td>").append(nullToEmpty(requestFile)).append("</td>");
				resultFile.append("<td>").append(nullToEmpty(responseFile)).append("</td>");
				resultFile.append("<td>").append(nullToEmpty(message)).append("</td>");
				resultFile.append(statusBadgeCell(status));
				resultFile.append("<td>").append(escapeHtml(nullToEmpty(comment))).append("</td>");
				resultFile.append("</tr>");
			}
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
			} else {
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
	 */
	public static void logValidationFailure(String fieldName, String expectedValue, String actualValue,
			String failureType) {
		logValidationFailureWithRequest(fieldName, expectedValue, actualValue, failureType, null);
	}

	/**
	 * This method will be used to log failures for interactive testing of api along with request.
	 */
	public static void logValidationFailureWithRequest(String fieldName, String expectedValue, String actualValue,
			String failureType, String request) {
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		String compareFilePath;
		String linkCompareFilePath;
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

	private static String metaCard(String label, String value) {
		return "<div class=\"card\"><div class=\"label\">" + escapeHtml(label) + "</div><div class=\"value\">"
				+ escapeHtml(nullToEmpty(value)) + "</div></div>";
	}

	private static String kpiCard(String css, String label, String value) {
		return "<div class=\"card kpi " + css + "\"><div class=\"label\">" + escapeHtml(label)
				+ "</div><div class=\"value\">" + escapeHtml(value) + "</div></div>";
	}

	private static String statusBadgeCell(String status) {
		String normalized = nullToEmpty(status);
		String css = "fail";
		String label = normalized;
		if (normalized.equalsIgnoreCase("True") || normalized.equalsIgnoreCase("Pass")) {
			css = "pass";
			label = "Pass";
		} else if (normalized.equalsIgnoreCase("false") || normalized.equalsIgnoreCase("Fail")) {
			css = "fail";
			label = "Fail";
		} else if (normalized.equalsIgnoreCase(TESTRESULT_WARNING) || normalized.equalsIgnoreCase("Warning")) {
			css = "warn";
			label = "Warning";
		} else if (normalized.equalsIgnoreCase(TESTRESULT_HTMLREPORT)) {
			css = "fail";
			label = "Fail";
		}
		return "<td><span class=\"badge " + css + "\">" + escapeHtml(label) + "</span></td>";
	}

	private static String chartScript(int pass, int fail, int warn) {
		return "<script>"
				+ "const pass=" + pass + ",fail=" + fail + ",warn=" + warn + ";"
				+ "const labels=['Pass','Fail','Warning'];"
				+ "const values=[pass,fail,warn];"
				+ "const colors=['#12B76A','#F04438','#F79009'];"
				+ "if(window.Chart){"
				+ "new Chart(document.getElementById('resultPieChart'),{type:'pie',data:{labels:labels,datasets:[{data:values,backgroundColor:colors,borderWidth:0}]},"
				+ "options:{plugins:{legend:{position:'bottom'}},maintainAspectRatio:false}});"
				+ "new Chart(document.getElementById('resultBarChart'),{type:'bar',data:{labels:labels,datasets:[{label:'Tests',data:values,backgroundColor:colors,borderRadius:8,maxBarThickness:48}]},"
				+ "options:{plugins:{legend:{display:false}},scales:{y:{beginAtZero:true,ticks:{precision:0}}},maintainAspectRatio:false}});"
				+ "}"
				+ "</script>";
	}

	private static String nullToEmpty(String value) {
		return value == null ? "" : value;
	}

	private static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}
