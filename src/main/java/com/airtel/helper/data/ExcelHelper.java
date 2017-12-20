
package com.airtel.helper.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {

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

	public static int getTotalColoumnNumberInExcel(Row row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		int totalcolumnsInExcel = 0;
		while (cellIterator.hasNext()) {
			cellIterator.next();
			totalcolumnsInExcel++;
		}
		return totalcolumnsInExcel;
	}


}
