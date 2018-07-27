package com.airtel.helper.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;

import com.airtel.helper.logging.AirtelLoggingMgr;

public class FileHelper {

	static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	private static Logger logger = AirtelLoggingMgr.getLogger(FileHelper.class);
	byte[] keyAsBytes;
	SecretKey key;

	public void writeToFile(String str, String fileName, boolean appendToFile) {
		File file = null;

		file = new File(fileName);

		try {
			FileWriter writer = new FileWriter(file, appendToFile);
			writer.write(str);
			writer.close();

		} catch (IOException e) {
			logger.log(Level.ERROR, "Not able to write to a file - " + e);
		}
	}

	public String getFileAbsolutePath(String partialFilePath, String filename) {
		String path = null;
		File file = new File(partialFilePath + File.separator + filename);

		path = file.getAbsolutePath();

		return path;

	}

	public String readOutputFromFile(String fileName) {

		BufferedReader br = null;

		String finalStr = "";
		StringBuilder str = new StringBuilder();

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				str.append(sCurrentLine);
			}

		} catch (IOException e) {
			logger.log(Level.ERROR, "Error while reading from a file - " + e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				logger.log(Level.ERROR, "Error while closing file to read - " + ex);
			}
		}
		finalStr = str.toString();
		return finalStr;
	}

	public String createRequestJsonFile(String jsonMsg, String fileName) {
		String requestFilePath = null;
		ReportHelper report = new ReportHelper();
		try {
			String resultFolder = report.getResultFolderpath();
			resultFolder = resultFolder + "/Requests";
			File resultFileFolder = new File(resultFolder);
			resultFileFolder.delete();
			resultFileFolder.mkdir();

			UUID randNo = UUID.randomUUID();

			requestFilePath = resultFolder + "/Requests_" + fileName + "_" + randNo + ".json";
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(requestFilePath, true));
			resultFile.append(jsonMsg);
			resultFile.close();
			requestFilePath = "." + requestFilePath.split(report.getResultFolderpath())[1];
			Reporter.log("<br><a href='" + requestFilePath + "'>Request_" + fileName + "</a><br>");
		} catch (Exception e) {
			logger.log(Level.ERROR, "Error while creating JSON file - " + e);
		}
		return requestFilePath;
	}

	public String createResponseJsonFile(String jsonMsg, String fileName) {
		String responseFilePath = null;
		ReportHelper report = new ReportHelper();
		try {
			String resultFolder = report.getResultFolderpath();
			resultFolder = resultFolder + "/Responses";
			File resultFileFolder = new File(resultFolder);
			resultFileFolder.delete();
			resultFileFolder.mkdir();

			UUID randNo = UUID.randomUUID();

			responseFilePath = resultFolder + "/Responses_" + fileName + "_" + randNo + ".json";
			BufferedWriter resultFile = new BufferedWriter(new FileWriter(responseFilePath, true));
			resultFile.append(jsonMsg);
			resultFile.close();
			responseFilePath = "." + responseFilePath.split(report.getResultFolderpath())[1];
			Reporter.log("<br><a href='" + responseFilePath + "'>Response_" + fileName + "</a><br>");
		} catch (Exception e) {
			logger.log(Level.ERROR, "Error while creating reponse JSON file - " + e);
		}
		return responseFilePath;
	}

	public static void emptyFile(String path) {
		File file;
		file = new File(path);
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.error("Exception occured on making file empty" + e);
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
}
