package com.groqdata.common.exception.file;

/**
 * Excel工作表不存在异常
 */
public class ExcelException extends RuntimeException {

	public ExcelException(String message) {
		super(message);
	}

	public ExcelException(String message, Throwable cause) {
		super(message, cause);
	}
}