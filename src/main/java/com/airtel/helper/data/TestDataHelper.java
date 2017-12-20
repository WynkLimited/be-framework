package com.airtel.helper.data;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.airtel.helper.common.CommonBaseClass;

public class TestDataHelper extends CommonBaseClass {

	@DataProvider(name = "dbDataProvider")
	protected Iterator<Object[]> getDataFromDB(Method method) {
		List<String> queryData = new ArrayList<String>();
		List<Map<String, String>> queryResultData = new ArrayList<Map<String, String>>();
		List<Object[]> testData = new ArrayList<Object[]>();
		Test tstData = method.getAnnotation(Test.class);
		if (tstData != null) {
			if (tstData.groups() != null) {
				for (String queryGroup : tstData.groups()) {
					queryData.add(queryGroup);
				}
			}
		}
		String query = queryData.get(0);
		String dbEnvironment = queryData.get(1);
		String sid = queryData.get(2);
		connection = DbHelper.createDbConnection(dbEnvironment, sid);
		queryResultData = DbHelper.fetchDatafromDb(connection, query);
		// List<Object[]> collect = tempQueryData.stream().map(temp -> new Object[]
		// {temp}).collect(Collectors.toList());

		// Testng data provider accepts either Object[][] or Iterator<Object[]> as
		// input. Therefore we need to convert the data returned from query to this
		// type, which is done below.
		for (Map<String, String> queryResult : queryResultData) {
			testData.add(new Object[] { queryResult });
		}

		Iterator<Object[]> finalData = testData.iterator();
		return finalData;
	}

	@DataProvider(name = "ExcelDataProvider")
	public static Object[][] getDataFromDataProvider(Method method) {
		String packageName = method.getDeclaringClass().getPackage().getName();
		packageName = packageName.split("RestAssured.")[1];

		packageName = packageName.replace(".", "/");

		String pathName = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test_data"
				+ File.separator + packageName + File.separator + method.getName() + ".xlsx";

		// System.out.println("packageName >>>>>" + packageName + " pathName >>>>>>>" +
		// pathName);

		Object[][] testDataObject = ExcelHelper.getDataFromExcel(pathName);
		return testDataObject;
	}

}
