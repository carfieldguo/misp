package com.groqdata.common.utils;

/**
 * 处理并记录日志文件
 * 
 * @author MISP TEAM
 */
public class LogUtils {
    private LogUtils() {
        throw new IllegalStateException("工具类不可实例化");
    }
	public static String getBlock(Object msg) {
		if (msg == null) {
			msg = "";
		}
		return "[" + msg.toString() + "]";
	}
}
