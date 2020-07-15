package csg.ios.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ExcelUpLoadUtils {
	protected static final Logger log = LoggerFactory.getLogger(ExcelUpLoadUtils.class);

	public static final String XLSX = ".xlsx";
	public static final String XLS = ".xls";

	public static JSONArray readExcel(File file) throws Exception {
		int res = checkFile(file);

		if (res == 0) {
			log.error("File not found");
		} else if (res == 1) {
			return readXLSX(file);
		} else if (res == 2) {
			return readXLS(file);
		}
		JSONArray array = new JSONArray();
		return array;
	}

	public static JSONArray readExcel(MultipartFile file) throws Exception {
		int res = checkMultipartFile(file);

		if (res == 0) {
			log.error("File not found");
		} else if (res == 1) {
			return readXLSX(file);
		} else if (res == 2) {
			return readXLS(file);
		}
		JSONArray array = new JSONArray();
		return array;
	}

	/**
	 * 判断File文件的类型
	 * 
	 * @param file 传入的文件
	 * @return 0-文件为空，1-XLSX文件，2-XLS文件，3-其他文件
	 */
	public static int checkFile(File file) {
		if (file == null) {
			return 0;
		}
		String flieName = file.getName();
		if (flieName.endsWith(XLSX)) {
			return 1;
		}
		if (flieName.endsWith(XLS)) {
			return 2;
		}
		return 3;
	}

	public static int checkMultipartFile(MultipartFile file) {
		if (file == null) {
			return 0;
		}
		String flieName = file.getOriginalFilename();
		if (flieName.endsWith(XLSX)) {
			return 1;
		}
		if (flieName.endsWith(XLS)) {
			return 2;
		}
		return 3;
	}

	/**
	 * 读取XLSX文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static JSONArray readXLSX(File file) throws InvalidFormatException, IOException {
		Workbook book = new XSSFWorkbook(file);
		Sheet sheet = book.getSheetAt(0);
		return read(sheet, book);
	}

	public static JSONArray readXLSX(MultipartFile file) throws InvalidFormatException, IOException {
		Workbook book = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = book.getSheetAt(0);
		return read(sheet, book);
	}

	/**
	 * 读取XLS文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static JSONArray readXLS(File file) throws FileNotFoundException, IOException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
		Workbook book = new HSSFWorkbook(poifsFileSystem);
		Sheet sheet = book.getSheetAt(0);
		return read(sheet, book);
	}

	public static JSONArray readXLS(MultipartFile file) throws FileNotFoundException, IOException {
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(file.getInputStream());
		Workbook book = new HSSFWorkbook(poifsFileSystem);
		Sheet sheet = book.getSheetAt(0);
		return read(sheet, book);
	}

	/**
	 * 解析数据
	 * 
	 * @param sheet 表格sheet对象
	 * @param book  用于流关闭
	 * @return
	 * @throws IOException
	 */
	public static JSONArray read(Sheet sheet, Workbook book) throws IOException {
		int rowStart = sheet.getFirstRowNum(); // 首行下标
		int rowEnd = sheet.getLastRowNum(); // 尾行下标
		// 如果首行与尾行相同，表明只有一行，直接返回空数组
		if (rowStart == rowEnd) {
			book.close();
			return new JSONArray();
		}
		// 获取第一行JSON对象键
		Row firstRow = sheet.getRow(rowStart);
		int cellStart = firstRow.getFirstCellNum();
		int cellEnd = firstRow.getLastCellNum();
		Map<Integer, String> keyMap = new HashMap<Integer, String>();
		for (int j = cellStart; j < cellEnd; j++) {
			keyMap.put(j, getValue(firstRow.getCell(j), rowStart, j, book, true));
		}
		// 获取每行JSON对象的值
		JSONArray array = new JSONArray();
		for (int i = rowStart + 1; i <= rowEnd; i++) {
			Row eachRow = sheet.getRow(i);
			JSONObject obj = new JSONObject();
			StringBuffer sb = new StringBuffer();
			for (int k = cellStart; k < cellEnd; k++) {
				if (eachRow != null) {
					String val = getValue(eachRow.getCell(k), i, k, book, false);
					sb.append(val); // 所有数据添加到里面，用于判断该行是否为空
					obj.put(keyMap.get(k), val);
				}
			}
			if (sb.toString().length() > 0) {
				array.add(obj);
			}
		}
		book.close();
		return array;
	}

	/**
	 * 获取每个单元格的数据
	 * 
	 * @param cell   单元格对象
	 * @param rowNum 第几行
	 * @param index  该行第几个
	 * @param book   主要用于关闭流
	 * @param isKey  是否为键：true-是，false-不是。 如果解析Json键，值为空时报错；如果不是Json键，值为空不报错
	 * @return
	 * @throws IOException
	 */
	public static String getValue(Cell cell, int rowNum, int index, Workbook book, boolean isKey) throws IOException {

		// 判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) {
			return "";
		}
		String cellValue = "";
		int cellType = cell.getCellType();
		switch (cellType) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			short format = cell.getCellStyle().getDataFormat();
			if (DateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat sdf = null;
				// System.out.println("cell.getCellStyle().getDataFormat()="+cell.getCellStyle().getDataFormat());
				if (format == 20 || format == 32) {
					sdf = new SimpleDateFormat("HH:mm");
				} else if (format == 14 || format == 31 || format == 57 || format == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil.getJavaDate(value);
					cellValue = sdf.format(date);
				} else {// 日期
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
				try {
					cellValue = sdf.format(cell.getDateCellValue());// 日期
				} catch (Exception e) {
					try {
						throw new Exception("exception on get date data !".concat(e.toString()));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} finally {
					sdf = null;
				}
			} else {
				if (format == 14 || format == 31 || format == 57 || format == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil.getJavaDate(value);
					cellValue = sdf1.format(date);
				} else {
					BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
					cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
				}
			}
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = cell.getBooleanCellValue() + "";
			;
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellValue = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
			cellValue = "";
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			cellValue = "ERROR VALUE";
			break;
		default:
			cellValue = "UNKNOW VALUE";
			break;
		}
		return cellValue;
	}

}
