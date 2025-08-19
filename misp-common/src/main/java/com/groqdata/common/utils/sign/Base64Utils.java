package com.groqdata.common.utils.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64工具类，提供Base64编码和解码功能
 * 支持字符串、字节数组和文件的Base64处理
 * 基于Java标准库java.util.Base64实现，线程安全
 *
 * @author MISP TEAM
 */
public final class Base64Utils {

	private static final Logger logger = LoggerFactory.getLogger(Base64Utils.class);

	/**
	 * Base64编码器实例（URL安全模式，不添加填充）
	 */
	private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

	/**
	 * Base64编码器实例（标准模式）
	 */
	private static final Base64.Encoder STANDARD_ENCODER = Base64.getEncoder();

	/**
	 * Base64解码器实例（URL安全模式）
	 */
	private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

	/**
	 * Base64解码器实例（标准模式）
	 */
	private static final Base64.Decoder STANDARD_DECODER = Base64.getDecoder();

	/**
	 * 私有构造函数，防止实例化工具类
	 */
	private Base64Utils() {
		throw new UnsupportedOperationException("工具类不允许实例化");
	}

	/**
	 * 对字符串进行Base64编码（标准模式）
	 *
	 * @param content 待编码的字符串（使用UTF-8编码）
	 * @return 编码后的Base64字符串，若输入为null则返回null
	 */
	public static String encodeToString(String content) {
		if (content == null) {
			return null;
		}
		return STANDARD_ENCODER.encodeToString(content.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 对字节数组进行Base64编码（标准模式）
	 *
	 * @param bytes 待编码的字节数组
	 * @return 编码后的Base64字符串，若输入为null则返回null
	 */
	public static String encodeToString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return STANDARD_ENCODER.encodeToString(bytes);
	}

	/**
	 * 对字符串进行Base64编码（URL安全模式）
	 *
	 * @param content 待编码的字符串（使用UTF-8编码）
	 * @return 编码后的URL安全Base64字符串，若输入为null则返回null
	 */
	public static String encodeToUrlSafeString(String content) {
		if (content == null) {
			return null;
		}
		return URL_ENCODER.encodeToString(content.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 对Base64字符串进行解码（标准模式）
	 *
	 * @param base64Str 待解码的Base64字符串
	 * @return 解码后的字节数组，若输入为null则返回null
	 * @throws IllegalArgumentException 当输入不是有效的Base64字符串时抛出
	 */
	public static byte[] decode(String base64Str) {
		if (base64Str == null) {
			return new byte[0];
		}
		try {
			return STANDARD_DECODER.decode(base64Str);
		} catch (IllegalArgumentException e) {
			logger.error("Base64解码失败，输入字符串: {}", base64Str, e);
			throw new IllegalArgumentException("无效的Base64字符串: " + base64Str, e);
		}
	}

	/**
	 * 对Base64字符串进行解码为字符串（标准模式）
	 *
	 * @param base64Str 待解码的Base64字符串
	 * @return 解码后的字符串（使用UTF-8编码），若输入为null则返回null
	 * @throws IllegalArgumentException 当输入不是有效的Base64字符串时抛出
	 */
	public static String decodeToString(String base64Str) {
		byte[] bytes = decode(base64Str);
		return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
	}

	/**
	 * 对Base64字符串进行解码（URL安全模式）
	 *
	 * @param base64Str 待解码的URL安全Base64字符串
	 * @return 解码后的字节数组，若输入为null则返回null
	 * @throws IllegalArgumentException 当输入不是有效的Base64字符串时抛出
	 */
	public static byte[] decodeUrlSafe(String base64Str) {
		if (base64Str == null) {
			return new byte[0];
		}
		try {
			return URL_DECODER.decode(base64Str);
		} catch (IllegalArgumentException e) {
			logger.error("URL安全Base64解码失败，输入字符串: {}", base64Str, e);
			throw new IllegalArgumentException("无效的URL安全Base64字符串: " + base64Str, e);
		}
	}

	/**
	 * 对文件进行Base64编码
	 *
	 * @param file 待编码的文件
	 * @return 编码后的Base64字符串
	 * @throws IOException 当文件读取失败时抛出
	 */
	public static String encodeFile(File file) throws IOException {
		validateFile(file);
		try (InputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			return encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			logger.error("文件Base64编码失败，文件路径: {}", file.getAbsolutePath(), e);
			throw new IOException("文件编码失败: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * 将Base64字符串解码为文件
	 *
	 * @param base64Str 待解码的Base64字符串
	 * @param outputFile 输出文件
	 * @throws IOException 当文件写入失败时抛出
	 * @throws IllegalArgumentException 当输入不是有效的Base64字符串时抛出
	 */
	public static void decodeToFile(String base64Str, File outputFile) throws IOException {
		if (base64Str == null || base64Str.isEmpty()) {
			throw new IllegalArgumentException("Base64字符串不能为空");
		}
		if (outputFile == null) {
			throw new IllegalArgumentException("输出文件不能为null");
		}

		// 确保父目录存在
		File parentDir = outputFile.getParentFile();
		if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
			throw new IOException("无法创建父目录: " + parentDir.getAbsolutePath());
		}

		byte[] data = decode(base64Str);
		try (OutputStream outputStream = new FileOutputStream(outputFile)) {
			outputStream.write(data);
		} catch (IOException e) {
			logger.error("Base64解码到文件失败，文件路径: {}", outputFile.getAbsolutePath(), e);
			throw new IOException("文件写入失败: " + outputFile.getAbsolutePath(), e);
		}
	}

	/**
	 * 验证文件是否有效
	 *
	 * @param file 待验证的文件
	 * @throws IOException 当文件无效时抛出
	 */
	private static void validateFile(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("文件不能为null");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在: " + file.getAbsolutePath());
		}
		if (!file.isFile()) {
			throw new IOException("不是一个文件: " + file.getAbsolutePath());
		}
		if (!file.canRead()) {
			throw new IOException("文件不可读: " + file.getAbsolutePath());
		}
	}
}
