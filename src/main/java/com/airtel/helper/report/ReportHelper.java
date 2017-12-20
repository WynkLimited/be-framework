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

import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class ReportHelper {

	static int count = 0;
	static int passCount = 0;
	static int failCount = 0;
	static String newDate = null;

	private static String resultFolderpath;

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
	public void beforeSuite() throws IOException {
		String resultsFolderPath = "./Results";
		File resultFolder = new File(resultsFolderPath);
		if(resultFolder.delete()) {
			System.out.println("Content of folder deleted successfully");
		}
		resultFolder.mkdir();

		Date date = new Date();
		SimpleDateFormat oDateFormat = new SimpleDateFormat("dd_MM_YY");
		newDate = oDateFormat.format(date);

		resultFolderpath = resultsFolderPath + "/Result_" + newDate;
		File resultFileFolder = new File(resultFolderpath);
		resultFileFolder.mkdir();

		suiteFileStringPath = resultsFolderPath + "/AirtelTestResult_" + newDate + ".html";
		date = new Date();
		oDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:s");
		String sRunStartTime = oDateFormat.format(date);
		String sUserReq = System.getenv("ComputerName");
		String sEnvName = System.getProperty("os.name");
		String sReleaseName = "API Automation";
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, false));
		resultFile.append(
				"<html><HEAD><TITLE>Automation Report</TITLE></HEAD><body><h4 align=\"center\"><FONT COLOR=\"660066\" FACE=\"Arial\"SIZE=5><b>API Automation Test Report</b>");
		resultFile.append("<table cellspacing=1 cellpadding=1   border=1> <tr>");
		resultFile.append("<h4> <FONT COLOR=\"660000\" FACE=\"Arial\" SIZE=4.5> Test Details :</h4>");
		resultFile.append(
				"<td width=150 align=\"left\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2.75><b>Run Start Date Time</b></td>");
		resultFile.append("<td width=150 align=\"left\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ sRunStartTime + "</b></td></tr>");
		resultFile.append(
				"<tr  border=1><td width=150 align=\"left\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2.75><b>User Requested</b></td>");
		resultFile.append("<td width=150 align=\"left\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>" + sUserReq
				+ "</b></td></tr>");
		resultFile.append(
				"<tr  border=1><td width=150 align=\"left\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2.75><b>Environment</b></td>");
		resultFile.append("<td width=150 align=\"left\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>" + sEnvName
				+ "</b></td></tr>");
		resultFile.append(
				"<tr><td  border=1 width=150 align=\"left\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2.75><b>Release</b></td>");
		resultFile.append("<td  border=1 width=150 align=\"left\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ sReleaseName + "</b></td></tr></table>");
		resultFile.append(
				"<h4> <FONT COLOR=\"660000\" FACE=\"Arial\" SIZE=4.5> Detailed Report :</h4><table  border=1 cellspacing=1  cellpadding=1 ><tr>");
		resultFile.append(
				"<td width=150  align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Test Scenario</b></td>");
		resultFile.append(
				"<td width=150 align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Test Scenario File</b></td>");
		resultFile.append(
				"<td width=150 align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Status</b></td>");
		resultFile.close();
		setResultFolderpath(resultFolderpath);

	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() throws Exception {
		// ReportHelper reporter = new ReportHelper();
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true));
		resultFile.append("</table>");
		resultFile.append("<h4> <FONT COLOR=\"660000\" FACE=\"Arial\" SIZE=4.5> Summary :</h4>");
		resultFile.append("<table cellspacing=1 cellpadding=1   border=1>");

		resultFile.append(
				"<tr><td width=300  align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Total TestNg Test Executed</td></b>");
		resultFile.append("<td width=150 align=\"center\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ String.valueOf(passCount + failCount) + "</b></td></tr>");

		resultFile.append(
				"<tr><td width=300  align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Total TestNg Pass Test count</td></b>");
		resultFile.append("<td width=150 align=\"center\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ String.valueOf(passCount) + "</b></td></tr>");

		resultFile.append(
				"<tr><td width=300  align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2><b>Total TestNg Fail Test count</td></b>");
		resultFile.append("<td width=150 align=\"center\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ String.valueOf(failCount) + "</b></td></tr>");

		Date date = new Date();
		SimpleDateFormat oDateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:s");
		String sRunEndTime = oDateFormat.format(date);
		resultFile.append(
				"<tr><td width=150 align=\"center\" bgcolor=\"#e63d00\"><FONT COLOR=\"#E0E0E0\" FACE=\"Arial\" SIZE=2.75><b>Run End Date Time</b></td>");
		resultFile.append("<td width=150 align=\"center\"><FONT COLOR=\"#385daa\" FACE=\"Arial\" SIZE=2.75><b>"
				+ sRunEndTime + "</b></td></tr>");

		resultFile.append("</table></body></html>");
		resultFile.close();
		// String zipFileName = "./ResultArchive.zip";
		// FileOutputStream fos = new FileOutputStream(zipFileName);
		// ZipOutputStream zos = new ZipOutputStream(fos);
		// reporter.addDirToZipArchive(zos, new File(resultFolderpath), null);
		// reporter.addDirToZipArchive(zos, new File(suiteFileStringPath), null);
		// zos.flush();
		// fos.flush();
		// zos.close();
		// fos.close();
	}

	public void appendMethodInformationInSuite(String scenarioName, String methodName, String methodFilePath,
			String status) throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(suiteFileStringPath, true));
		String statusTag = null;
		if (status.equalsIgnoreCase("Pass")) {
			statusTag = "<td bgcolor='#00FF7F'>";
		} else if (status.equalsIgnoreCase("Fail")) {
			statusTag = "<td bgcolor='#FF4500'>";
		} else {
			statusTag = "<td>";
		}
		resultFile.append("<tr>");
		resultFile.append("<td>" + scenarioName + "</td>");
		resultFile.append("<td><a href='" + methodFilePath + "' />" + methodName + "</td>");
		resultFile.append(statusTag + status + "</td>");
		resultFile.append("</tr>");
		resultFile.close();
	}

	@BeforeMethod(alwaysRun = true)
	public void createinitialHTMLReport(Method method) {

		try {
			UUID randNo = UUID.randomUUID();

			resultFileStringPath = resultFolderpath + "/" + method.getName().toString() + "_" + randNo + ".html";

			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			resultFile.append("<html>");
			resultFile.append("</head>");
			resultFile.append("<body>");
			resultFile.append("<title>API Test Report</title>");

			resultFile.append("<table style=table-layout:fixed; width:40px border = '1'><tr>" + "<th>API URL</th>");
			resultFile.append("<th>Request</th>");
			resultFile.append("<th>Response</th>");
			resultFile.append("<th>Message</th>");
			resultFile.append("<th>Status</th>");
			resultFile.append("<th>Calling Method</th></tr>");
			resultFile.close();
			setResultFileStringPath(resultFileStringPath);
		} catch (Exception e) {
			resultFileStringPath = null;
		}
	}

	@AfterMethod(alwaysRun = true)
	public void appendFinalHTMLReport(ITestResult result) throws IOException {
		ITestNGMethod testNGMethod = result.getMethod();
		int number = testNGMethod.getCurrentInvocationCount();
		String status = "Pass";
		String scenario = "";

		if (result.getStatus() == ITestResult.FAILURE) {
			status = "Fail";
			failCount = failCount + 1;
		} else {
			passCount = passCount + 1;
		}
		count = count + 1;
		String resultFilePath = resultFileStringPath.split("/Results")[1];
		resultFilePath = "." + resultFilePath;

		appendMethodInformationInSuite(scenario, result.getMethod().getMethodName() + "_" + String.valueOf(number),
				resultFilePath, status);
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));

		resultFile.append("</table></body></html>");
		resultFile.close();
	}

	public void appendresultHTMLReport(String resultFileStringPath, String apiURL, String requestFile,
			String responseFile, String message, String status, String comment) throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		String statusTag = null;
		if (status.equalsIgnoreCase("True") || status.equalsIgnoreCase("Pass")) {
			statusTag = "<td bgcolor='#00FF7F'>";
		} else if (status.equalsIgnoreCase("false") || status.equalsIgnoreCase("Fail")) {
			statusTag = "<td bgcolor='#FF4500'>";
		} else {
			statusTag = "<td>";
		}
		resultFile.append("<tr>");

		resultFile.append("<td width=30% style=WORD-break:BREAK-all>" + apiURL + "</td>");
		resultFile.append("<td width=40% style=WORD-break:BREAK-all>" + requestFile + "</td>");
		resultFile.append("<td style=word-wrap: break-word>" + responseFile + "</td>");
		resultFile.append("<td width=70% style=WORD-break:BREAK-all>" + message + "</td>");
		resultFile.append(statusTag + status + "</td>");
		resultFile.append("<td style=word-wrap: break-word>" + comment + "</td>");
		resultFile.append("</tr>");
		resultFile.close();
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

	public boolean reporterLogging(Boolean status, String sMessage) {
		ReportHelper reportHelper = new ReportHelper();
		try {
			// softAssert.assertTrue( status, sMessage);
			reportHelper.appendresultHTMLReport(reportHelper.getResultFileStringPath(), "", "", "", sMessage,
					String.valueOf(status), Thread.currentThread().getStackTrace()[2].getMethodName());
			Reporter.log("<br>");
			if (status) {
				Reporter.log("<Font Color=#008000> PASS </Font>" + sMessage);
			} else {
				Reporter.log("<Font Color=red> FAIL </Font> " + sMessage);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return status;
	}

	/**
	 * This method helps in adding data to report for interactive failures.
	 * 
	 * @param fieldName - Name of the field for which comparison has failed
	 * @param expectedValue - Expected value of the field
	 * @param actualValue - Actual value of the field
	 * @param failureType - Type of failure that will be the hyperlink for report. For e.g. - Interactive failure, ES logs failure, 
	 */
	public static void logValidationFailure(String fieldName, String expectedValue, String actualValue, String failureType) {
		ReportHelper reporter = new ReportHelper();
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		String compareFilePath = null;
		String linkCompareFilePath = null;
		try {
			compareFilePath = compareHtmlObj.createinitialHTMLReport();
			linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];
			compareHtmlObj.appendresultHTMLReport(compareFilePath,
					"<textarea disabled rows='7' cols='55'>" + failureType + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + fieldName + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + expectedValue + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + actualValue + "</textarea>");
			compareHtmlObj.appendFinalHTMLReport(compareFilePath);
			String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>" + failureType + "</a>";
			reporter.reporterLogging(false, differenceHTMLLink);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}