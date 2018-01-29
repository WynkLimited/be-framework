
package com.airtel.helper.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {

	/**
	 * This method returns the values present in excel in a List of Map. Key of map is the name of the column and value is corresponding to the value of a cell at the row. 
	 * @param fileName
	 * @param sheetNo
	 * @return
	 */
	public static List<Map<String, String>> fetchDataExcel(String fileName, int sheetNo) {
		File dataFile = new File(fileName);
		List<Map<String, String>> resultantData = new ArrayList<Map<String, String>>();

		Map<Integer, String> columnMapping = new HashMap<Integer, String>();
		try {
			int startRow = 1;
			int startCol = 0;
			FileInputStream fileInputStream = new FileInputStream(dataFile);
			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			XSSFSheet datatypeSheet = workbook.getSheetAt(sheetNo);
			Iterator<Row> rowIterator = datatypeSheet.iterator();
			int totalRows = datatypeSheet.getLastRowNum();
			int totalcolumns = getTotalColoumnNumberInExcel(rowIterator.next());

			// Mapping column data with index
			for (startCol = 0; startCol < totalcolumns; startCol++) {
				String columnName = datatypeSheet.getRow(0).getCell(startCol).getStringCellValue();
				columnMapping.put(startCol, columnName);
			}
			for (startRow = 1; startRow <= totalRows; startRow++) {
				Map<String, String> rowLevelData = new HashMap<>();
				for (startCol = 0; startCol < totalcolumns; startCol++) {
					String columnName = columnMapping.get(startCol);
					String dataValue = "";
					Cell cellValue = datatypeSheet.getRow(startRow).getCell(startCol);
					if (cellValue == null || cellValue.getCellType() == Cell.CELL_TYPE_BLANK) {
						dataValue = null;
					} else {
						dataValue = getCellValueAsString(cellValue);
					}
					rowLevelData.put(columnName, dataValue);
				}
				resultantData.add(rowLevelData);
			}
			workbook.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + fileName + " was not found" + e.getStackTrace().toString());

		} catch (IOException e) {
			throw new RuntimeException("Can't Read Excel " + fileName + e.getStackTrace().toString());
		}
		return resultantData;
	}

	public static Object[][] getDataFromExcel(String fileName) {
		File file = new File(fileName);

		Object[][] testDataObject = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(file);

			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			XSSFSheet datatypeSheet = workbook.getSheetAt(0);

			int startRow = 1;
			int startCol = 0;

			int ci = 0, cj = 0;
			Iterator<Row> rowIterator = datatypeSheet.iterator();
			int totalRows = datatypeSheet.getLastRowNum();
			int totalcolumns = getTotalColoumnNumberInExcel(rowIterator.next());

			testDataObject = new Object[totalRows][totalcolumns];

			for (startRow = 1; startRow <= totalRows; startRow++, ci++) {
				cj = 0;
				for (startCol = 0; startCol < totalcolumns; startCol++, cj++) {

					Cell cell = datatypeSheet.getRow(startRow).getCell(startCol);
					if (cell == null) {
						testDataObject[ci][cj] = null;
					} else {
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING: {
							testDataObject[ci][cj] = cell.getStringCellValue().equalsIgnoreCase("null") ? null
									: cell.getStringCellValue();
							break;
						}
						case Cell.CELL_TYPE_NUMERIC: {
							testDataObject[ci][cj] = cell.getNumericCellValue();
							break;
						}
						case Cell.CELL_TYPE_BOOLEAN: {
							testDataObject[ci][cj] = cell.getBooleanCellValue();
							break;
						}
						case Cell.CELL_TYPE_BLANK: {
							testDataObject[ci][cj] = "";
							break;
						}
						default:
						}
					}

				}
				workbook.close();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + fileName + " was not found" + e.getStackTrace().toString());

		} catch (IOException e) {
			throw new RuntimeException("Can't Read Excel " + fileName + e.getStackTrace().toString());
		}
		return testDataObject;
	}

	private static int getTotalColoumnNumberInExcel(Row row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		int totalcolumnsInExcel = 0;
		while (cellIterator.hasNext()) {
			cellIterator.next();
			totalcolumnsInExcel++;
		}
		return totalcolumnsInExcel;
	}

	/**
	 * This method for the type of data in the cell, extracts the data and returns
	 * it as a string.
	 */
	private static String getCellValueAsString(Cell cell) {
		String strCellValue = null;

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			strCellValue = cell.toString();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				strCellValue = dateFormat.format(cell.getDateCellValue());
			} else {
				Double value = cell.getNumericCellValue();
				Long longValue = value.longValue();
				strCellValue = new String(longValue.toString());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			strCellValue = new String(new Boolean(cell.getBooleanCellValue()).toString());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCellValue = "";
			break;
		}
		return strCellValue;
	}

}
