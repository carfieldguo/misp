package com.groqdata.common.utils.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.groqdata.common.exception.file.ExcelException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groqdata.common.annotation.Excel;
import com.groqdata.common.annotation.Excel.ColumnType;
import com.groqdata.common.annotation.Excel.Type;
import com.groqdata.common.annotation.Excels;
import com.groqdata.common.config.MispConfig;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.core.text.Convert;
import com.groqdata.common.exception.UtilException;
import com.groqdata.common.utils.basic.DateHelper;
import com.groqdata.common.utils.DictUtils;
import com.groqdata.common.utils.basic.StringHelper;
import com.groqdata.common.utils.file.FileTypeUtils;
import com.groqdata.common.utils.file.FileUtils;
import com.groqdata.common.utils.file.ImageUtils;
import com.groqdata.common.utils.reflect.ReflectUtils;

/**
 * Excel相关处理工具类
 *
 * @author MISP TEAM
 */
public class ExcelUtil<T> {
	private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * 用于dictType属性数据存储，避免重复查缓存
	 */
	private final Map<String, String> sysDictMap = new HashMap<>();

	/**
	 * 工作表名称
	 */
	private String sheetName;

	/**
	 * 导出类型（EXPORT:导出数据；IMPORT：导入模板）
	 */
	private Type type;

	/**
	 * 工作薄对象
	 */
	private Workbook wb;

	/**
	 * 工作表对象
	 */
	private Sheet sheet;

	/**
	 * 样式列表
	 */
	private Map<String, CellStyle> styles;

	/**
	 * 导入导出数据列表
	 */
	private List<T> list;

	/**
	 * 注解列表
	 */
	private List<Object[]> fields;

	/**
	 * 当前行号
	 */
	private int rownum;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 最大高度
	 */
	private short maxHeight;

	/**
	 * 合并后最后行数
	 */
	private int subMergedLastRowNum = 0;

	/**
	 * 合并后开始行数
	 */
	private int subMergedFirstRowNum = 1;

	/**
	 * 对象的子列表方法
	 */
	private Method subMethod;

	/**
	 * 对象的子列表属性
	 */
	private List<Field> subFields;

	/**
	 * 统计列表
	 */
	private final Map<Integer, Double> statistics = new HashMap<>();

	/**
	 * 数字格式
	 */
	private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

	/**
	 * 实体对象
	 */
	private final Class<T> clazz;

	public Class<T> getClazz() {
		return clazz;
	}
	/**
	 * 需要排除列属性
	 */
	private String[] excludeFields;

	public String[] getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(String[] excludeFields) {
		this.excludeFields = excludeFields;
	}

	public ExcelUtil(Class<T> clazz) {
		this.clazz = clazz;
	}

	public String getDictValue(String key) {
		return sysDictMap.get(key);
	}

	public void putDictValue(String key, String value) {
		sysDictMap.put(key, value);
	}

	/**
	 * 隐藏Excel中列属性
	 *
	 * @param fields 列属性名 示例[单个"name"/多个"id","name"]
	 */
	public void hideColumn(String... fields) {
		this.excludeFields = fields;
	}

	/**
	 * 初始化Excel导出/导入参数
	 *
	 * @param dataList 数据列表
	 * @param sheetNameParam 工作表名称
	 * @param titleParam 标题
	 * @param typeParam 导出类型
	 */
	public void init(List<T> dataList, String sheetNameParam, String titleParam, Type typeParam) {
		if (dataList == null) {
			dataList = new ArrayList<>();
		}
		this.list = dataList;
		this.sheetName = sheetNameParam;
		this.type = typeParam;
		this.title = titleParam;
		createExcelField();
		createWorkbook();
		createTitle();
		createSubHead();
	}

