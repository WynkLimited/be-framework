package com.airtel.helper.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Excel (.xlsx) reader using Apache POI try-with-resources.
 */
public final class ExcelHelper {

	private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);

	private ExcelHelper() {
	}

	/**
	 * Returns sheet rows as list of maps (header row → column names).
	 */
	public static List<Map<String, String>> fetchDataExcel(String fileName, int sheetNo) {
		List<Map<String, String>> resultantData = new ArrayList<>();
		try (FileInputStream in = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(in)) {
			Sheet sheet = workbook.getSheetAt(sheetNo);
			Row header = sheet.getRow(0);
			if (header == null) {
				return resultantData;
			}
			int totalColumns = header.getLastCellNum();
			Map<Integer, String> columnMapping = new HashMap<>();
			for (int col = 0; col < totalColumns; col++) {
				Cell cell = header.getCell(col);
				columnMapping.put(col, cell != null ? cell.getStringCellValue() : "COL_" + col);
			}

			int totalRows = sheet.getLastRowNum();
			for (int rowIdx = 1; rowIdx <= totalRows; rowIdx++) {
				Row row = sheet.getRow(rowIdx);
				Map<String, String> rowLevelData = new HashMap<>();
				for (int col = 0; col < totalColumns; col++) {
					String columnName = columnMapping.get(col);
					Cell cell = row != null ? row.getCell(col) : null;
					String dataValue = (cell == null || cell.getCellType() == CellType.BLANK)
							? null
							: getCellValueAsString(cell);
					rowLevelData.put(columnName, dataValue);
				}
				resultantData.add(rowLevelData);
			}
		} catch (IOException e) {
			log.error("Cannot read excel {}", fileName, e);
			throw new RuntimeException("Can't read Excel " + fileName, e);
		}
		return resultantData;
	}

	public static Object[][] getDataFromExcel(String fileName) {
		try (FileInputStream in = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(in)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row header = sheet.getRow(0);
			if (header == null) {
				return new Object[0][0];
			}
			int totalColumns = header.getLastCellNum();
			int totalRows = sheet.getLastRowNum();
			Object[][] testDataObject = new Object[totalRows][totalColumns];

			for (int rowIdx = 1, ci = 0; rowIdx <= totalRows; rowIdx++, ci++) {
				Row row = sheet.getRow(rowIdx);
				for (int col = 0; col < totalColumns; col++) {
					Cell cell = row != null ? row.getCell(col) : null;
					testDataObject[ci][col] = cellValue(cell);
				}
			}
			return testDataObject;
		} catch (IOException e) {
			log.error("Cannot read excel {}", fileName, e);
			throw new RuntimeException("Can't read Excel " + fileName, e);
		}
	}

	private static Object cellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		return switch (cell.getCellType()) {
			case STRING -> {
				String value = cell.getStringCellValue();
				yield "null".equalsIgnoreCase(value) ? null : value;
			}
			case NUMERIC -> DateUtil.isCellDateFormatted(cell)
					? cell.getDateCellValue()
					: cell.getNumericCellValue();
			case BOOLEAN -> cell.getBooleanCellValue();
			case BLANK -> "";
			default -> null;
		};
	}

	private static String getCellValueAsString(Cell cell) {
		return switch (cell.getCellType()) {
			case STRING -> cell.toString();
			case NUMERIC -> {
				if (DateUtil.isCellDateFormatted(cell)) {
					yield new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
				}
				yield Long.toString((long) cell.getNumericCellValue());
			}
			case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
			case BLANK -> "";
			default -> null;
		};
	}
}
