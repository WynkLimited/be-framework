package com.airtel.helper.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.testng.Reporter;

import com.airtel.helper.report.ReportHelper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerConnectUtility {

	private static String hostCheck = "StrictHostKeyChecking";

	/**
	 * This method executes a Unix command on unix server
	 * 
	 * @param session
	 * @param command
	 * @param hostName
	 * @param port
	 */
	public static void executeUnixCommandWithSudo(Session session, String command, String password) {
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			ChannelExec execChannel = (ChannelExec) channel;
			// Execute sudo command so that user can restart the server
			execChannel.setCommand("sudo -S " + command);
			execChannel.setErrStream(System.err);

			if (password != null) {
				OutputStream out = channel.getOutputStream();
				channel.connect();
				out.write((password + "\n").getBytes());
				out.flush();
				Thread.sleep(1000);
			} else
				channel.connect();
			execChannel.disconnect();
		} catch (Exception e) {
			Reporter.log(e.getLocalizedMessage());
		} finally {
			if (channel != null)
				channel.disconnect();
		}
	}

	/**
	 * This method will open a session to server with mentioned credentials
	 * 
	 * @param serverIp
	 * @param userName
	 * @param password
	 * @return
	 */
	public static Session connectToServer(String serverIp, String userName, String password) {
		JSch jsch = new JSch();
		Session session = null;

		try {
			session = jsch.getSession(userName, serverIp);

			java.util.Properties config = new java.util.Properties();
			config.put(hostCheck, "no");

			session.setConfig(config);
			session.setPassword(password);
			session.connect();
		} catch (JSchException e) {
			Reporter.log(e.getLocalizedMessage());
		}

		return session;
	}

	/**
	 * This method will update a file having propertyField with mentioned value
	 * 
	 * @param session
	 * @param absFileName
	 * @param propertyField
	 * @param propertyValue
	 */
	public static void updatePropertyFile(Session session, String absFileName, String propertyField,
			String propertyValue, String operation) {

		BufferedReader br = null;
		List<String> lines = new ArrayList<String>();
		String line = null;
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		ByteArrayInputStream objInputStream = null;

		try {
			channel = session.openChannel("sftp");
			channel.connect();

			sftpChannel = (ChannelSftp) channel;
			InputStream input = sftpChannel.get(absFileName);

			br = new BufferedReader(new InputStreamReader(input));

			// Read file and check if the mentioned property exists
			// else append the property with new value in case of update operation
			// and delete the property in case of delete operation
			boolean flagFound = false;

			if (operation.equals("update")) {
				while ((line = br.readLine()) != null) {
					if (line.contains(propertyField)) {
						line = line.replaceFirst(propertyField + "(.*)", propertyField + "=" + propertyValue);
						flagFound = true;
					}
					if ((line.trim()).length() > 0) {
						lines.add(line + "\n");
					}
				}

				if (!flagFound) {
					lines.add("\n" + propertyField + "=" + propertyValue);
				}
			} else if (operation.equals("delete")) {
				while ((line = br.readLine()) != null) {
					if (line.contains(propertyField)) {
						continue;
					}
					if ((line.trim()).length() > 0) {
						lines.add(line + "\n");
					}
				}
			}

			// Write to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(baos);

			for (String element : lines) {
				output.writeBytes(element);
			}

			byte[] bytes = baos.toByteArray();
			objInputStream = new ByteArrayInputStream(bytes);

			// Put updated file to server
			sftpChannel.put(objInputStream, absFileName);

			br.close();
			input.close();
			objInputStream.close();
			baos.close();
			output.close();

		} catch (Exception e) {
			ReportHelper.reporterLogging(false,
					"Exception Caught while updating property file" + "<br>Stacktrace-<br>" + e.getMessage());
		} finally {
			sftpChannel.disconnect();
			channel.disconnect();
		}
	}

	/**
	 * This method will read data from a log file as per command provided in
	 * parameter and return list of all lines of command output
	 * 
	 * Example of command - 1. "cat "+ CommonConstants.SERVER_LOG_FILE + " | grep "
	 * + searchObj.getCorrelationKey() + " | grep ErrorCodesLogEntry" 2. "cat
	 * /opt/logs/webapi.log |grep abc | grep ERROR"
	 * 
	 * @param session
	 * @param command
	 * @return
	 */
	public static List<String> readLogFileData(Session session, String command) {

		Channel channel = null;
		List<String> logData = new ArrayList<>();

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			InputStream commandOutput = channel.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(commandOutput));
			String line = "";
			while ((line = br.readLine()) != null) {
				logData.add(line);
			}
			br.close();
			commandOutput.close();
		} catch (Exception e) {
			ReportHelper.reporterLogging(false,
					"Exception Caught while reading data from log File" + "<br>Stacktrace-<br>" + e.getMessage());
		} finally {
			if (channel != null)
				channel.disconnect();
		}
		return logData;
	}

}
