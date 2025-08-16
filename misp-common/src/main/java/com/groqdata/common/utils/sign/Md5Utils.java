package com.groqdata.common.utils.sign;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Md5加密方法
 *
 * @author MISP TEAM
 */
public class Md5Utils {
	private static final Logger log = LoggerFactory.getLogger(Md5Utils.class);

	// 隐藏构造函数，防止实例化
	private Md5Utils() {
		throw new UnsupportedOperationException("工具类不允许实例化");
	}

	/**
	 * MD5加密
	 * @param text 待加密文本
	 * @return 加密后的小写十六进制字符串
	 */
	public static String md5(String text) {
		if (text == null) {
			return null;
		}
		return DigestUtils.md5Hex(text);
	}

	/**
	 * MD5加密并返回大写结果
	 * @param text 待加密文本
	 * @return 加密后的大写十六进制字符串
	 */
	public static String md5UpperCase(String text) {
		if (text == null) {
			return null;
		}
		return DigestUtils.md5Hex(text).toUpperCase();
	}

	/**
	 * MD5加密字节数组
	 * @param bytes 待加密字节数组
	 * @return 加密后的小写十六进制字符串
	 */
	public static String md5(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return DigestUtils.md5Hex(bytes);
	}
}
