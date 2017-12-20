package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class ComparatorReportGenerator {

	public void appendresultHTMLReport(String resultFileStringPath, String operationType, String fieldName, String liveValue, String qaValue)
			throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append("<tr>");
		resultFile.append("<td>" + operationType + "</td>");
		resultFile.append("<td>" + fieldName + "</td>");
		resultFile.append("<td>" + liveValue + "</td>");
		resultFile.append("<td>" + qaValue + "</td>");
		resultFile.append("</tr>");
		resultFile.close();
	}

	public String createinitialHTMLReport() throws IOException {
		ReportHelper report = new ReportHelper();
		String resultFolder = report.getResultFolderpath();
		resultFolder = resultFolder + "/comparatorReport";
		File resultFileFolder = new File(resultFolder);
		resultFileFolder.mkdir();

		UUID randNo = UUID.randomUUID();

		String resultFileStringPath = resultFolder + "/compareResult_" + randNo + ".html";
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append("<html>");
		resultFile.append("</head>");
		resultFile.append("<body>");
		resultFile.append("<title>Compare Engine Response</title>");
		resultFile.append("<table border = '1'><tr>");
		resultFile.append("<th>Operation Type</th>");
		resultFile.append("<th>Field Name</th>");
		resultFile.append("<th>Expected value</th>");
		resultFile.append("<th>Test build value</th>");
		resultFile.append("</tr>");
		resultFile.close();
		return resultFileStringPath;
	}

	public void appendFinalHTMLReport(String resultFileStringPath) throws IOException {
		BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
		resultFile.append("</table></body></html>");
		resultFile.close();
	}

}
