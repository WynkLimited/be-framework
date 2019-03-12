package com.airtel.validator;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.airtel.helper.data.DataHelper;
import com.airtel.helper.data.JsonParser;
import org.testng.Reporter;

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
 * This class have the functions to validate API response. It constitutes of 3
 * functions: 1. json comparator - compare 2 jsons 2. schema validator -
 * validate the schema against a pre-defined value 3. empty response - check if
 * the response is empty
 *
 * @author b0202849
 */
public abstract class ApiResponseValidator extends JsonParser {

    final String OPERATION = "op";
    final String DIFFERENCE_PATH = "path";
    final String DIFFERENCE_VALUE = "value";
    final String FAILURE_LOG_START = "";
    final String FAILURE_LOG_END = "";

    DataHelper dataHelper;

    /**
     * This method compares two json and log the difference b/w those JSON. This
     * comparator has a limitation of logging difference when the position of node
     * has also changed, so it's recommended to do object to object comparison
     * before using this.
     *
     * @param responseExpected
     * @param responseActual
     * @param propFilePath
     * @return
     */
    public boolean compareJson(String responseExpected, String responseActual, String propFilePath) {
        return compareJsonWithRequest(responseExpected, responseActual, null, propFilePath);
    }

    /**
     * This method will compare 2 JSON and log difference b/w those JSON.
     *
     * @param responseExpected
     * @param responseActual
     * @return
     */
    public boolean compareJson(String responseExpected, String responseActual) {
        return compareJsonWithRequest(responseExpected, responseActual, null, null);
    }

    /**
     * This method compares two json and log the difference b/w those JSON along
     * with the request. This comparator has a limitation of logging difference when
     * the position of node has also changed, so it's recommended to do object to
     * object comparison before using this.
     *
     * @param responseExpected
     * @param responseActual
     * @param request
     * @param propFilePath
     * @return
     */
    public boolean compareJsonWithRequest(String responseExpected, String responseActual, String request,
                                          String propFilePath) {
        return compareJson(responseExpected, responseActual, request, propFilePath, null);
    }

    protected boolean compareJson(String responseExpected, String responseActual, String request,
                                  String propFilePath, String ignoreOperation) {
        ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
        try {
            String compareFilePath = null;
            String linkCompareFilePath = null;

            if (request == null) {
                compareFilePath = compareHtmlObj.createCompartarReport();
            } else {
                compareFilePath = compareHtmlObj.createCompartorReport(request);
            }

            linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];

            int differenceCounter = 0;
            // Extracting JSON data to JSON node and comparing them

            ObjectMapper objMapper = new ObjectMapper();
            JsonNode responseExp = objMapper.readTree(responseExpected);
            JsonNode responseAct = objMapper.readTree(responseActual);
            JsonNode jsonDiff = JsonDiff.asJson(responseExp, responseAct);
            Set<String> dndCompareValues = null;

            if (propFilePath != null && !propFilePath.isEmpty()) {
                dndCompareValues = new DataHelper().getAllProperty(propFilePath);
            }

            for (int diffCount = 0; diffCount < jsonDiff.size(); diffCount++) {
                String diffPath = jsonDiff.get(diffCount).get(DIFFERENCE_PATH).asText();
                String operationType = jsonDiff.get(diffCount).get(OPERATION).asText();
                if ((dndCompareValues == null || !ignoreFieldOperation(dndCompareValues, diffPath))
                        && !(ignoreOperation != null && operationType.equalsIgnoreCase(ignoreOperation))) {
                    differenceCounter++;

                    JsonNode diffValueJsonNode = jsonDiff.get(diffCount).get(DIFFERENCE_VALUE);

                    String diffValue = Optional.ofNullable(diffValueJsonNode)
                            .map(dataValue -> diffValueJsonNode.asText()).orElse(null);
                    String parentValue = Optional.ofNullable(responseExp.at(diffPath))
                            .map(value -> responseExp.at(diffPath).asText()).orElse(null);

                    logFailureInReporter(compareFilePath, operationType.toUpperCase(), diffPath, diffValue,
                            parentValue);
                }
            }

            if (differenceCounter > 0) {
                compareHtmlObj.appendFinalHTMLReport(compareFilePath);
                String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>Differences Link</a>";

                ReportHelper.reporterLogging(false, differenceHTMLLink);
                return false;
            }
        } catch (Exception e) {
            ReportHelper.logValidationFailure("Exception in comparison", "", e.getMessage(), "Exception");
        }
        return true;
    }

    /**
     * This method is used to validate Json Schema for a particular JSON againsta a
     * valid schema
     *
     * @param response
     * @param responseSchema
     * @param id
     * @return
     */
    protected boolean validateJsonSchema(String response, String responseSchema, String id) {
        boolean schemaValidationFlag = false;
        ComparatorReportGenerator compareHtmlObj = new ComparatorReportGenerator();
        try {
            String compareFilePath = null;
            String linkCompareFilePath = null;
            compareFilePath = compareHtmlObj.createCompartarReport();
            linkCompareFilePath = "./comparatorReport/" + compareFilePath.split("comparatorReport")[1];
            JsonNode jsonSchema = JsonLoader.fromString(responseSchema);
            JsonNode jsonResponse = JsonLoader.fromString(response);
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonValidator schemaValidator = factory.getValidator();
            ProcessingReport errorReport = schemaValidator.validate(jsonSchema, jsonResponse);
            String errorText = "Schema validation failures - for id - " + id + " :";
            if (!errorReport.isSuccess()) {
                for (ProcessingMessage msg : errorReport) {
                    if (msg.getLogLevel().toString().equalsIgnoreCase("error")) {
                        errorText += msg.getMessage() + "<br/>";
                    }
                }
                ReportHelper.reporterLogging(false, errorText);
                logFailureInReporter(compareFilePath, "JSON SCHEMA  COMPARISION", "-", errorText, "-");
                compareHtmlObj.appendFinalHTMLReport(compareFilePath);
                String differenceHTMLLink = "<a href='" + linkCompareFilePath + "'>Differences Link</a>";
                ReportHelper.reporterLogging(false, differenceHTMLLink);
            } else
                schemaValidationFlag = true;

        } catch (IOException e) {
            ReportHelper.reporterLogging(false, "Couldn't convert schema because of error " + e.getLocalizedMessage());
        } catch (ProcessingException e) {
            ReportHelper.reporterLogging(false, "Couldn't process report " + e.getLocalizedMessage());
        }
        return schemaValidationFlag;
    }

    public boolean validateJsonObject(Object responseObj, String esFieldName) {
        if (responseObj == null) {
            Reporter.log("<br/><font size='2'>ES Logs field " + esFieldName + " is null.</font>");
            return false;
        }
        return true;
    }

    protected void logFailureInReporter(String compareFilePath, String operationType, String differencePath,
                                        String differenceValue, String parentValue) {
        ComparatorReportGenerator compareObj = new ComparatorReportGenerator();

        compareObj.appendresultHTMLReport(compareFilePath, FAILURE_LOG_START + operationType + FAILURE_LOG_END,
                FAILURE_LOG_START + differencePath + FAILURE_LOG_END, FAILURE_LOG_START + parentValue + FAILURE_LOG_END,
                FAILURE_LOG_START + differenceValue + FAILURE_LOG_END);
    }

    protected boolean ignoreFieldOperation(Set<String> ignoreProps, String field) {
        boolean ignoreField = false;
        for (String ignoreProperty : ignoreProps) {
            if (Pattern.matches(ignoreProperty, field)) {
                ignoreField = true;
                break;
            }
        }
        return ignoreField;
    }

}
