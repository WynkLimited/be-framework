package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.airtel.helper.common.BaseTestClass;
import com.airtel.helper.logging.AirtelLoggingMgr;

public class ReportHelper extends BaseTestClass implements ReportConstants {

	static int count = 0;
	static int passCount = 0;
	static int failCount = 0;
	String newDate = null;
	Date startTime = null;
	private static String resultFolderpath;
	private static Logger logger = AirtelLoggingMgr.getLogger(ReportHelper.class);

	public String getResultFolderpath() {
		return resultFolderpath;
	}

	public void setResultFolderpath(String resultFolderpath) {
		ReportHelper.resultFolderpath = resultFolderpath;
	}

	private static String suiteFileStringPath;
	private static String resultFileStringPath;

	public String getResultFileStringPath() {
		return resultFileStringPath;
	}

	public void setResultFileStringPath(String resultFileStringPath) {
		ReportHelper.resultFileStringPath = resultFileStringPath;
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
	public void afterSuite() {
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true));
			Date date = new Date();
			SimpleDateFormat oDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:s");
			String sRunEndTime = oDateFormat.format(date);
			String sRunStartTime = oDateFormat.format(startTime);

			resultFile.append("</table>");
			resultFile.append(SUMMARY_REPORT_TEMPLATE);
			resultFile.append(SUMMARY_REPORT_ROW_START + "Run Start Time" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + sRunStartTime + "</b></td></tr>");

			resultFile.append(SUMMARY_REPORT_ROW_START + "Run End Time" + REPORT_COLUMN_END);
			resultFile.append(SUMMARY_REPORT_VALUE_TEMPLATE + sRunEndTime + "</b></td></tr>");
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

	public void appendMethodInformationInSuite(String scenarioName, String methodName, String methodFilePath,
			String status) {
		try {
			String statusTag = null;
			if (status.equalsIgnoreCase("Pass")) {
				statusTag = TEST_PASS_TAG;
			} else if (status.equalsIgnoreCase("Fail")) {
				statusTag = TEST_FAIL_TAG;
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

	@BeforeMethod(alwaysRun = true)
	public void createinitialHTMLReport(Method method) {
		try {
			UUID randNo = UUID.randomUUID();

			resultFileStringPath = resultFolderpath + "/" + method.getName().toString() + "_" + randNo + ".html";
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			resultFile.append(REQ_RESP_LOG_TEMPLATE);
			resultFile.close();
			setResultFileStringPath(resultFileStringPath);
		} catch (Exception e) {
			resultFileStringPath = null;
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

			if (result.getStatus() == ITestResult.FAILURE) {
				status = "Fail";
				failCount = failCount + 1;
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				passCount = passCount + 1;
			}
			
			count = count + 1;
			String resultFilePath = resultFileStringPath.split("/Results")[1];
			resultFilePath = "." + resultFilePath;

			if (result.getStatus() != ITestResult.SKIP) {
				appendMethodInformationInSuite(scenario,
						result.getMethod().getMethodName() + "_" + String.valueOf(number), resultFilePath, status);
				BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));

				resultFile.append("</table></body></html>");
				resultFile.close();
			}
		} catch (IOException e) {
			logger.error("Report not completed - " + e);
		}
	}

	public void appendresultHTMLReport(String resultFileStringPath, String apiURL, String requestFile,
			String responseFile, String message, String status, String comment) {
		try {

			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			String statusTag = null;
			if (status.equalsIgnoreCase("True") || status.equalsIgnoreCase("Pass")) {
				statusTag = TEST_PASS_TAG;
			} else if (status.equalsIgnoreCase("false") || status.equalsIgnoreCase("Fail")) {
				statusTag = TEST_FAIL_TAG;
			} else {
				statusTag = "<td>";
			}

			resultFile.append("<tr>");
			resultFile.append("<td width=30% style=WORD-break:BREAK-all>" + apiURL + "</td>");
			resultFile.append("<td width=20% style=WORD-break:BREAK-all>" + requestFile + "</td>");
			resultFile.append("<td width=20% style=WORD-break:BREAK-all>" + responseFile + "</td>");
			resultFile.append("<td width=10% style=WORD-break:BREAK-all>" + message + "</td>");
			resultFile.append(statusTag + status + "</td>");
			resultFile.append("<td style=word-wrap: break-word>" + comment + "</td>");
			resultFile.append("</tr>");
			resultFile.close();

		} catch (Exception e) {
			logger.error("Append to HTML failed - " + e);
		}
	}

	public void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}

	public static boolean reporterLogging(Boolean status, String sMessage) {
		ReportHelper reportHelper = new ReportHelper();
		try {
			reportHelper.appendresultHTMLReport(reportHelper.getResultFileStringPath(), "", "", "", sMessage,
					String.valueOf(status), Thread.currentThread().getStackTrace()[2].getMethodName());
			Reporter.log("<br>");
			if (status) {
				Reporter.log("<Font Color=#008000> PASS </Font>" + sMessage);
			} else {
				Reporter.log("<Font Color=red> FAIL </Font> " + sMessage);
			}
		} catch (Exception e) {
			logger.error("Not able to log in report - " + e);
		}
		return status;
	}

	/**
	 * This method will be used to log failures for interactive testing of api.
	 * 
	 * @param fieldName
	 *            - Name of the field for which comparison has failed
	 * @param expectedValue
	 *            - Expected value of the field
	 * @param actualValue
	 *            - Actual value of the field
	 * @param failureType
	 *            - Type of failure that will be the hyperlink for report. For e.g.
	 *            - Interactive failure, ES logs failure,
	 */
	public static void logValidationFailure(String fieldName, String expectedValue, String actualValue,
			String failureType) {
		logValidationFailureWithRequest(fieldName, expectedValue, actualValue, failureType, null);
	}

	/**
	 * This method will be used to log failures for interactive testing of api along
	 * with request.
	 * 
	 * @param fieldName
	 *            - Name of the field for which comparison has failed
	 * @param expectedValue
	 *            - Expected value of the field
	 * @param actualValue
	 *            - Actual value of the field
	 * @param failureType
	 *            - Type of failure that will be the hyperlink for report. For e.g.
	 *            - Interactive failure, ES logs failure,
	 * @param request
	 *            - Request for which the validation has failed
	 *
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
			ReportHelper.reporterLogging(false, differenceHTMLLink);
		}

		catch (Exception e) {
			logger.error("File path is not specified correctly - " + e);
		}
	}
}