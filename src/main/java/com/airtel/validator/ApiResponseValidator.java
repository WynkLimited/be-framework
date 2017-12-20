package com.airtel.validator;

import java.io.IOException;
import java.util.Set;

import org.testng.Reporter;

import com.airtel.helper.common.ApiHelper;
import com.airtel.helper.report.ComparatorReportGenerator;
import com.airtel.helper.report.ReportHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

/**
 * This method have the functions to validate API response. It constitutes of 3
 * functions: 1. json comparator - compare 2 jsons 2. schema validator -
 * validate the schema against a pre-defined value 3. empty response - check if
 * the response is empty
 * 
 * @author b0202849
 */
public class ApiResponseValidator {

	public static boolean compareJson(String responseExpected, String responseActual, String propFilePath) {
		ReportHelper reporter = new ReportHelper();
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		try {
			String compareFilePath = null;
			String linkCompareFilePath = null;
			compareFilePath = compareHtmlObj.createinitialHTMLReport();
			linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];

			Integer differenceCounter = 0;
			// Extracting JSON data to JSON node and comparing them
			ObjectMapper objMapper = new ObjectMapper();
			JsonNode responseExp = objMapper.readTree(responseExpected);
			JsonNode responseAct = objMapper.readTree(responseActual);
			JsonNode jsonDiff = JsonDiff.asJson(responseExp, responseAct);
			Set<String> dndCompareValues = null;
			if (propFilePath != null && !propFilePath.isEmpty()) {
				dndCompareValues = ApiHelper.getAllProperty(propFilePath);
			}
			for (int diffCount = 0; diffCount < jsonDiff.size(); diffCount++) {
				String diffPath = jsonDiff.get(diffCount).get("path").asText();

				if (dndCompareValues == null || !dndCompareValues.contains(diffPath)) {
					differenceCounter++;
					String operationType = jsonDiff.get(diffCount).get("op").asText();
					JsonNode diffValueJsonNode = jsonDiff.get(diffCount).get("value");
					String diffValue = null;
					if (diffValueJsonNode != null)
						diffValue = jsonDiff.get(diffCount).get("value").asText();
					String parentValue = "";
					if (operationType.equalsIgnoreCase("replace") || operationType.equalsIgnoreCase("remove")) {
						parentValue = responseExp.at(diffPath).asText();
					}
					logFailure(operationType.toUpperCase(), diffPath, diffValue, parentValue);
					logFailureInReporter(compareFilePath, operationType.toUpperCase(), diffPath, diffValue,
							parentValue);
				}
			}
			if (differenceCounter > 0) {
				compareHtmlObj.appendFinalHTMLReport(compareFilePath);
				String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>Differences Link</a>";

				reporter.reporterLogging(false, differenceHTMLLink);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean validateJsonSchema(String response, String responseSchema, String esFieldName) {
		boolean schemaValidationFlag = false;
		ReportHelper reporter = new ReportHelper();
		ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
		try {
			String compareFilePath = null;
			String linkCompareFilePath = null;
			compareFilePath = compareHtmlObj.createinitialHTMLReport();
			linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];
			JsonNode jsonSchema = JsonLoader.fromString(responseSchema);
			JsonNode jsonResponse = JsonLoader.fromString(response);
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonValidator schemaValidator = factory.getValidator();
			ProcessingReport errorReport = schemaValidator.validate(jsonSchema, jsonResponse);
			String errorText = "Schema validation failures - in " + esFieldName + " :";
			if (!errorReport.isSuccess()) {
				for (ProcessingMessage msg : errorReport) {
					if (msg.getLogLevel().toString().equalsIgnoreCase("error")) {
						schemaValidationFlag = false;
						errorText += msg.getMessage() + "<br/>";
					}
				}
				Reporter.log(errorText);
				logFailureInReporter(compareFilePath, "ES log comparison", "-", errorText, "-");
				compareHtmlObj.appendFinalHTMLReport(compareFilePath);
				String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>Differences Link</a>";
				reporter.reporterLogging(false, differenceHTMLLink);
			} else
				schemaValidationFlag = true;

		} catch (IOException e) {
			Reporter.log("Couldn't convert schema because of error " + e.getLocalizedMessage());
		} catch (ProcessingException e) {
			Reporter.log("Couldn't process report " + e.getLocalizedMessage());

		}
		return schemaValidationFlag;
	}

	public static boolean validateJsonObject(Object responseObj, String esFieldName) {
		if (responseObj.equals(null)) {
			Reporter.log("<br/><font size='2'>ES Logs field " + esFieldName + " is null.</font>");
			return false;
		}
		return true;
	}

	private static void logFailureInReporter(String compareFilePath, String operationType, String differencePath,
			String differenceValue, String parentValue) {
		ComparatorReportGenerator compareObj = new ComparatorReportGenerator();
		try {
			compareObj.appendresultHTMLReport(compareFilePath,
					"<textarea disabled rows='7' cols='55'>" + operationType + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + differencePath + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + parentValue + "</textarea>",
					"<textarea disabled rows='7' cols='55'>" + differenceValue + "</textarea>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void logFailure(String operationType, String path, String qaEnvValue, String liveEnvValue) {
		Reporter.log("<font size='2'><b>Difference Type - </b>" + operationType + "</font>");
		Reporter.log("<font size='1'><b>Jpath - </b>" + path + "</font>");
		Reporter.log("<font size='1'><b>QA Env value - </b>" + qaEnvValue + "</font>");
		Reporter.log("<font size='1'><b>Live Env value - </b>" + liveEnvValue + "</font>");
		Reporter.log("<br/>");
	}
}
