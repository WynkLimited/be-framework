package com.airtel.helper.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

public class DataHelper {
	public static Object readJsonFile(String file) {
		JSONParser parser = new JSONParser();
		Object object = null;
		try {
			object = parser.parse(new FileReader(file));

		} catch (Exception e) {
			Reporter.log("Exception in Reading JSON  file " + file + e.getStackTrace());
		}

		return object;
	}

	public static String readFile(String file) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			reader.close();
		} catch (IOException e) {
			Reporter.log("Could not find file " + file);
		}
		return stringBuilder.toString();
	}

	public static List<String> executeShelCommand(String command) {
		List<String> outputData = new ArrayList<String>();
		try {
			String dataString = null;
			Process execCommand = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c",
					command });
			BufferedReader commandRespReader = new BufferedReader(new InputStreamReader(execCommand.getInputStream()));
			while ((dataString = commandRespReader.readLine()) != null) {
				outputData.add(dataString);
			}
		} catch (Exception e) {
			Reporter.log("Couldn't execute command");
		}
		return outputData;
	}
}
