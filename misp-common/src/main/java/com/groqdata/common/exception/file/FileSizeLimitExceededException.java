package com.groqdata.common.exception.file;

/**
 * 文件大小超出限制异常
 *
 * @author MISP TEAM
 */
public class FileSizeLimitExceededException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	private final long fileSize;
	private final long maxSize;

	public FileSizeLimitExceededException(String message, long fileSize, long maxSize) {
		super(message);
		this.fileSize = fileSize;
		this.maxSize = maxSize;
	}

	public FileSizeLimitExceededException(String message, long fileSize, long maxSize, Throwable cause) {
		super(message, cause);
		this.fileSize = fileSize;
		this.maxSize = maxSize;
	}

	public long getFileSize() {
		return fileSize;
	}

	public long getMaxSize() {
		return maxSize;
	}
}