	/**
	 * 创建excel第一行标题
	 */
	public void createTitle() {
		if (StringUtils.isNotEmpty(title)) {
			subMergedFirstRowNum++;
			subMergedLastRowNum++;
			int titleLastCol = this.fields.size() - 1;
			if (isSubList()) {
				titleLastCol = titleLastCol + subFields.size() - 1;
			}
			Row titleRow = sheet.createRow(rownum == 0 ? rownum++ : 0);
			titleRow.setHeightInPoints(30);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(styles.get(ExcelConstants.STYLE_TYPE_TITLE));
			titleCell.setCellValue(title);
			sheet.addMergedRegion(
					new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(),
							titleLastCol));
		}
	}

	/**
	 * 创建对象的子列表名称
	 */
	public void createSubHead() {
		if (isSubList()) {
			subMergedFirstRowNum++;
			subMergedLastRowNum++;
			Row subRow = sheet.createRow(rownum);
			int excelNum = 0;
			for (Object[] objects : fields) {
				Excel attr = (Excel) objects[1];
				Cell headCell1 = subRow.createCell(excelNum);
				headCell1.setCellValue(attr.name());
				headCell1.setCellStyle(
						styles.get(
								StringHelper.format(ExcelConstants.HEADER_ID_FORMAT, attr.headerColor(),
										attr.headerBackgroundColor())));
				excelNum++;
			}
			int headFirstRow = excelNum - 1;
			int headLastRow = headFirstRow + subFields.size() - 1;
			if (headLastRow > headFirstRow) {
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, headFirstRow, headLastRow));
			}
			rownum++;
		}
	}

	/**
	 * 对excel表单默认第一个索引名转换成list
	 *
	 * @param inputStream 输入流
	 * @return 转换后集合
	 */
	public List<T> importExcel(InputStream inputStream) {
		List<T> resultList = null;
		try {
			resultList = importExcel(inputStream, 0);
		} catch (Exception e) {
			log.error("导入Excel异常{}", e.getMessage());
			throw new UtilException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return resultList;
	}

	/**
	 * 对excel表单默认第一个索引名转换成list
	 *
	 * @param inputStream 输入流
	 * @param titleNum 标题占用行数
	 * @return 转换后集合
	 */
	public List<T> importExcel(InputStream inputStream, int titleNum) throws ExcelException {
		return importExcel(StringUtils.EMPTY, inputStream, titleNum);
	}

	/**
	 * 对excel表单指定表格索引名转换成list
	 *
	 * @param sheetNameParam 表格索引名
	 * @param titleNum 标题占用行数
	 * @param inputStream 输入流
	 * @return 转换后集合
	 */
	public List<T> importExcel(String sheetNameParam, InputStream inputStream, int titleNum) throws ExcelException {
		this.type = Type.IMPORT;

		try {
			this.wb = WorkbookFactory.create(inputStream);
		} catch (Exception e) {
			throw new ExcelException("创建Excel工作簿失败", e);
		}

		List<T> resultList = new ArrayList<>();
		Sheet targetSheet = getTargetSheet(sheetNameParam);
		boolean isXSSFWorkbook = !(wb instanceof HSSFWorkbook);
		Map<String, PictureData> pictures = loadPictures(targetSheet, isXSSFWorkbook);

		int rows = targetSheet.getLastRowNum();
		if (rows <= titleNum) {
			return resultList;
		}

		Row headerRow = targetSheet.getRow(titleNum);
		if (headerRow == null) {
			throw new ExcelException("表头行不存在");
		}

		Map<String, Integer> cellMap = buildHeaderMap(headerRow);
		List<Object[]> fieldList = this.getFields();
		Map<Integer, Object[]> fieldsMap = buildFieldsMap(cellMap, fieldList);

		for (int i = titleNum + 1; i <= rows; i++) {
			Row row = targetSheet.getRow(i);
			if (isRowEmpty(row)) {
				continue;
			}
			T entity = convertRowToEntity(row, fieldsMap, pictures);
			resultList.add(entity);
		}

		return resultList;
	}

	private Sheet getTargetSheet(String sheetNameParam) throws ExcelException {
		Sheet targetSheet = StringUtils.isNotEmpty(sheetNameParam) ? wb.getSheet(sheetNameParam) : wb.getSheetAt(0);
		if (targetSheet == null) {
			throw new ExcelException("指定的Sheet不存在");
		}
		return targetSheet;
	}

	private Map<String, PictureData> loadPictures(Sheet sheet, boolean isXSSF) {
		if (isXSSF) {
			return getSheetPictures07((XSSFSheet) sheet);
		} else {
			return getSheetPictures03((HSSFSheet) sheet, (HSSFWorkbook) wb);
		}
	}

	private Map<String, Integer> buildHeaderMap(Row headerRow) {
		Map<String, Integer> cellMap = new HashMap<>();
		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
			Cell cell = headerRow.getCell(i);
			String value = StringHelper.isNotNull(cell) ? getCellValue(headerRow, i).toString() : null;
			cellMap.put(value, i);
		}
		return cellMap;
	}

	private Map<Integer, Object[]> buildFieldsMap(Map<String, Integer> cellMap, List<Object[]> fieldList) {
		Map<Integer, Object[]> fieldsMap = new HashMap<>();
		for (Object[] objects : fieldList) {
			Excel attr = (Excel) objects[1];
			Integer column = cellMap.get(attr.name());
			if (column != null) {
				fieldsMap.put(column, objects);
			}
		}
		return fieldsMap;
	}

	private T convertRowToEntity(Row row, Map<Integer, Object[]> fieldsMap, Map<String, PictureData> pictures)
			throws ExcelException {
		T entity = null;
		try {
			for (Map.Entry<Integer, Object[]> entry : fieldsMap.entrySet()) {
				Object val = getCellValue(row, entry.getKey());
				entity = (entity == null ? clazz.getDeclaredConstructor().newInstance() : entity);

				Field field = (Field) entry.getValue()[0];
				Excel attr = (Excel) entry.getValue()[1];
				val = convertCellValue(val, field, attr, row, entry.getKey(), pictures);
				String propertyName = buildPropertyName(field, attr);
				ReflectUtils.invokeSetter(entity, propertyName, val);
			}
		} catch (Exception e) {
			throw new ExcelException("转换行数据失败", e);
		}
		return entity;
	}

	private Object convertCellValue(Object val, Field field, Excel attr, Row row, int columnIndex,
			Map<String, PictureData> pictures) {
		Class<?> fieldType = field.getType();

		// 根据字段类型进行转换
		val = convertByFieldType(val, fieldType, attr);

		// 根据注解属性进行进一步处理
		val = processByAnnotation(val, attr, row, columnIndex, pictures);

		return val;
	}

	private Object convertByFieldType(Object val, Class<?> fieldType, Excel attr) {
		if (String.class == fieldType) {
			return convertToString(val, attr);
		} else if (isIntegerType(fieldType) && StringUtils.isNumeric(Convert.toStr(val))) {
			return Convert.toInt(val);
		} else if (isLongType(fieldType) && StringUtils.isNumeric(Convert.toStr(val))) {
			return Convert.toLong(val);
		} else if (isDoubleType(fieldType)) {
			return Convert.toDouble(val);
		} else if (isFloatType(fieldType)) {
			return Convert.toFloat(val);
		} else if (BigDecimal.class == fieldType) {
			return Convert.toBigDecimal(val);
		} else if (Date.class == fieldType) {
			return convertToDate(val);
		} else if (isBooleanType(fieldType)) {
			return Convert.toBool(val, false);
		}
		return val;
	}

	private Object convertToString(Object val, Excel attr) {
		String s = Convert.toStr(val);
		if (StringUtils.endsWith(s, ".0")) {
			return StringUtils.substringBefore(s, ".0");
		} else {
			String dateFormat = attr.dateFormat();
			if (StringUtils.isNotEmpty(dateFormat)) {
				return parseDateToStr(dateFormat, val);
			} else {
				return Convert.toStr(val);
			}
		}
	}

	private Object convertToDate(Object val) {
		if (val instanceof String) {
			return DateHelper.parseDate(val);
		} else if (val instanceof Double doubleVal) {
			return DateUtil.getJavaDate(doubleVal);
		}
		return val;
	}

	private boolean isIntegerType(Class<?> fieldType) {
		return Integer.TYPE == fieldType || Integer.class == fieldType;
	}

	private boolean isLongType(Class<?> fieldType) {
		return Long.TYPE == fieldType || Long.class == fieldType;
	}

	private boolean isDoubleType(Class<?> fieldType) {
		return Double.TYPE == fieldType || Double.class == fieldType;
	}

	private boolean isFloatType(Class<?> fieldType) {
		return Float.TYPE == fieldType || Float.class == fieldType;
	}

	private boolean isBooleanType(Class<?> fieldType) {
		return Boolean.TYPE == fieldType || Boolean.class == fieldType;
	}

	private Object processByAnnotation(Object val, Excel attr, Row row, int columnIndex,
			Map<String, PictureData> pictures) {
		if (StringUtils.isNotEmpty(attr.readConverterExp())) {
			val = reverseByExp(Convert.toStr(val), attr.readConverterExp(), attr.separator());
		} else if (StringUtils.isNotEmpty(attr.dictType())) {
			String key = attr.dictType() + val;
			if (!sysDictMap.containsKey(key)) {
				String dictValue = reverseDictByExp(Convert.toStr(val), attr.dictType(), attr.separator());
				sysDictMap.put(key, dictValue);
			}
			val = sysDictMap.get(key);
		} else if (!attr.handler().equals(ExcelHandlerAdapter.class)) {
			val = dataFormatHandlerAdapter(val, attr, null);
		} else if (ColumnType.IMAGE == attr.cellType() && StringHelper.isNotEmpty(pictures)) {
			PictureData image = pictures.get(row.getRowNum() + "_" + columnIndex);
			if (image == null) {
				val = "";
			} else {
				try {
					byte[] data = image.getData();
					val = FileUtils.writeImportBytes(data);
				} catch (Exception e) {
					val = "";
				}
			}
		}
		return val;
	}

	private String buildPropertyName(Field field, Excel attr) {
		String propertyName = field.getName();
		if (StringUtils.isNotEmpty(attr.targetAttr())) {
			propertyName = field.getName() + "." + attr.targetAttr();
		}
		return propertyName;
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param dataList 导出数据集合
	 * @param sheetNameParam 工作表的名称
	 * @return 结果
	 */
	public AjaxResult exportExcel(List<T> dataList, String sheetNameParam) {
		return exportExcel(dataList, sheetNameParam, StringUtils.EMPTY);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param dataList 导出数据集合
	 * @param sheetNameParam 工作表的名称
	 * @param titleParam 标题
	 * @return 结果
	 */
	public AjaxResult exportExcel(List<T> dataList, String sheetNameParam, String titleParam) {
		this.init(dataList, sheetNameParam, titleParam, Type.EXPORT);
		return exportExcel();
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param response 返回数据
	 * @param dataList 导出数据集合
	 * @param sheetNameParam 工作表的名称
	 */
	public void exportExcel(HttpServletResponse response, List<T> dataList, String sheetNameParam) {
		exportExcel(response, dataList, sheetNameParam, StringUtils.EMPTY);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param response 返回数据
	 * @param dataList 导出数据集合
	 * @param sheetNameParam 工作表的名称
	 * @param titleParam 标题
	 */
	public void exportExcel(HttpServletResponse response, List<T> dataList, String sheetNameParam, String titleParam) {
		response.setContentType(ExcelConstants.OFFICE_SPREAD_SHEET);
		response.setCharacterEncoding("utf-8");
		this.init(dataList, sheetNameParam, titleParam, Type.EXPORT);
		exportExcel(response);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param sheetNameParam 工作表的名称
	 * @return 结果
	 */
	public AjaxResult importTemplateExcel(String sheetNameParam) {
		return importTemplateExcel(sheetNameParam, StringUtils.EMPTY);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param sheetNameParam 工作表的名称
	 * @param titleParam 标题
	 * @return 结果
	 */
	public AjaxResult importTemplateExcel(String sheetNameParam, String titleParam) {
		this.init(null, sheetNameParam, titleParam, Type.IMPORT);
		return exportExcel();
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param response HttpServletResponse对象
	 * @param sheetNameParam 工作表的名称
	 */
	public void importTemplateExcel(HttpServletResponse response, String sheetNameParam) {
		importTemplateExcel(response, sheetNameParam, StringUtils.EMPTY);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param response HttpServletResponse对象
	 * @param sheetNameParam 工作表的名称
	 * @param titleParam 标题
	 */
	public void importTemplateExcel(HttpServletResponse response, String sheetNameParam, String titleParam) {
		response.setContentType(ExcelConstants.OFFICE_SPREAD_SHEET);
		response.setCharacterEncoding("utf-8");
		this.init(null, sheetNameParam, titleParam, Type.IMPORT);
		exportExcel(response);
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @param response HttpServletResponse对象
	 */
	public void exportExcel(HttpServletResponse response) {
		try {
			writeSheet();
			wb.write(response.getOutputStream());
		} catch (Exception e) {
			log.error("导出Excel异常{}", e.getMessage());
		} finally {
			IOUtils.closeQuietly(wb);
		}
	}

	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 *
	 * @return 结果
	 */
	public AjaxResult exportExcel() {
		try {
			writeSheet();
			String filename = encodingFilename(sheetName);
			File absoluteFile = new File(getAbsoluteFile(filename)); // 使用 try-with-resources 自动关闭 OutputStream
			try (OutputStream out = new FileOutputStream(absoluteFile)) {
				wb.write(out);
				return AjaxResult.success(filename);
			}
		} catch (Exception e) {
			log.error("导出Excel异常{}", e.getMessage(), e);
			throw new UtilException("导出Excel失败，请联系网站管理员！");
		} finally {
			IOUtils.closeQuietly(wb);
		}
	}

	/**
	 * 创建写入数据到Sheet
	 */
	public void writeSheet() {
		// 取出一共有多少个sheet.
		int sheetNo = Math.max(1, (int) Math.ceil(list.size() * 1.0 / ExcelConstants.DEFAULT_MAX_SHEET_SIZE));
		for (int sheetIndex = 0; sheetIndex < sheetNo; sheetIndex++) {
			createSheet(sheetNo, sheetIndex);

			// 产生一行
			Row row = sheet.createRow(rownum);
			int column = 0;
			// 写入各个字段的列头名称
			for (Object[] os : fields) {
				Field field = (Field) os[0];
				Excel excel = (Excel) os[1];
				if (Collection.class.isAssignableFrom(field.getType())) {
					for (Field subField : subFields) {
						Excel subExcel = subField.getAnnotation(Excel.class);
						this.createHeadCell(subExcel, row, column++);
					}
				} else {
					this.createHeadCell(excel, row, column++);
				}
			}
			if (Type.EXPORT.equals(type)) {
				fillExcelData(sheetIndex, row);
				addStatisticsRow();
			}
		}
	}

	/**
	 * 填充excel数据
	 *
	 * @param sheetIndex 序号
	 * @param targetRow 单元格行
	 */
	public void fillExcelData(int sheetIndex, Row targetRow) {
		int startNo = sheetIndex * ExcelConstants.DEFAULT_MAX_SHEET_SIZE;
		int endNo = Math.min(startNo + ExcelConstants.DEFAULT_MAX_SHEET_SIZE, list.size());

		for (int i = startNo; i < endNo; i++) {
			int rowNo = calculateRowNumber(i, startNo);
			Row currentRow = sheet.createRow(rowNo);

			// 得到导出对象.
			T vo = list.get(i);
			Collection<?> subList = getSubListIfApplicable(vo);

			int column = 0;
			for (Object[] os : fields) {
				Field field = (Field) os[0];
				Excel excel = (Excel) os[1];

				if (shouldProcessSubList(field, subList)) {
					processSubListField(currentRow, subList, column, rowNo);
					column += subFields.size();
				} else {
					addCell(excel, currentRow, vo, field, column++);
				}
			}
		}
	}

	/**
	 * 计算行号
	 *
	 * @param index 当前索引
	 * @param startNo 起始编号
	 * @return 行号
	 */
	private int calculateRowNumber(int index, int startNo) {
		if (isSubList()) {
			if (index > 1) {
				return (1 + rownum) - startNo + index - 1;
			} else {
				return (1 + rownum) - startNo + index;
			}
		} else {
			return index + 1 + rownum - startNo;
		}
	}

	/**
	 * 获取子列表（如果适用）
	 *
	 * @param vo 对象实例
	 * @return 子列表
	 */
	private Collection<?> getSubListIfApplicable(T vo) {
		if (isSubList()) {
			if (isSubListValue(vo)) {
				Collection<?> subList = getListCellValue(vo);
				subMergedLastRowNum = subMergedLastRowNum + subList.size();
				return subList;
			} else {
				subMergedFirstRowNum++;
				subMergedLastRowNum++;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 判断是否应该处理子列表字段
	 *
	 * @param field 字段
	 * @param subList 子列表
	 * @return 是否应该处理子列表
	 */
	private boolean shouldProcessSubList(Field field, Collection<?> subList) {
		return Collection.class.isAssignableFrom(field.getType()) && StringHelper.isNotNull(subList);
	}

	/**
	 * 处理子列表字段
	 *
	 * @param currentRow 当前行
	 * @param subList 子列表
	 * @param column 列索引
	 * @param rowNo 行号
	 */
	private void processSubListField(Row currentRow, Collection<?> subList,
			int column, int rowNo) {
		boolean subFirst = false;
		int currentRowNum = rowNo;

		for (Object obj : subList) {
			if (subFirst) {
				currentRowNum++;
				currentRow = sheet.createRow(currentRowNum);
			}

			List<Field> subFieldsList = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), Excel.class);
			processSubFields(currentRow, obj, subFieldsList, column);

			subFirst = true;
		}

		subMergedFirstRowNum = subMergedFirstRowNum + subList.size();
	}

	/**
	 * 处理子字段
	 *
	 * @param currentRow 当前行
	 * @param obj 对象实例
	 * @param subFieldsList 子字段列表
	 * @param column 列索引
	 */
	private void processSubFields(Row currentRow, Object obj, List<Field> subFieldsList, int column) {
		int subIndex = 0;
		for (Field subField : subFieldsList) {
			if (subField.isAnnotationPresent(Excel.class)) {
				setFieldAccessible(subField);
				Excel attr = subField.getAnnotation(Excel.class);
				addCell(attr, currentRow, (T) obj, subField, column + subIndex);
			}
			subIndex++;
		}
	}

	/**
	 * 创建表格样式
	 *
	 * @param workbook 工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook workbook) {
		// 写入各条记录,每条记录对应excel表中的一行
		Map<String, CellStyle> targetStyles = new HashMap<>();
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		Font titleFont = workbook.createFont();
		titleFont.setFontName(ExcelConstants.DEFAULT_FONT_NAME);
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBold(true);
		style.setFont(titleFont);
		DataFormat dataFormat = workbook.createDataFormat();
		style.setDataFormat(dataFormat.getFormat("@"));
		targetStyles.put(ExcelConstants.STYLE_TYPE_TITLE, style);

		style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = workbook.createFont();
		dataFont.setFontName(ExcelConstants.DEFAULT_FONT_NAME);
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		targetStyles.put(ExcelConstants.STYLE_TYPE_DATA, style);

		style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		Font totalFont = workbook.createFont();
		totalFont.setFontName(ExcelConstants.DEFAULT_FONT_NAME);
		totalFont.setFontHeightInPoints((short) 10);
		style.setFont(totalFont);
		targetStyles.put(ExcelConstants.STYLE_TYPE_TOTAL, style);

		targetStyles.putAll(annotationHeaderStyles(workbook, targetStyles));

		targetStyles.putAll(annotationDataStyles());

		return targetStyles;
	}

	/**
	 * 根据Excel注解创建表格头样式
	 *
	 * @param workbook 工作薄对象
	 * @param targetStyles 样式列表
	 * @return 自定义样式列表
	 */
	private Map<String, CellStyle> annotationHeaderStyles(Workbook workbook, Map<String, CellStyle> targetStyles) {
		Map<String, CellStyle> headerStyles = new HashMap<>();
		for (Object[] os : fields) {
			Excel excel = (Excel) os[1];
			String key = StringHelper.format(ExcelConstants.HEADER_ID_FORMAT, excel.headerColor(),
					excel.headerBackgroundColor());
			headerStyles.computeIfAbsent(key, k -> {
				CellStyle style = workbook.createCellStyle();
				style.cloneStyleFrom(targetStyles.get(ExcelConstants.STYLE_TYPE_DATA));
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setFillForegroundColor(excel.headerBackgroundColor().index);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				Font headerFont = workbook.createFont();
				headerFont.setFontName(ExcelConstants.DEFAULT_FONT_NAME);
				headerFont.setFontHeightInPoints((short) 10);
				headerFont.setBold(true);
				headerFont.setColor(excel.headerColor().index);
				style.setFont(headerFont);
				DataFormat dataFormat = workbook.createDataFormat();
				style.setDataFormat(dataFormat.getFormat("@"));
				return style;
			});

		}
		return headerStyles;
	}

	/**
	 * 根据Excel注解创建表格列样式
	 *
	 * @return 自定义样式列表
	 */
	private Map<String, CellStyle> annotationDataStyles() {
		Map<String, CellStyle> stylesMap = new HashMap<>();
		for (Object[] os : fields) {
			Field field = (Field) os[0];
			Excel excel = (Excel) os[1];
			if (Collection.class.isAssignableFrom(field.getType())) {
				ParameterizedType pt = (ParameterizedType) field.getGenericType();
				Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
				List<Field> subFieldsList = FieldUtils.getFieldsListWithAnnotation(subClass, Excel.class);
				for (Field subField : subFieldsList) {
					Excel subExcel = subField.getAnnotation(Excel.class);
					annotationDataStyles(stylesMap, subField, subExcel);
				}
			} else {
				annotationDataStyles(stylesMap, field, excel);
			}
		}
		return stylesMap;
	}

	/**
	 * 根据Excel注解创建表格列样式
	 *
	 * @param targetStyles 自定义样式列表
	 * @param targetField  属性列信息
	 * @param excel  注解信息
	 */
	public void annotationDataStyles(Map<String, CellStyle> targetStyles, Field targetField, Excel excel) {
		String key = StringHelper.format(ExcelConstants.DATA_ID_FORMAT, excel.align(), excel.color(),
				excel.backgroundColor(),
				excel.cellType());
		if (!targetStyles.containsKey(key)) {
			CellStyle style = wb.createCellStyle();
			style.setAlignment(excel.align());
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
			style.setBorderLeft(BorderStyle.THIN);
			style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
			style.setBorderTop(BorderStyle.THIN);
			style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(excel.backgroundColor().getIndex());
			Font dataFont = wb.createFont();
			dataFont.setFontName(ExcelConstants.DEFAULT_FONT_NAME);
			dataFont.setFontHeightInPoints((short) 10);
			dataFont.setColor(excel.color().index);
			style.setFont(dataFont);
			if (ColumnType.TEXT == excel.cellType()) {
				DataFormat dataFormat = wb.createDataFormat();
				style.setDataFormat(dataFormat.getFormat("@"));
			}
			targetStyles.put(key, style);
		}
	}

	/**
	 * 创建单元格
	 *
	 * @param excelAnnotation Excel注解
	 * @param headerRow 表头行
	 * @param columnIndex 列索引
	 * @return 单元格对象
	 */
	public Cell createHeadCell(Excel excelAnnotation, Row headerRow, int columnIndex) {
		// 创建列
		Cell cell = headerRow.createCell(columnIndex);
		// 写入列信息
		cell.setCellValue(excelAnnotation.name());
		setDataValidation(excelAnnotation, headerRow, columnIndex);
		cell.setCellStyle(
				styles.get(StringHelper.format(ExcelConstants.HEADER_ID_FORMAT, excelAnnotation.headerColor(),
						excelAnnotation.headerBackgroundColor())));
		if (isSubList()) {
			// 填充默认样式，防止合并单元格样式失效
			sheet.setDefaultColumnStyle(columnIndex,
					styles.get(StringHelper.format(ExcelConstants.DATA_ID_FORMAT, excelAnnotation.align(),
							excelAnnotation.color(), excelAnnotation.backgroundColor(), excelAnnotation.cellType())));
			if (excelAnnotation.needMerge()) {
				sheet.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, columnIndex, columnIndex));
			}
		}
		return cell;
	}

	/**
	* 设置单元格信息
	*
	* @param cellValue 单元格值
	* @param excelAnnotation 注解相关
	* @param cell 单元格信息
	*/
	public void setCellVo(Object cellValue, Excel excelAnnotation, Cell cell) {
		if (isStringOrTextType(excelAnnotation)) {
			handleStringOrTextType(cellValue, excelAnnotation, cell);
		} else if (isNumericType(excelAnnotation)) {
			handleNumericType(cellValue, cell);
		} else if (isImageType(excelAnnotation)) {
			handleImageType(cellValue, cell);
		}
	}

	/**
	 * 判断是否为字符串或文本类型
	 *
	 * @param excel Excel注解
	 * @return 是否为字符串或文本类型
	 */
	private boolean isStringOrTextType(Excel excel) {
		return ColumnType.STRING == excel.cellType() || ColumnType.TEXT == excel.cellType();
	}

	/**
	 * 判断是否为数值类型
	 *
	 * @param excel Excel注解
	 * @return 是否为数值类型
	 */
	private boolean isNumericType(Excel excel) {
		return ColumnType.NUMERIC == excel.cellType();
	}

	/**
	 * 判断是否为图片类型
	 *
	 * @param excel Excel注解
	 * @return 是否为图片类型
	 */
	private boolean isImageType(Excel excel) {
		return ColumnType.IMAGE == excel.cellType();
	}

	/**
	 * 处理字符串或文本类型
	 *
	 * @param cellValue 单元格值
	 * @param excel Excel注解
	 * @param cell 单元格
	 */
	private void handleStringOrTextType(Object cellValue, Excel excel, Cell cell) {
		String cellValueStr = Convert.toStr(cellValue);

		// 对于任何以表达式触发字符 =-+@开头的单元格，直接使用tab字符作为前缀，防止CSV注入。
		if (StringUtils.startsWithAny(cellValueStr, ExcelConstants.FORMULA_STR.toArray(new String[0]))) {
			cellValueStr = RegExUtils.replaceFirst(cellValueStr, ExcelConstants.FORMULA_REGEX_STR, "\t$0");
		}

		if (cellValue instanceof Collection && StringUtils.equals("[]", cellValueStr)) {
			cellValueStr = StringUtils.EMPTY;
		}

		cell.setCellValue(StringHelper.isNull(cellValueStr)
				? excel.defaultValue()
				: cellValueStr + excel.suffix());
	}

	/**
	 * 处理数值类型
	 *
	 * @param cellValue 单元格值
	 * @param cell 单元格
	 */
	private void handleNumericType(Object cellValue, Cell cell) {
		if (!StringHelper.isNotNull(cellValue)) {
			return;
		}

		String valueStr = Convert.toStr(cellValue);
		if (StringUtils.contains(valueStr, ".")) {
			cell.setCellValue(Convert.toDouble(cellValue));
		} else {
			cell.setCellValue(Convert.toInt(cellValue));
		}
	}

	/**
	 * 处理图片类型
	 *
	 * @param cellValue 单元格值
	 * @param cell 单元格
	 */
	private void handleImageType(Object cellValue, Cell cell) {
		ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(),
				cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1), cell.getRow().getRowNum() + 1);
		String imagePath = Convert.toStr(cellValue);
		if (StringUtils.isNotEmpty(imagePath)) {
			byte[] data = ImageUtils.getImage(imagePath);
			getDrawingPatriarch(cell.getSheet()).createPicture(anchor,
					cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
		}
	}

	/**
	 * 获取画布
	 *
	 * @param targetSheet 工作表对象
	 * @return Drawing对象
	 */
	public static Drawing<?> getDrawingPatriarch(Sheet targetSheet) {
		if (targetSheet.getDrawingPatriarch() == null) {
			targetSheet.createDrawingPatriarch();
		}
		return targetSheet.getDrawingPatriarch();
	}

	/**
	 * 获取图片类型,设置图片插入类型
	 *
	 * @param imageData 图片数据
	 * @return 图片类型
	 */
	public int getImageType(byte[] imageData) {
		String imageType = FileTypeUtils.getFileExtendName(imageData);
		if (ExcelConstants.IMAGE_TYPE_JPG.equalsIgnoreCase(imageType)) {
			return Workbook.PICTURE_TYPE_JPEG;
		} else if (ExcelConstants.IMAGE_TYPE_PNG.equalsIgnoreCase(imageType)) {
			return Workbook.PICTURE_TYPE_PNG;
		}
		return Workbook.PICTURE_TYPE_JPEG;
	}

	/**
	 * 创建表格样式
	 *
	 * @param excelAnnotation Excel注解
	 * @param headerRow 表头行
	 * @param columnIndex 列索引
	 */
	public void setDataValidation(Excel excelAnnotation, Row headerRow, int columnIndex) {
		if (excelAnnotation.name().contains("注：")) {
			sheet.setColumnWidth(columnIndex, 6000);
		} else {
			// 设置列宽
			sheet.setColumnWidth(columnIndex, (int) ((excelAnnotation.width() + 0.72) * 256));
		}
		if (StringUtils.isNotEmpty(excelAnnotation.prompt()) || excelAnnotation.combo().length > 0
				|| excelAnnotation.comboReadDict()) {
			String[] comboArray = excelAnnotation.combo();
			if (excelAnnotation.comboReadDict()) {
				if (!sysDictMap.containsKey(ExcelConstants.DICT_COMBO_PREFIX + excelAnnotation.dictType())) {
					String labels = DictUtils.getDictLabels(excelAnnotation.dictType());
					sysDictMap.put(ExcelConstants.DICT_COMBO_PREFIX + excelAnnotation.dictType(), labels);
				}
				String val = sysDictMap.get(ExcelConstants.DICT_COMBO_PREFIX + excelAnnotation.dictType());
				comboArray = StringUtils.split(val, DictUtils.SEPARATOR);
			}
			if (comboArray.length > 15 || StringUtils.join(comboArray).length() > 255) {
				// 如果下拉数大于15或字符串长度大于255，则使用一个新sheet存储，避免生成的模板下拉值获取不到
				setXSSFValidationWithHidden(sheet, comboArray, excelAnnotation.prompt(), 1, 100, columnIndex,
						columnIndex);
			} else {
				// 提示信息或只能选择不能输入的列内容.
				setPromptOrValidation(sheet, comboArray, excelAnnotation.prompt(), 1, 100, columnIndex, columnIndex);
			}
		}
	}

	/**
	 * 添加单元格
	 *
	 * @param excelAttr Excel注解
	 * @param dataRow 数据行
	 * @param vo 对象实例
	 * @param targetField 字段
	 * @param columnIndex 列索引
	 * @return 单元格对象
	 */
	public Cell addCell(Excel excelAttr, Row dataRow, T vo, Field targetField, int columnIndex) {
		Cell cell = null;
		try {
			// 设置行高
			dataRow.setHeight(maxHeight);
			// 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
			if (excelAttr.isExport()) {
				cell = createAndConfigureCell(excelAttr, dataRow, vo, columnIndex);
				Object value = getTargetValue(vo, targetField, excelAttr);
				setCellValueByType(cell, value, excelAttr);
				addStatisticsData(columnIndex, Convert.toStr(value), excelAttr);
			}
		} catch (Exception e) {
			log.error("导出Excel失败{}", e.getMessage(), e);
		}
		return cell;
	}

	/**
	 * 创建并配置单元格
	 *
	 * @param excelAttr Excel注解
	 * @param dataRow 数据行
	 * @param vo 对象实例
	 * @param columnIndex 列索引
	 * @return 单元格对象
	 */
	private Cell createAndConfigureCell(Excel excelAttr, Row dataRow, T vo, int columnIndex) {
		Cell cell = dataRow.createCell(columnIndex);
		if (isSubListValue(vo) && getListCellValue(vo).size() > 1 && excelAttr.needMerge()) {
			CellRangeAddress cellAddress = new CellRangeAddress(subMergedFirstRowNum, subMergedLastRowNum,
					columnIndex, columnIndex);
			sheet.addMergedRegion(cellAddress);
		}
		cell.setCellStyle(styles
				.get(StringHelper.format(ExcelConstants.DATA_ID_FORMAT, excelAttr.align(), excelAttr.color(),
						excelAttr.backgroundColor(), excelAttr.cellType())));
		return cell;
	}

	/**
	 * 根据不同类型设置单元格值
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param excelAttr Excel注解
	 */
	private void setCellValueByType(Cell cell, Object value, Excel excelAttr) {
		if (!shouldSetValue(value, excelAttr)) {
			return;
		}

		String dateFormat = excelAttr.dateFormat();
		String readConverterExp = excelAttr.readConverterExp();
		String dictType = excelAttr.dictType();

		if (StringUtils.isNotEmpty(dateFormat) && StringHelper.isNotNull(value)) {
			handleDateType(cell, value, dateFormat);
		} else if (StringUtils.isNotEmpty(readConverterExp) && StringHelper.isNotNull(value)) {
			handleReadConverterExpType(cell, value, readConverterExp, excelAttr.separator());
		} else if (StringUtils.isNotEmpty(dictType) && StringHelper.isNotNull(value)) {
			handleDictType(cell, value, dictType, excelAttr.separator());
		} else if (value instanceof BigDecimal bigDecimal && -1 != excelAttr.scale()) {
			handleBigDecimalType(cell, bigDecimal, excelAttr);
		} else if (!excelAttr.handler().equals(ExcelHandlerAdapter.class)) {
			handleCustomHandlerType(cell, value, excelAttr);
		} else {
			// 设置列类型
			setCellVo(value, excelAttr, cell);
		}
	}

	/**
	 * 判断是否应该设置值
	 *
	 * @param value 值
	 * @param excelAttr Excel注解
	 * @return 是否应该设置值
	 */
	private boolean shouldSetValue(Object value, Excel excelAttr) {
		return excelAttr.isExport() && StringHelper.isNotNull(value);
	}

	/**
	 * 处理日期类型
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param dateFormat 日期格式
	 */
	private void handleDateType(Cell cell, Object value, String dateFormat) {
		cell.setCellValue(parseDateToStr(dateFormat, value));
	}

	/**
	 * 处理读取转换表达式类型
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param readConverterExp 转换表达式
	 * @param separator 分隔符
	 */
	private void handleReadConverterExpType(Cell cell, Object value, String readConverterExp, String separator) {
		cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
	}

	/**
	 * 处理字典类型
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param dictType 字典类型
	 * @param separator 分隔符
	 */
	private void handleDictType(Cell cell, Object value, String dictType, String separator) {
		String key = dictType + value;
		if (!sysDictMap.containsKey(key)) {
			String label = convertDictByExp(Convert.toStr(value), dictType, separator);
			sysDictMap.put(key, label);
		}
		cell.setCellValue(sysDictMap.get(key));
	}

	/**
	 * 处理BigDecimal类型
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param excelAttr Excel注解
	 */
	private void handleBigDecimalType(Cell cell, BigDecimal value, Excel excelAttr) {
		cell.setCellValue(value.setScale(excelAttr.scale(), excelAttr.roundingMode()).doubleValue());
	}

	/**
	 * 处理自定义处理器类型
	 *
	 * @param cell 单元格
	 * @param value 值
	 * @param excelAttr Excel注解
	 */
	private void handleCustomHandlerType(Cell cell, Object value, Excel excelAttr) {
		cell.setCellValue(dataFormatHandlerAdapter(value, excelAttr, cell));
	}

	/**
	 * 设置 POI XSSFSheet 单元格提示或选择框
	 *
	 * @param targetSheet 表单
	 * @param textlist 下拉框显示的内容
	 * @param promptContent 提示内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 */
	public void setPromptOrValidation(Sheet targetSheet, String[] textlist, String promptContent, int firstRow,
			int endRow,
			int firstCol, int endCol) {
		DataValidationHelper helper = targetSheet.getDataValidationHelper();
		DataValidationConstraint constraint = textlist.length > 0
				? helper.createExplicitListConstraint(textlist)
				: helper.createCustomConstraint("DD1");
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		DataValidation dataValidation = helper.createValidation(constraint, regions);
		if (StringUtils.isNotEmpty(promptContent)) {
			// 如果设置了提示信息则鼠标放上去提示
			dataValidation.createPromptBox("", promptContent);
			dataValidation.setShowPromptBox(true);
		}
		// 处理Excel兼容性问题
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setSuppressDropDownArrow(true);
			dataValidation.setShowErrorBox(true);
		} else {
			dataValidation.setSuppressDropDownArrow(false);
		}
		targetSheet.addValidationData(dataValidation);
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框（兼容超出一定数量的下拉框）.
	 *
	 * @param targetSheet 要设置的sheet.
	 * @param textlist 下拉框显示的内容
	 * @param promptContent 提示内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 */
	public void setXSSFValidationWithHidden(Sheet targetSheet, String[] textlist, String promptContent, int firstRow,
			int endRow, int firstCol, int endCol) {
		String hideSheetName = "combo_" + firstCol + "_" + endCol;
		Sheet hideSheet = wb.createSheet(hideSheetName); // 用于存储 下拉菜单数据
		for (int i = 0; i < textlist.length; i++) {
			hideSheet.createRow(i).createCell(0).setCellValue(textlist[i]);
		}
		// 创建名称，可被其他单元格引用
		Name name = wb.createName();
		name.setNameName(hideSheetName + "_data");
		name.setRefersToFormula(hideSheetName + "!$A$1:$A$" + textlist.length);
		DataValidationHelper helper = targetSheet.getDataValidationHelper();
		// 加载下拉列表内容
		DataValidationConstraint constraint = helper.createFormulaListConstraint(hideSheetName + "_data");
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		DataValidation dataValidation = helper.createValidation(constraint, regions);
		if (StringUtils.isNotEmpty(promptContent)) {
			// 如果设置了提示信息则鼠标放上去提示
			dataValidation.createPromptBox("", promptContent);
			dataValidation.setShowPromptBox(true);
		}
		// 处理Excel兼容性问题
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setSuppressDropDownArrow(true);
			dataValidation.setShowErrorBox(true);
		} else {
			dataValidation.setSuppressDropDownArrow(false);
		}

		targetSheet.addValidationData(dataValidation);
		// 设置hiddenSheet隐藏
		wb.setSheetHidden(wb.getSheetIndex(hideSheet), true);
	}

	/**
	 * 解析导出值 0=男,1=女,2=未知
	 *
	 * @param propertyValue 参数值
	 * @param converterExp 翻译注解
	 * @param separator 分隔符
	 * @return 解析后值
	 */
	public static String convertByExp(String propertyValue, String converterExp, String separator) {
		StringBuilder propertyString = new StringBuilder();
		String[] convertSource = converterExp.split(",");
		for (String item : convertSource) {
			String[] itemArray = item.split("=");
			if (StringUtils.containsAny(propertyValue, separator)) {
				for (String value : propertyValue.split(separator)) {
					if (itemArray[0].equals(value)) {
						propertyString.append(MessageFormat.format("{0}{1}", itemArray[1], separator));
						break;
					}
				}
			} else {
				if (itemArray[0].equals(propertyValue)) {
					return itemArray[1];
				}
			}
		}
		return StringUtils.stripEnd(propertyString.toString(), separator);
	}

	/**
	 * 反向解析值 男=0,女=1,未知=2
	 *
	 * @param propertyValue 参数值
	 * @param converterExp 翻译注解
	 * @param separator 分隔符
	 * @return 解析后值
	 */
	public static String reverseByExp(String propertyValue, String converterExp, String separator) {
		StringBuilder propertyString = new StringBuilder();
		String[] convertSource = converterExp.split(",");
		for (String item : convertSource) {
			String[] itemArray = item.split("=");
			if (StringUtils.containsAny(propertyValue, separator)) {
				for (String value : propertyValue.split(separator)) {
					if (itemArray[1].equals(value)) {
						propertyString.append(itemArray[0]).append(separator);
						break;
					}
				}
			} else {
				if (itemArray[1].equals(propertyValue)) {
					return itemArray[0];
				}
			}
		}
		return StringUtils.stripEnd(propertyString.toString(), separator);
	}

	/**
	 * 解析字典值
	 *
	 * @param dictValue 字典值
	 * @param dictType 字典类型
	 * @param separator 分隔符
	 * @return 字典标签
	 */
	public static String convertDictByExp(String dictValue, String dictType, String separator) {
		return DictUtils.getDictLabel(dictType, dictValue, separator);
	}

	/**
	 * 反向解析值字典值
	 *
	 * @param dictLabel 字典标签
	 * @param dictType 字典类型
	 * @param separator 分隔符
	 * @return 字典值
	 */
	public static String reverseDictByExp(String dictLabel, String dictType, String separator) {
		return DictUtils.getDictValue(dictType, dictLabel, separator);
	}

	/**
	 * 数据处理器
	 *
	 * @param value 数据值
	 * @param excel 数据注解
	 * @param cell 单元格对象
	 * @return 处理后的字符串值
	 */
	public String dataFormatHandlerAdapter(Object value, Excel excel, Cell cell) {
		try {
			Object instance = excel.handler().getDeclaredConstructor().newInstance();
			Method formatMethod = excel.handler().getMethod("format",
					Object.class, String[].class, Cell.class, Workbook.class);
			value = formatMethod.invoke(instance, value, excel.args(), cell, this.wb);
		} catch (Exception e) {
			log.error("不能格式化数据 {}", excel.handler(), e);
		}
		return Convert.toStr(value);
	}

	/**
	 * 合计统计信息
	 *
	 * @param index 列索引
	 * @param text 文本值
	 * @param entity Excel注解
	 */
	private void addStatisticsData(Integer index, String text, Excel entity) {
		if (entity != null && entity.isStatistics()) {
			try {
				double value = Double.parseDouble(text);
				statistics.merge(index, value, Double::sum);
			} catch (NumberFormatException e) {
				// 忽略无法解析为数字的值
				log.debug("无法将文本 '{}' 解析为数字，跳过统计", text);
			}
		}
	}

	/**
	 * 创建统计行
	 */
	public void addStatisticsRow() {
		if (!statistics.isEmpty()) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			Set<Integer> keys = statistics.keySet();
			Cell cell = row.createCell(0);
			cell.setCellStyle(styles.get(ExcelConstants.STYLE_TYPE_TOTAL));
			cell.setCellValue("合计");

			for (Integer key : keys) {
				cell = row.createCell(key);
				cell.setCellStyle(styles.get(ExcelConstants.STYLE_TYPE_TOTAL));
				cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key)));
			}
			statistics.clear();
		}
	}

	/**
	 * 编码文件名
	 *
	 * @param filenameParam 文件名称
	 * @return 编码后的文件名
	 */
	public String encodingFilename(String filenameParam) {
		filenameParam = UUID.randomUUID() + "_" + filenameParam + ".xlsx";
		return filenameParam;
	}

	/**
	 * 获取下载路径
	 *
	 * @param filenameParam 文件名称
	 * @return 文件绝对路径
	 */
	public String getAbsoluteFile(String filenameParam) {
		String downloadPath = MispConfig.getDownloadPath() + filenameParam;
		File desc = new File(downloadPath);
		if (!desc.getParentFile().exists()) {
			desc.getParentFile().mkdirs();
		}
		return downloadPath;
	}

	/**
	 * 获取bean中的属性值
	 *
	 * @param vo 实体对象
	 * @param targetField 字段
	 * @param excel 注解
	 * @return 最终的属性值
	 */
	private Object getTargetValue(T vo, Field targetField, Excel excel) throws Exception {
		Object o = targetField.get(vo);
		if (StringUtils.isNotEmpty(excel.targetAttr())) {
			String target = excel.targetAttr();
			if (target.contains(".")) {
				String[] targets = target.split("[.]");
				for (String name : targets) {
					o = getValue(o, name);
				}
			} else {
				o = getValue(o, target);
			}
		}
		return o;
	}

	/**
	 * 以类的属性的get方法方法形式获取值
	 *
	 * @param obj 对象实例
	 * @param fieldName 字段名称
	 * @return value 属性值
	 */
	private Object getValue(Object obj, String fieldName) throws Exception {
		if (StringHelper.isNotNull(obj) && StringUtils.isNotEmpty(fieldName)) {
			Class<?> objClass = obj.getClass();
			Field field = objClass.getDeclaredField(fieldName);
			setFieldAccessible(field);
			obj = field.get(obj);
		}
		return obj;
	}

	/**
	 * 得到所有定义字段
	 */
	private void createExcelField() {
		this.fields = getFields();
		this.fields = this.fields.stream().sorted(Comparator.comparing(objects -> ((Excel) objects[1]).sort()))
				.toList();
		this.maxHeight = getRowHeight();
	}

	/**
	 * 获取字段注解信息
	 *
	 * @return 字段注解信息列表
	 */
	public List<Object[]> getFields() {
		List<Object[]> fieldList = new ArrayList<>();
		List<Field> tempFields = new ArrayList<>();

		// 添加父类和当前类的字段
		tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		for (Field field : tempFields) {
			if (shouldSkipField(field)) {
				continue;
			}

			processField(field, fieldList);
		}

		return fieldList;
	}

	/**
	 * 判断是否应该跳过字段
	 *
	 * @param field 字段
	 * @return 是否应该跳过
	 */
	private boolean shouldSkipField(Field field) {
		return ArrayUtils.contains(this.excludeFields, field.getName());
	}

	/**
	 * 处理单个字段
	 *
	 * @param field 字段
	 * @param fieldList 字段列表
	 */
	private void processField(Field field, List<Object[]> fieldList) {
		// 处理单注解
		if (field.isAnnotationPresent(Excel.class)) {
			processExcelAnnotation(field, fieldList);
		}

		// 处理多注解
		if (field.isAnnotationPresent(Excels.class)) {
			processExcelsAnnotation(field, fieldList);
		}
	}

	/**
	 * 处理Excel单注解
	 *
	 * @param field 字段
	 * @param fieldList 字段列表
	 */
	private void processExcelAnnotation(Field field, List<Object[]> fieldList) {
		Excel attr = field.getAnnotation(Excel.class);
		if (attr != null && (attr.type() == Type.ALL || attr.type() == type)) {
			setFieldAccessible(field);
			fieldList.add(new Object[]{field, attr });
		}

		// 如果是集合类型，获取子字段信息
		if (Collection.class.isAssignableFrom(field.getType())) {
			subMethod = getSubMethod(field.getName(), clazz);
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
			this.subFields = FieldUtils.getFieldsListWithAnnotation(subClass, Excel.class);
		}
	}

	/**
	 * 设置字段访问权限
	 *
	 * @param field 要设置的字段
	 */
	private void setFieldAccessible(Field field) {
		try {
			field.setAccessible(true);
		} catch (InaccessibleObjectException e) {
			log.warn("无法直接访问字段 {}: {}", field.getName(), e.getMessage());
			// 可以在这里添加备选方案，如使用getter方法
		}
	}

	/**
	 * 处理Excels多注解
	 *
	 * @param field 字段
	 * @param fieldList 字段列表
	 */
	private void processExcelsAnnotation(Field field, List<Object[]> fieldList) {
		Excels attrs = field.getAnnotation(Excels.class);
		Excel[] excels = attrs.value();

		for (Excel attr : excels) {
			if (shouldIncludeExcelField(field, attr)) {
				fieldList.add(new Object[]{field, attr });
			}
		}
	}

	/**
	 * 判断是否应该包含Excel字段
	 *
	 * @param field 字段
	 * @param attr Excel注解
	 * @return 是否应该包含
	 */
	private boolean shouldIncludeExcelField(Field field, Excel attr) {
		return !ArrayUtils.contains(this.excludeFields, field.getName() + "." + attr.targetAttr())
				&& (attr.type() == Type.ALL || attr.type() == type);
	}

	/**
	 * 根据注解获取最大行高
	 *
	 * @return 最大行高
	 */
	public short getRowHeight() {
		double maxHeightValue = 0;
		for (Object[] os : this.fields) {
			Excel excel = (Excel) os[1];
			maxHeightValue = Math.max(maxHeightValue, excel.height());
		}
		return (short) (maxHeightValue * 20);
	}

	/**
	 * 创建一个工作簿
	 */
	public void createWorkbook() {
		this.wb = new SXSSFWorkbook(500);
		this.sheet = wb.createSheet();
		wb.setSheetName(0, sheetName);
		this.styles = createStyles(wb);
	}

	/**
	 * 创建工作表
	 *
	 * @param sheetNo sheet数量
	 * @param sheetIndex 序号
	 */
	public void createSheet(int sheetNo, int sheetIndex) {
		// 设置工作表的名称.
		if (sheetNo > 1 && sheetIndex > 0) {
			this.sheet = wb.createSheet();
			this.createTitle();
			wb.setSheetName(sheetIndex, sheetName + sheetIndex);
		}
	}
	/**
	 * 获取单元格值
	 *
	 * @param targetRow 获取的行
	 * @param columnIndex 获取单元格列号
	 * @return 单元格值
	 */
	public Object getCellValue(Row targetRow, int columnIndex) {
		if (targetRow == null) {
			return targetRow;
		}

		try {
			Cell cell = targetRow.getCell(columnIndex);
			if (StringHelper.isNotNull(cell)) {
				return getCellValueByCellType(cell);
			}
		} catch (Exception e) {
			log.debug("获取单元格值异常: {}", e.getMessage());
		}

		return "";
	}

	/**
	 * 根据单元格类型获取值
	 *
	 * @param cell 单元格
	 * @return 单元格值
	 */
	private Object getCellValueByCellType(Cell cell) {
		return switch (cell.getCellType()) {
			case NUMERIC, FORMULA -> handleNumericOrFormulaCell(cell);
			case STRING -> cell.getStringCellValue();
			case BOOLEAN -> cell.getBooleanCellValue();
			case ERROR -> cell.getErrorCellValue();
			default -> "";
		};
	}

	/**
	 * 处理数值或公式单元格
	 *
	 * @param cell 单元格
	 * @return 单元格值
	 */
	private Object handleNumericOrFormulaCell(Cell cell) {
		Object val = cell.getNumericCellValue();

		if (DateUtil.isCellDateFormatted(cell)) {
			return DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
		} else {
			return formatNumericValue(val);
		}
	}

	/**
	 * 格式化数值
	 *
	 * @param val 值
	 * @return 格式化后的值
	 */
	private Object formatNumericValue(Object val) {
		if ((Double) val % 1 != 0) {
			return new BigDecimal(val.toString());
		} else {
			return new DecimalFormat("0").format(val);
		}
	}

	/**
	 * 判断是否是空行
	 *
	 * @param targetRow 判断的行
	 * @return 是否为空行
	 */
	private boolean isRowEmpty(Row targetRow) {
		if (targetRow == null) {
			return true;
		}
		for (int i = targetRow.getFirstCellNum(); i < targetRow.getLastCellNum(); i++) {
			Cell cell = targetRow.getCell(i);
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取Excel2003图片
	 *
	 * @param targetSheet 当前sheet对象
	 * @param workbook 工作簿对象
	 * @return Map key:图片单元格索引（1_1）String，value:图片流PictureData
	 */
	public static Map<String, PictureData> getSheetPictures03(HSSFSheet targetSheet, HSSFWorkbook workbook) {
		Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
		List<HSSFPictureData> pictures = workbook.getAllPictures();
		if (!pictures.isEmpty()) {
			for (HSSFShape shape : targetSheet.getDrawingPatriarch().getChildren()) {
				HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
				if (shape instanceof HSSFPicture pic) {
					int pictureIndex = pic.getPictureIndex() - 1;
					HSSFPictureData picData = pictures.get(pictureIndex);
					String picIndex = anchor.getRow1() + "_" + anchor.getCol1();
					sheetIndexPicMap.put(picIndex, picData);
				}
			}
			return sheetIndexPicMap;
		} else {
			return sheetIndexPicMap;
		}
	}

	/**
	 * 获取Excel2007图片
	 *
	 * @param targetSheet 当前sheet对象
	 * @return Map key:图片单元格索引（1_1）String，value:图片流PictureData
	 */
	public static Map<String, PictureData> getSheetPictures07(XSSFSheet targetSheet) {
		Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
		for (POIXMLDocumentPart dr : targetSheet.getRelations()) {
			if (dr instanceof XSSFDrawing drawing) {
				List<XSSFShape> shapes = drawing.getShapes();
				for (XSSFShape shape : shapes) {
					if (shape instanceof XSSFPicture pic) {
						XSSFClientAnchor anchor = pic.getPreferredSize();
						CTMarker ctMarker = anchor.getFrom();
						String picIndex = ctMarker.getRow() + "_" + ctMarker.getCol();
						sheetIndexPicMap.put(picIndex, pic.getPictureData());
					}
				}
			}
		}
		return sheetIndexPicMap;
	}

	/**
	 * 格式化不同类型的日期对象
	 *
	 * @param dateFormat 日期格式
	 * @param val 被格式化的日期对象
	 * @return 格式化后的日期字符
	 */
	public String parseDateToStr(String dateFormat, Object val) {
		if (val == null) {
			return StringUtils.EMPTY;
		} else if (val instanceof Date date) {
			return DateHelper.parseDateToStr(dateFormat, date);
		} else if (val instanceof LocalDateTime localDateTime) {
			return DateHelper.parseDateToStr(dateFormat, DateHelper.toDate(localDateTime));
		} else if (val instanceof LocalDate localDate) {
			return DateHelper.parseDateToStr(dateFormat, DateHelper.toDate(localDate));
		} else {
			return val.toString();
		}
	}

	/**
	 * 是否有对象的子列表
	 *
	 * @return 是否有子列表
	 */
	public boolean isSubList() {
		return StringHelper.isNotNull(subFields) && !subFields.isEmpty();
	}

	/**
	 * 是否有对象的子列表，集合不为空
	 *
	 * @param vo 对象实例
	 * @return 是否有子列表且不为空
	 */
	public boolean isSubListValue(T vo) {
		return StringHelper.isNotNull(subFields) && !subFields.isEmpty() && StringHelper.isNotNull(getListCellValue(vo))
				&& !getListCellValue(vo).isEmpty();
	}

	/**
	 * 获取集合的值
	 *
	 * @param obj 对象实例
	 * @return 集合值
	 */
	public Collection<?> getListCellValue(Object obj) {
		Object value;
		try {
			value = subMethod.invoke(obj);
		} catch (Exception e) {
			return new ArrayList<>();
		}
		return (Collection<?>) value;
	}

	/**
	 * 获取对象的子列表方法
	 *
	 * @param fieldName 字段名称
	 * @param pojoClass 类对象
	 * @return 子列表方法
	 */
	public Method getSubMethod(String fieldName, Class<?> pojoClass) {
		StringBuilder getMethodName = new StringBuilder("get");
		getMethodName.append(fieldName.substring(0, 1).toUpperCase());
		getMethodName.append(fieldName.substring(1));
		Method method = null;
		try {
			method = pojoClass.getMethod(getMethodName.toString());
		} catch (Exception e) {
			log.error("获取对象异常{}", e.getMessage());
		}
		return method;
	}
}
