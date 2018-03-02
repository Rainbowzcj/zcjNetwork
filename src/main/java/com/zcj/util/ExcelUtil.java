package com.zcj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@SuppressWarnings("deprecation")
public class ExcelUtil {
	// 默认单元格内容为数字时格式
	private static DecimalFormat df = new DecimalFormat("0");
	// 默认单元格格式化日期字符串
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// 格式化数字
	private static DecimalFormat nf = new DecimalFormat("0.00");

	public static ArrayList<ArrayList<Object>> readExcel(File file) {
		if (file == null) {
			return null;
		}
		if (file.getName().endsWith("xlsx")) {
			// 处理ecxel2007
			return readExcel2007(file);
		} else {
			// 处理ecxel2003
			return readExcel2003(file);
		}
	}
	/*
	 * @return 将返回结果存储在ArrayList内，存储结构与二位数组类似
	 * lists.get(0).get(0)表示过去Excel中0行0列单元格
	 */

	public static ArrayList<ArrayList<Object>> readExcel2003(File file) {
		HSSFWorkbook wb = null;
		try {
			ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
			ArrayList<Object> colList;
			wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;
			Object value;
			for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				colList = new ArrayList<Object>();
				if (row == null) {
					// 当读取行为空时
					if (i != sheet.getPhysicalNumberOfRows()) {// 判断是否是最后一行
						rowList.add(colList);
					}
					continue;
				} else {
					rowCount++;
				}
				for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
					cell = row.getCell(j);
					if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
						// 当该单元格为空
						if (j != row.getLastCellNum()) {// 判断是否是该行中最后一个单元格
							colList.add("");
						}
						continue;
					}
					// 读取数据
					value = getStringCellValue(cell);
					colList.add(value);
				} // end for j
				rowList.add(colList);
			} // end for i

			return rowList;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<ArrayList<Object>> readExcel2007(File file) {
		XSSFWorkbook wb = null;
		try {
			ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
			ArrayList<Object> colList;
			wb = new XSSFWorkbook(new FileInputStream(file));
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
			Object value;
			for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				colList = new ArrayList<Object>();
				if (row == null) {
					// 当读取行为空时
					if (i != sheet.getPhysicalNumberOfRows()) {// 判断是否是最后一行
						rowList.add(colList);
					}
					continue;
				} else {
					rowCount++;
				}
				for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
					cell = row.getCell(j);
					if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
						// 当该单元格为空
						if (j != row.getLastCellNum()) {// 判断是否是该行中最后一个单元格
							colList.add("");
						}
						continue;
					}
					// 读取数据
					value = getStringCellValue(cell);
					colList.add(value);
				} // end for j
				rowList.add(colList);
			} // end for i

			return rowList;
		} catch (Exception e) {
			System.out.println("exception");
			return null;
		} finally {
			try {
				if (wb != null)
					wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取每一格的数据
	 * 
	 * @param cell
	 * @return
	 */
	private static Object getStringCellValue(Cell cell) {
		String strCell = "";
		if (cell == null) {
			strCell = "";
		} else {
			int cellType = cell.getCellType();
			// 先判断是否是数字类型
			if (cellType == Cell.CELL_TYPE_NUMERIC) {
				// 数字类型中判断是否是日期类型，如果不是，按string类型处理
				if (DateUtil.isCellDateFormatted(cell)) {
					if (cell.getCellStyle().getDataFormatString().equals("h:mm:ss")) {
						strCell = stf.format(cell.getDateCellValue()); // 日期型
					} else {
						if (cell.getCellStyle().getDataFormatString().equals("m/d/yy")) {
							strCell = sdf.format(cell.getDateCellValue()); // 日期型
						} else {
							strCell = sdtf.format(cell.getDateCellValue()); // 日期型
						}
					}
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);// string类型
					strCell = cell.getStringCellValue();
				}
			} else {// 非数字类型全部按string类型处理
				cell.setCellType(Cell.CELL_TYPE_STRING);
				strCell = cell.getStringCellValue();
			}
		}
		if (strCell == null) {
			strCell = "";
		}
		return strCell;
	}

	public static void writeExcel(ArrayList<ArrayList<Object>> result, String path) {
		if (result == null) {
			return;
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		for (int i = 0; i < result.size(); i++) {
			HSSFRow row = sheet.createRow(i);
			if (result.get(i) != null) {
				for (int j = 0; j < result.get(i).size(); j++) {
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(result.get(i).get(j).toString());
				}
			}
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] content = os.toByteArray();
		File file = new File(path);// Excel文件生成后存储的位置。
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content);
			os.close();
			fos.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DecimalFormat getDf() {
		return df;
	}

	public static void setDf(DecimalFormat df) {
		ExcelUtil.df = df;
	}

	public static SimpleDateFormat getSdf() {
		return sdf;
	}

	public static void setSdf(SimpleDateFormat sdf) {
		ExcelUtil.sdf = sdf;
	}

	public static DecimalFormat getNf() {
		return nf;
	}

	public static void setNf(DecimalFormat nf) {
		ExcelUtil.nf = nf;
	}

}