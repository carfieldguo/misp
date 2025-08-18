package com.groqdata.common.utils.file;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.groqdata.common.config.MispConfig;
import com.groqdata.common.constant.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * 图片处理工具类
 *
 * @author MISP TEAM
 */
public class ImageUtils {
	private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

	private ImageUtils() {
		// 防止实例化
		throw new IllegalStateException("工具类直接使用不需要实例化");
	}

	public static byte[] getImage(String imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			log.warn("图片路径为空");
			return new byte[0];
		}

		InputStream is = getFile(imagePath);
		if (is == null) {
			log.error("无法获取图片文件流，路径：{}", imagePath);
			return new byte[0];
		}
		try {
			return IOUtils.toByteArray(is);
		} catch (Exception e) {
			log.error("图片加载异常 {}", e.getMessage(), e);
			return new byte[0];
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static InputStream getFile(String imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			log.warn("图片路径为空");
			return null;
		}

		try {
			byte[] result = readFile(imagePath);
			if (result == null || result.length == 0) {
				return null;
			}
			result = Arrays.copyOf(result, result.length);
			return new ByteArrayInputStream(result);
		} catch (Exception e) {
			log.error("获取图片异常 {}", e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 读取文件为字节数据
	 *
	 * @param url 地址
	 * @return 字节数据
	 */
	public static byte[] readFile(String url) {
		if (url == null || url.isEmpty()) {
			log.warn("文件路径为空");
			return new byte[0];
		}

		try {
			if (url.startsWith("http")) {
				// 网络地址
				URL urlObj = URI.create(url).toURL();
				URLConnection urlConnection = urlObj.openConnection();
				urlConnection.setConnectTimeout(30 * 1000);
				urlConnection.setReadTimeout(60 * 1000);
				urlConnection.setDoInput(true);
				try (InputStream in = urlConnection.getInputStream()) {
					return IOUtils.toByteArray(in);
				}
			} else {
				// 本机地址
				String localPath = MispConfig.getProfile();
				if (localPath == null || localPath.isEmpty()) {
					log.error("本地路径配置为空");
					return new byte[0];
				}
				String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX);
				try (InputStream in = new FileInputStream(downloadPath)) {
					return IOUtils.toByteArray(in);
				}
			}
		} catch (Exception e) {
			log.error("获取文件路径异常 {}", e.getMessage(), e);
			return new byte[0];
		}
	}

}
