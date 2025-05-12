package com.groqdata.framework.config.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.groqdata.common.utils.StringUtils;

public class CustomP6SpyLogger implements MessageFormattingStrategy {
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";
    /**
     * Sql日志格式化
     *
     * @param connectionId: 连接ID
     * @param now:          当前时间
     * @param elapsed:      花费时间
     * @param category:     类别
     * @param prepared:     预编译SQL
     * @param sql:          最终执行的SQL
     * @param url:          数据库连接地址
     * @return 格式化日志结果
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (StringUtils.isNotBlank(sql)) {
            return String.format("%s执行时间：%s    耗时：%d ms    完整SQL：\n %s\n%s", PURPLE, now,  elapsed, sql.replaceAll("[\\s]+", " "), RESET);
        }
        return "";
    }
}