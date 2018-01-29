package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class ComparatorReportGenerator implements ReportConstants {

	public void appendresultHTMLReport(String resultFileStringPath, String operationType, String fieldName,
			String liveValue, String qaValue) throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append("<tr>");
		resultFile.append("<td>" + operationType + "</td>");
		resultFile.append("<td>" + fieldName + "</td>");
		resultFile.append("<td>" + liveValue + "</td>");
		resultFile.append("<td>" + qaValue + "</td>");
		resultFile.append("</tr>");
		resultFile.close();
	}
	
	public String createCompartarReport() throws IOException {
		ReportHelper report = new ReportHelper();
		String resultFolder = report.getResultFolderpath();
		resultFolder = resultFolder + "/comparatorReport";
		File resultFileFolder = new File(resultFolder);
		resultFileFolder.mkdir();

		UUID randNo = UUID.randomUUID();

		String resultFileStringPath = resultFolder + "/compareResult_" + randNo + ".html";
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append(COMPARATOR_REPORT_TITLE);
		resultFile.close();
		return resultFileStringPath;
	}
	
	public String createCompartarReportWithReq(String request) throws IOException {
		ReportHelper report = new ReportHelper();
		String resultFolder = report.getResultFolderpath();
		resultFolder = resultFolder + "/comparatorReport";
		File resultFileFolder = new File(resultFolder);
		resultFileFolder.mkdir();

		UUID randNo = UUID.randomUUID();

		String resultFileStringPath = resultFolder + "/compareResult_" + randNo + ".html";
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append(COMPARATOR_REPORT_TITLE);
		resultFile.append("<tr><td><textarea disabled rows='7' cols='55'>REQUEST</textarea></td><td colspan='3'>" + request + "</td></tr>");
		resultFile.close();
		return resultFileStringPath;
	}

	public void appendFinalHTMLReport(String resultFileStringPath) throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append("</table></body></html>");
		resultFile.close();
	}

}
