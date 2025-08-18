package com.groqdata.generator.constants;

import java.util.List;

/**
 * 代码生成通用常量
 *
 * @author MISP TEAM
 */
public class GenConstants {
	/**
	 * 私有构造函数，防止实例化
	 */
	private GenConstants() {
		throw new IllegalStateException("常量类直接使用不需要实例化");
	}

	/** 单表（增删改查） */
	public static final String TPL_CRUD = "crud";

	/** 树表（增删改查） */
	public static final String TPL_TREE = "tree";

	/** 主子表（增删改查） */
	public static final String TPL_SUB = "sub";

	/** 树编码字段 */
	public static final String TREE_CODE = "treeCode";

	/** 树父编码字段 */
	public static final String TREE_PARENT_CODE = "treeParentCode";

	/** 树名称字段 */
	public static final String TREE_NAME = "treeName";

	/** 上级菜单ID字段 */
	public static final String PARENT_MENU_ID = "parentMenuId";

	/** 上级菜单名称字段 */
	public static final String PARENT_MENU_NAME = "parentMenuName";

	// 提取重复字段为常量
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_CREATE_BY = "create_by";
	private static final String COLUMN_CREATE_TIME = "create_time";
	private static final String COLUMN_DEL_FLAG = "del_flag";
	private static final String COLUMN_UPDATE_BY = "update_by";
	private static final String COLUMN_UPDATE_TIME = "update_time";
	private static final String COLUMN_REMARK = "remark";
	private static final String COLUMN_TYPE_DATETIME = "datetime";

	/** 数据库字符串类型 */
	public static final List<String> COLUMN_TYPE_STR = List.of("char", "varchar", "nvarchar", "varchar2");

	/** 数据库文本类型 */
	public static final List<String> COLUMN_TYPE_TEXT = List.of("tinytext", "text", "mediumtext", "longtext");

	/** 数据库时间类型 */
	public static final List<String> COLUMN_TYPE_TIME = List.of(COLUMN_TYPE_DATETIME, "time", "date", "timestamp");

	/** 数据库时间戳类型 */
	public static final List<String> COLUMN_TYPE_TIMESTAMP = List.of("timestamp without time zone",
			"timestamp with time zone");

	/** 数据库数字类型 */
	public static final List<String> COLUMN_TYPE_NUMBER = List.of("tinyint", "smallint", "mediumint", "int", "number",
			"integer", "bit", "bigint", "float", "double", "decimal");

	/** 页面不需要编辑字段 */
	public static final List<String> COLUMN_NAME_NOT_EDIT = List.of(COLUMN_ID, COLUMN_CREATE_BY, COLUMN_CREATE_TIME,
			COLUMN_DEL_FLAG);

	/** 页面不需要显示的列表字段 */
	public static final List<String> COLUMN_NAME_NOT_LIST = List.of(COLUMN_ID, COLUMN_CREATE_BY, COLUMN_CREATE_TIME,
			COLUMN_DEL_FLAG, COLUMN_UPDATE_BY, COLUMN_UPDATE_TIME);

	/** 页面不需要查询字段 */
	public static final List<String> COLUMN_NAME_NOT_QUERY = List.of(COLUMN_ID, COLUMN_CREATE_BY, COLUMN_CREATE_TIME,
			COLUMN_DEL_FLAG, COLUMN_UPDATE_BY, COLUMN_UPDATE_TIME, COLUMN_REMARK);

	/** Entity基类字段 */
	public static final List<String> BASE_ENTITY = List.of("createBy", "createTime", "updateBy", "updateTime",
			COLUMN_REMARK);

	/** Tree基类字段 */
	public static final List<String> TREE_ENTITY = List.of("parentName", "parentId", "orderNum", "ancestors",
			"children");

	/** 文本框 */
	public static final String HTML_INPUT = "input";

	/** 文本域 */
	public static final String HTML_TEXTAREA = "textarea";

	/** 下拉框 */
	public static final String HTML_SELECT = "select";

	/** 单选框 */
	public static final String HTML_RADIO = "radio";

	/** 复选框 */
	public static final String HTML_CHECKBOX = "checkbox";

	/** 日期控件 */
	public static final String HTML_DATETIME = "datetime";

	/** 图片上传控件 */
	public static final String HTML_IMAGE_UPLOAD = "imageUpload";

	/** 文件上传控件 */
	public static final String HTML_FILE_UPLOAD = "fileUpload";

	/** 富文本控件 */
	public static final String HTML_EDITOR = "editor";

	/** 字符串类型 */
	public static final String TYPE_STRING = "String";

	/** 整型 */
	public static final String TYPE_INTEGER = "Integer";

	/** 长整型 */
	public static final String TYPE_LONG = "Long";

	/** 浮点型 */
	public static final String TYPE_DOUBLE = "Double";

	/** 高精度计算类型 */
	public static final String TYPE_BIGDECIMAL = "BigDecimal";

	/** 时间类型 */
	public static final String TYPE_DATE = "Date";

	/** 本地时间类型*/
	public static final String TYPE_LOCALDATETIME = "LocalDateTime";

	/** 模糊查询 */
	public static final String QUERY_LIKE = "LIKE";

	/** 相等查询 */
	public static final String QUERY_EQ = "EQ";

	/** 需要 */
	public static final String REQUIRE = "1";
}
