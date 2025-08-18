package com.groqdata.generator.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import com.groqdata.generator.constants.GenConstants;
import com.groqdata.common.utils.StringHelper;
import com.groqdata.generator.config.GenConfig;
import com.groqdata.generator.domain.GenTable;
import com.groqdata.generator.domain.GenTableColumn;

/**
 * 代码生成器 工具类
 *
 * @author MISP TEAM
 */
public class GenUtils {
    private GenUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, String operName) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName(GenConfig.getPackageName());
        genTable.setModuleName(getModuleName(GenConfig.getPackageName()));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(replaceText(genTable.getTableComment()));
        genTable.setFunctionAuthor(GenConfig.getAuthor());
        genTable.setCreateBy(operName);
    }

    /**
     * 初始化列属性字段
     *
     * @param column 数据表列信息
     * @param table 数据表信息
     */
    public static void initColumnField(GenTableColumn column, GenTable table) {
        // 获取数据库字段类型
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();

        // 设置基础属性
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        column.setJavaField(StringHelper.toCamelCase(columnName)); // 设置java字段名
        column.setJavaType(GenConstants.TYPE_STRING); // 设置默认Java类型为String
        column.setQueryType(GenConstants.QUERY_EQ); // 设置默认查询方式为相等

        // 根据数据库字段类型设置Java类型和HTML控件类型
        handleColumnDataType(column, dataType);

        // 设置字段操作标识（插入、编辑、列表、查询）
        setColumnOperationFlags(column, columnName);

        // 设置查询方式和特殊HTML控件类型
        setQueryAndSpecialHtmlTypes(column, columnName);
    }

    /**
     * 根据数据库字段类型设置Java类型和HTML控件类型
     *
     * @param column 数据表列信息
     * @param dataType 数据库字段类型
     */
    private static void handleColumnDataType(GenTableColumn column, String dataType) {
        if (GenConstants.COLUMN_TYPE_STR.contains(dataType)
                || GenConstants.COLUMN_TYPE_TEXT.contains(dataType)) {
            // 处理字符串类型字段
            handleStringType(column);
        } else if (GenConstants.COLUMN_TYPE_TIME.contains(dataType)) {
            // 处理时间类型字段
            column.setJavaType(GenConstants.TYPE_DATE);
            column.setHtmlType(GenConstants.HTML_DATETIME);
        } else if (GenConstants.COLUMN_TYPE_TIMESTAMP.contains(dataType)) {
            // 处理时间戳类型字段
            column.setJavaType(GenConstants.TYPE_LOCALDATETIME);
            column.setHtmlType(GenConstants.HTML_DATETIME);
        } else if (GenConstants.COLUMN_TYPE_NUMBER.contains(dataType)) {
            // 处理数字类型字段
            handleNumberType(column);
        }
    }

    /**
     * 处理字符串类型字段
     *
     * @param column 数据表列信息
     */
    private static void handleStringType(GenTableColumn column) {
        Integer columnLength = getColumnLength(column.getColumnType());
        // 字符串长度超过500设置为文本域
        String htmlType = columnLength >= 500 || GenConstants.COLUMN_TYPE_TEXT.contains(getDbType(column.getColumnType()))
                ? GenConstants.HTML_TEXTAREA
                : GenConstants.HTML_INPUT;
        column.setHtmlType(htmlType);
    }

    /**
     * 处理数字类型字段
     *
     * @param column 数据表列信息
     */
    private static void handleNumberType(GenTableColumn column) {
        column.setHtmlType(GenConstants.HTML_INPUT);

        // 解析字段精度信息
        String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), ",");
        if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
            // 如果是浮点型，统一用BigDecimal
            column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
        }
        // 如果是整形
        else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
            column.setJavaType(GenConstants.TYPE_INTEGER);
        }
        // 长整形
        else {
            column.setJavaType(GenConstants.TYPE_LONG);
        }
    }

    /**
     * 设置字段操作标识（插入、编辑、列表、查询）
     *
     * @param column 数据表列信息
     * @param columnName 列名
     */
    private static void setColumnOperationFlags(GenTableColumn column, String columnName) {
        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert(GenConstants.REQUIRE);

        // 编辑字段（排除不需要编辑的字段和主键）
        if (!GenConstants.COLUMN_NAME_NOT_EDIT.contains(columnName) && !column.isPk()) {
            column.setIsEdit(GenConstants.REQUIRE);
        }
        // 列表字段（排除不需要显示的字段和主键）
        if (!GenConstants.COLUMN_NAME_NOT_LIST.contains(columnName) && !column.isPk()) {
            column.setIsList(GenConstants.REQUIRE);
        }
        // 查询字段（排除不需要查询的字段和主键）
        if (!GenConstants.COLUMN_NAME_NOT_QUERY.contains(columnName) && !column.isPk()) {
            column.setIsQuery(GenConstants.REQUIRE);
        }
    }

    /**
     * 设置查询方式
     *
     * @param column 数据表列信息
     * @param columnName 列名
     */
    private static void setQueryType(GenTableColumn column, String columnName) {
        if (StringUtils.endsWithIgnoreCase(columnName, "name")) {
            column.setQueryType(GenConstants.QUERY_LIKE);
        }
    }

    /**
     * 设置特殊HTML控件类型
     *
     * @param column 数据表列信息
     * @param columnName 列名
     */
    private static void setSpecialHtmlTypes(GenTableColumn column, String columnName) {
        if (StringUtils.endsWithIgnoreCase(columnName, "status")) {
            // 状态字段设置单选框
            column.setHtmlType(GenConstants.HTML_RADIO);
        } else if (StringUtils.endsWithIgnoreCase(columnName, "type")
                || StringUtils.endsWithIgnoreCase(columnName, "sex")) {
            // 类型&性别字段设置下拉框
            column.setHtmlType(GenConstants.HTML_SELECT);
        } else if (StringUtils.endsWithIgnoreCase(columnName, "image")) {
            // 图片字段设置图片上传控件
            column.setHtmlType(GenConstants.HTML_IMAGE_UPLOAD);
        } else if (StringUtils.endsWithIgnoreCase(columnName, "file")) {
            // 文件字段设置文件上传控件
            column.setHtmlType(GenConstants.HTML_FILE_UPLOAD);
        } else if (StringUtils.endsWithIgnoreCase(columnName, "content")) {
            // 内容字段设置富文本控件
            column.setHtmlType(GenConstants.HTML_EDITOR);
        }
    }

    /**
     * 统一设置查询方式和特殊HTML控件类型
     *
     * @param column 数据表列信息
     * @param columnName 列名
     */
    private static void setQueryAndSpecialHtmlTypes(GenTableColumn column, String columnName) {
        setQueryType(column, columnName);
        setSpecialHtmlTypes(column, columnName);
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        return StringUtils.substring(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName) {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return StringUtils.substring(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName) {
        boolean autoRemovePre = GenConfig.getAutoRemovePre();
        String tablePrefix = GenConfig.getTablePrefix();
        if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix)) {
            String[] searchList = StringUtils.split(tablePrefix, ",");
            tableName = replaceFirst(tableName, searchList);
        }
        return StringHelper.convertToCamelCase(tableName);
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList 替换列表
     * @return 替换后的字符串
     */
    public static String replaceFirst(String replacementm, String[] searchList) {
        String text = replacementm;
        for (String searchString : searchList) {
            if (replacementm.startsWith(searchString)) {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

    /**
     * 关键字替换
     *
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    public static String replaceText(String text) {
        return RegExUtils.replaceAll(text, "(?:表|若依)", "");
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StringUtils.indexOf(columnType, "(") > 0) {
            return StringUtils.substringBefore(columnType, "(");
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        if (StringUtils.indexOf(columnType, "(") > 0) {
            String length = StringUtils.substringBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }
}
