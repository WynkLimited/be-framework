package com.airtel.helper.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.airtel.helper.logging.AirtelLoggingMgr;

public class ComparatorReportGenerator implements ReportConstants {
	Logger logger = AirtelLoggingMgr.getLogger(ComparatorReportGenerator.class);

	public void appendresultHTMLReport(String resultFileStringPath, String operationType, String fieldName,
			String liveValue, String qaValue) {
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			resultFile.append("<tr>");
			resultFile.append("<td>" + operationType + "</td>");
			resultFile.append("<td>" + fieldName + "</td>");
			resultFile.append("<td>" + liveValue + "</td>");
			resultFile.append("<td>" + qaValue + "</td>");
			resultFile.append("</tr>");
			resultFile.close();
		} catch (IOException e) {
			logger.log(Level.ERROR, "Error with adding Html report" + e);

		}
	}

	public String createCompartarReport() {
		return createCompartorReport(null);
	}

	public String createCompartorReport(String request) {
		ReportHelper report = new ReportHelper();
		String resultFolder = report.getResultFolderpath();
		resultFolder = resultFolder + "/comparatorReport";
		File resultFileFolder = new File(resultFolder);
		resultFileFolder.mkdir();
		UUID randNo = UUID.randomUUID();

		String resultFileStringPath = resultFolder + "/compareResult_" + randNo + ".html";
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			resultFile.append(COMPARATOR_REPORT_TITLE);
			if (request != null) {
				resultFile
						.append("<tr><td><textarea disabled rows='7' cols='55'>REQUEST</textarea></td><td colspan='3'>"
								+ request + "</td></tr>");
			}
			resultFile.close();
		} catch (IOException e) {
			logger.log(Level.ERROR, "Error with compartor report creation" + e);

		}
		return resultFileStringPath;
	}

	public void appendFinalHTMLReport(String resultFileStringPath) {
		try {
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(resultFileStringPath, true));
			resultFile.append("</table></body></html>");
			resultFile.close();
		} catch (IOException e) {
			logger.log(Level.ERROR, "Not able to close report" + e);

		}

	}

}
