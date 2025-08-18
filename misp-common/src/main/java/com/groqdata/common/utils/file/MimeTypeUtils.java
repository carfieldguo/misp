package com.groqdata.common.utils.file;

import java.util.List;

/**
 * 媒体类型工具类
 * 
 * @author MISP TEAM
 */
public class MimeTypeUtils {
	private MimeTypeUtils() {
		// 防止实例化
		throw new IllegalStateException("工具类直接使用不需要实例化");
	}
	public static final String IMAGE_PNG = "image/png";

	public static final String IMAGE_JPG = "image/jpg";

	public static final String IMAGE_JPEG = "image/jpeg";

	public static final String IMAGE_BMP = "image/bmp";

	public static final String IMAGE_GIF = "image/gif";

	public static final List<String> IMAGE_EXTENSION = List.of("bmp", "gif", "jpg", "jpeg", "png");

	public static final List<String> FLASH_EXTENSION = List.of("swf", "flv");

	public static final List<String> MEDIA_EXTENSION = List.of("swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi",
			"mpg",
			"asf", "rm", "rmvb");

	public static final List<String> VIDEO_EXTENSION = List.of("mp4", "avi", "rmvb");

	public static final List<String> DEFAULT_ALLOWED_EXTENSION = List.of(
			// 图片
			"bmp", "gif", "jpg", "jpeg", "png",
			// word excel powerpoint
			"doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
			// 压缩文件
			"rar", "zip", "gz", "bz2",
			// 视频格式
			"mp4", "avi", "rmvb",
			// pdf
			"pdf");

	public static String getExtension(String prefix) {
		return switch (prefix) {
			case IMAGE_PNG -> "png";
			case IMAGE_JPG -> "jpg";
			case IMAGE_JPEG -> "jpeg";
			case IMAGE_BMP -> "bmp";
			case IMAGE_GIF -> "gif";
			default -> "";
		};
	}
}
