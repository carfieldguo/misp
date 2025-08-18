package com.groqdata.common.utils.poi;

import java.util.List;

/**
 * Excel相关常量定义
 *
 * @author MISP TEAM
 */
public class ExcelConstants {

	/**
	 * 公式相关常量
	 */
	public static final String FORMULA_REGEX_STR = "=|-|\\+|@";
	public static final List<String> FORMULA_STR = List.of("=", "-", "+", "@");

	/**
	 * 样式键名常量
	 */
	public static final String STYLE_TITLE = "title";
	public static final String STYLE_DATA = "data";
	public static final String STYLE_TOTAL = "total";

	/**
	 * 格式化常量
	 */
	public static final String NUMBER_FORMAT = "######0.00";
	public static final String DEFAULT_SHEET_NAME = "Sheet";

	/**
	 * 图片类型常量
	 */
	public static final String IMAGE_TYPE_JPG = "JPG";
	public static final String IMAGE_TYPE_PNG = "PNG";

	/**
	 * 隐藏表前缀
	 */
	public static final String HIDDEN_SHEET_PREFIX = "combo_";

	/**
	 * 合计行文本
	 */
	public static final String TOTAL_TEXT = "合计";

	/**
	 * 字典相关常量
	 */
	public static final String DICT_COMBO_PREFIX = "combo_";
	public static final String DATA_ID_FORMAT = "data_{}_{}_{}_{}";
	public static final String HEADER_ID_FORMAT = "header_{}_{}";
	public static final String OFFICE_SPREADSHEETML_SHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String DEFAULT_FONT_NAME = "Arial";
	public static final String STYLE_TYPE_TOTAL = "total";
	public static final String STYLE_TYPE_DATA = "data";
	public static final String STYLE_TYPE_TITLE = "title";

	private ExcelConstants() {
		// 私有构造函数防止实例化
	}
}
