package com.groqdata.common.exception.file;

/**
 * 文件操作相关的自定义异常类
 */
public class FileDownloadException extends RuntimeException {

	public FileDownloadException(String message) {
		super(message);
	}

	public FileDownloadException(String message, Throwable cause) {
		super(message, cause);
	}
}
