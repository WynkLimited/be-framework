package com.airtel.helper.data;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.airtel.helper.common.ApiHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.airtel.helper.common.BaseTestClass;

import static com.airtel.helper.data.DbHelper.CONFIGURATION_FILE_PATH;

public class TestDataHelper extends BaseTestClass {


    @DataProvider(name = "dbDataProvider")
    protected Iterator<Object[]> getDataFromDB(Method method) {
        List<String> queryData = new ArrayList<>();
        List<Object[]> testData = new ArrayList<>();
        Test tstData = method.getAnnotation(Test.class);
        if (tstData != null && tstData.groups() != null) {
            for (String queryGroup : tstData.groups()) {
                queryData.add(queryGroup);
            }
        }
        String query = queryData.get(0);
        String dbEnvironment = queryData.get(1);
        String sid = queryData.get(2);

        String dbServerIp = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, dbEnvironment + "_IP");
        String userName = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, dbEnvironment + "_USER");
        String password = ApiHelper.getValueOfProperty(CONFIGURATION_FILE_PATH, dbEnvironment + "_PASSWORD");
        String port = "1521";
        DbHelper dbhelper = new DbHelper();

        connection = dbhelper.createDbConnection(dbServerIp, sid, port, userName, password);
        List<Map<String, String>> queryResultData = dbhelper.fetchDatafromDb(connection, query);
        // List<Object[]> collect = tempQueryData.stream().map(temp -> new Object[]
        // {temp}).collect(Collectors.toList());

        // Testng data provider accepts either Object[][] or Iterator<Object[]> as
        // input. Therefore we need to convert the data returned from query to this
        // type, which is done below.
        for (Map<String, String> queryResult : queryResultData) {
            testData.add(new Object[]{queryResult});
        }
        return testData.iterator();
    }

    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getDataFromDataProvider(Method method) {
        String packageName = method.getDeclaringClass().getPackage().getName();
        packageName = packageName.split("RestAssured.")[1];

        packageName = packageName.replace(".", "/");
        String pathName = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test_data"
                + File.separator + packageName + File.separator + method.getName() + ".xlsx";

        return ExcelHelper.getDataFromExcel(pathName);
    }

    @DataProvider(name = "ExcelData")
    public static Iterator<Object[]> getDataFromExcel(Method method) {
        Test classInfo = method.getAnnotation(Test.class);
        List<Object[]> testData = new ArrayList<>();
        String pathName = classInfo.description();
        List<Map<String, String>> excelData = ExcelHelper.fetchDataExcel(pathName, 0);
        for (Map<String, String> rowData : excelData) {
            testData.add(new Object[]{rowData});
        }
        return testData.iterator();
    }

}
