package com.groqdata.common.exception.file;

/**
 * 文件名长度超出限制异常
 *
 * @author MISP TEAM
 */
public class FileNameLengthLimitExceededException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	private final int fileNameLength;
	private final int maxLength;

	public FileNameLengthLimitExceededException(String message, int fileNameLength, int maxLength) {
		super(message);
		this.fileNameLength = fileNameLength;
		this.maxLength = maxLength;
	}

	public FileNameLengthLimitExceededException(String message, int fileNameLength, int maxLength, Throwable cause) {
		super(message, cause);
		this.fileNameLength = fileNameLength;
		this.maxLength = maxLength;
	}

	public int getFileNameLength() {
		return fileNameLength;
	}

	public int getMaxLength() {
		return maxLength;
	}
}
