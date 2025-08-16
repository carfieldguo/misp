package com.groqdata.common.exception.file;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * 无效文件扩展名异常
 * 当上传的文件扩展名不被允许或被明确拒绝时抛出此异常
 *
 * @author MISP TEAM
 */
public class InvalidExtensionException extends FileUploadException {
	private static final long serialVersionUID = 1L;

	private final String fileName;
	private final String extension;
	private final Set<String> allowedExtensions;
	private final Set<String> deniedExtensions;

	/**
	 * 构造函数 - 兼容原有用法（仅消息）
	 *
	 * @param message 错误消息
	 */
	public InvalidExtensionException(String message) {
		super(message);
		this.fileName = null;
		this.extension = null;
		this.allowedExtensions = Collections.emptySet();
		this.deniedExtensions = Collections.emptySet();
	}

	/**
	 * 构造函数 - 兼容原有用法（消息和原因）
	 *
	 * @param message 错误消息
	 * @param cause 原因异常
	 */
	public InvalidExtensionException(String message, Throwable cause) {
		super(message, cause);
		this.fileName = null;
		this.extension = null;
		this.allowedExtensions = Collections.emptySet();
		this.deniedExtensions = Collections.emptySet();
	}

	/**
	 * 构造函数 - 用于不允许的扩展名
	 *
	 * @param message 错误消息
	 * @param fileName 文件名
	 * @param extension 文件扩展名
	 * @param allowedExtensions 允许的扩展名列表
	 */
	public InvalidExtensionException(String message, String fileName, String extension, Set<String> allowedExtensions) {
		super(message);
		this.fileName = fileName;
		this.extension = extension;
		this.allowedExtensions = allowedExtensions != null ? new HashSet<>(allowedExtensions) : Collections.emptySet();
		this.deniedExtensions = Collections.emptySet();
	}

	/**
	 * 构造函数 - 用于被拒绝的扩展名
	 *
	 * @param message 错误消息
	 * @param fileName 文件名
	 * @param extension 文件扩展名
	 * @param deniedExtensions 被拒绝的扩展名列表
	 */
	public InvalidExtensionException(String message, String fileName, String extension, Set<String> deniedExtensions,
			boolean isDenied) {
		super(message);
		this.fileName = fileName;
		this.extension = extension;
		this.deniedExtensions = deniedExtensions != null ? new HashSet<>(deniedExtensions) : Collections.emptySet();
		this.allowedExtensions = Collections.emptySet();
	}

	/**
	 * 构造函数 - 通用构造函数（消息和扩展名）
	 *
	 * @param message 错误消息
	 * @param extension 文件扩展名
	 */
	public InvalidExtensionException(String message, String extension) {
		super(message);
		this.fileName = null;
		this.extension = extension;
		this.allowedExtensions = Collections.emptySet();
		this.deniedExtensions = Collections.emptySet();
	}

	/**
	 * 构造函数 - 带原因的构造函数（消息、扩展名和原因）
	 *
	 * @param message 错误消息
	 * @param extension 文件扩展名
	 * @param cause 原因异常
	 */
	public InvalidExtensionException(String message, String extension, Throwable cause) {
		super(message, cause);
		this.fileName = null;
		this.extension = extension;
		this.allowedExtensions = Collections.emptySet();
		this.deniedExtensions = Collections.emptySet();
	}

	/**
	 * 获取文件名
	 *
	 * @return 文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 获取文件扩展名
	 *
	 * @return 文件扩展名
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * 获取允许的扩展名列表（不可修改）
	 *
	 * @return 允许的扩展名列表
	 */
	public Set<String> getAllowedExtensions() {
		return Collections.unmodifiableSet(allowedExtensions);
	}

	/**
	 * 获取被拒绝的扩展名列表（不可修改）
	 *
	 * @return 被拒绝的扩展名列表
	 */
	public Set<String> getDeniedExtensions() {
		return Collections.unmodifiableSet(deniedExtensions);
	}

	/**
	 * 判断是否因为扩展名被拒绝列表而抛出异常
	 *
	 * @return 如果是因为被拒绝列表则返回true，否则返回false
	 */
	public boolean isDeniedExtension() {
		return !deniedExtensions.isEmpty();
	}

	/**
	 * 判断是否因为扩展名不在允许列表而抛出异常
	 *
	 * @return 如果是因为不在允许列表则返回true，否则返回false
	 */
	public boolean isNotAllowedExtension() {
		return !allowedExtensions.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(": ");
		sb.append(getMessage());

		if (extension != null) {
			sb.append(" (扩展名: ").append(extension).append(")");
		}

		if (fileName != null) {
			sb.append(" (文件名: ").append(fileName).append(")");
		}

		if (!allowedExtensions.isEmpty()) {
			sb.append(" (允许的扩展名: ").append(allowedExtensions).append(")");
		}

		if (!deniedExtensions.isEmpty()) {
			sb.append(" (拒绝的扩展名: ").append(deniedExtensions).append(")");
		}

		return sb.toString();
	}
}
