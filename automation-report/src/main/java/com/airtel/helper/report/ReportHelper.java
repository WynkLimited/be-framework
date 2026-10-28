package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import com.airtel.Models.DebugLogItemReq;
import com.report.SentinelLogger;

/**
 * Lightweight step/assertion logging used by helpers (ApiHelper, DB, Mongo, …).
 * Suite HTML reporting was removed — use <b>ChainTest</b> via {@link ReportFrameworkConfig}.
 */
public class ReportHelper implements ReportConstants {

	private static final Logger logger = LoggerFactory.getLogger(ReportHelper.class);
	private static String resultFolderpath;
	private static final ThreadLocal<String> threadLocalResultFileStringPath = new InheritableThreadLocal<>();

	public static String getResultFolderpath() {
		ensureResultFolder();
		return resultFolderpath;
	}

	public void setResultFolderpath(String resultFolderpath) {
		ReportHelper.resultFolderpath = resultFolderpath;
	}

	public static String getResultFileStringPath() {
		ensureResultFolder();
		if (threadLocalResultFileStringPath.get() == null) {
			String path = resultFolderpath + "/step_" + UUID.randomUUID() + ".html";
			threadLocalResultFileStringPath.set(path);
			try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(path, false))) {
				resultFile.append(REQ_RESP_LOG_TEMPLATE);
			} catch (Exception e) {
				logger.warn("Could not create step log file: {}", e.getMessage());
			}
		}
		return threadLocalResultFileStringPath.get();
	}

	public void setResultFileStringPath(String resultFileStringPath) {
		threadLocalResultFileStringPath.set(resultFileStringPath);
	}

	private static void ensureResultFolder() {
		if (resultFolderpath != null) {
			return;
		}
		String date = new SimpleDateFormat("dd_MM_yy").format(new Date());
		resultFolderpath = "./Results/Result_" + date;
		new File(resultFolderpath).mkdirs();
	}

	public static void appendresultHTMLReport(String resultFileStringPath, String apiURL, String requestFile,
			String responseFile, String message, String status, String comment, String headers) {
		if (resultFileStringPath == null || resultFileStringPath.isBlank()) {
			return;
		}
		try (BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true))) {
			resultFile.append("<tr>");
			resultFile.append("<td>").append(nullToEmpty(apiURL)).append("</td>");
			resultFile.append("<td>").append(nullToEmpty(headers)).append("</td>");
			resultFile.append("<td>").append(nullToEmpty(requestFile)).append("</td>");
			resultFile.append("<td>").append(nullToEmpty(responseFile)).append("</td>");
			resultFile.append("<td>").append(nullToEmpty(message)).append("</td>");
			resultFile.append("<td>").append(escapeHtml(nullToEmpty(status))).append("</td>");
			resultFile.append("<td>").append(escapeHtml(nullToEmpty(comment))).append("</td>");
			resultFile.append("</tr>");
		} catch (Exception e) {
			logger.error("Append to step log failed", e);
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
			logger.error("Not able to log - {}", e.getMessage());
		}
		return Boolean.parseBoolean(status);
	}

	public static void logValidationFailure(String fieldName, String expectedValue, String actualValue,
			String failureType) {
		logValidationFailureWithRequest(fieldName, expectedValue, actualValue, failureType, null);
	}

	public static void logValidationFailureWithRequest(String fieldName, String expectedValue, String actualValue,
			String failureType, String request) {
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		String compareFilePath = request == null
				? compareHtmlObj.createCompartarReport()
				: compareHtmlObj.createCompartorReport(request);
		try {
			String linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];
			compareHtmlObj.appendresultHTMLReport(compareFilePath,
					"<textarea disabled rows='7' cols='55'>" + failureType + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + fieldName + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + expectedValue + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + actualValue + "</textarea>");
			compareHtmlObj.appendFinalHTMLReport(compareFilePath);
			reporterLogging(TESTRESULT_HTMLREPORT, "<a href='" + linkCompareFilePath + "'>" + failureType + "</a>");
		} catch (Exception e) {
			logger.error("Comparator report path error", e);
			reporterLogging(false, failureType + ": " + fieldName + " expected=" + expectedValue + " actual=" + actualValue);
		}
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
