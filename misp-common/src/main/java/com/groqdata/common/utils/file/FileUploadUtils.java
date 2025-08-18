
package com.groqdata.common.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.groqdata.common.config.MispConfig;
import com.groqdata.common.exception.file.FileUploadException;
import com.groqdata.common.exception.file.FileNameLengthLimitExceededException;
import com.groqdata.common.exception.file.InvalidExtensionException;
import com.groqdata.common.exception.file.FileSizeLimitExceededException;
import com.groqdata.common.utils.DateHelper;

/**
 * 文件上传工具类
 *
 * @author MISP TEAM
 */
public class FileUploadUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUploadUtils.class);

	private FileUploadUtils() {
		// 防止实例化
		throw new IllegalStateException("工具类直接使用不需要实例化");
	}
	/**
	 * 默认大小 50M
	 */
	public static final long DEFAULT_MAX_SIZE = 50L * 1024 * 1024;
	/**
	 * 默认的文件名最大长度 100
	 */
	public static final int DEFAULT_FILE_NAME_LENGTH = 100;

	/**
	 * 默认上传的地址
	 */
	private static String defaultBaseDir = MispConfig.getProfile();

	/**
	 * 默认允许的扩展名
	 */
	private static final String[] DEFAULT_ALLOWED_EXTENSION = {
		// 图片格式
		"bmp", "gif", "jpg", "jpeg", "png",
		// 文档格式
		"doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
		// 压缩文件
		"rar", "zip", "gz", "bz2",
		// PDF
		"pdf"
	};

	/**
	 * 默认拒绝的扩展名
	 */
	private static final String[] DEFAULT_DENIED_EXTENSION = {
		"jsp", "jspx", "java", "class", "sh", "bat", "exe", "js", "html", "htm"
	};

	/**
	 * 以默认配置进行文件上传
	 *
	 * @param file 上传的文件
	 * @return 文件名称
	 * @throws FileUploadException 文件上传异常
	 */
	public static final String upload(MultipartFile file) throws FileUploadException {
		try {
			return upload(defaultBaseDir, file, DEFAULT_ALLOWED_EXTENSION);
		} catch (Exception e) {
			throw new FileUploadException("文件上传失败: " + e.getMessage(), e);
		}
	}

	/**
	 * 根据文件路径上传文件
	 *
	 * @param baseDir 相对应用的基目录
	 * @param file    上传的文件
	 * @return 文件名称
	 * @throws FileUploadException 文件上传异常
	 */
	public static final String upload(String baseDir, MultipartFile file) throws FileUploadException {
		try {
			return upload(baseDir, file, DEFAULT_ALLOWED_EXTENSION);
		} catch (Exception e) {
			throw new FileUploadException("文件上传失败: " + e.getMessage(), e);
		}
	}

	/**
	 * 文件上传
	 *
	 * @param baseDir          相对应用的基目录
	 * @param file             上传的文件
	 * @param allowedExtension 允许的文件类型
	 * @return 返回上传成功的文件名
	 * @throws FileUploadException 文件上传异常
	 */
	public static String upload(String baseDir, MultipartFile file, String[] allowedExtension)
			throws FileUploadException {
		// 参数校验
		validateUploadParameters(baseDir, file);

		try {
			// 文件名校验
			assertAllowed(file, allowedExtension);

			// 文件大小校验
			assertFileSize(file, DEFAULT_MAX_SIZE);

			// 文件名长度校验
			assertFileNameLength(file.getOriginalFilename());

			// 获取文件扩展名
			String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

			// 生成文件名
			String fileName = generateFileName(extension);

			// 构建文件路径
			String filePath = getPathFileName(baseDir, fileName);

			// 创建目录
			File desc = getAbsoluteFile(baseDir, fileName);

			// 保存文件
			file.transferTo(desc);

			log.info("文件上传成功: {}", filePath);
			return filePath;
		} catch (InvalidExtensionException | FileSizeLimitExceededException | FileNameLengthLimitExceededException e) {
			// 捕捉已知的自定义校验异常，无需标记为"未知"，直接传递原始信息
			log.error("文件校验失败，文件名：{}，原因：{}", file.getOriginalFilename(), e.getMessage(), e);
			throw new FileUploadException("文件上传失败：" + e.getMessage(), e); // 携带原始异常作为cause
		} catch (IOException e) {
			// IO异常单独处理，明确上下文（如文件写入失败）
			log.error("文件写入失败，文件名：{}，路径：{}", file.getOriginalFilename(), baseDir, e);
			throw new FileUploadException("文件上传失败：IO操作异常（路径不合法或磁盘写入失败）", e);
		} catch (Exception e) {
			// 剩余未预料的异常，添加更具体的上下文信息
			log.error("文件上传过程中发生未预料异常，文件名：{}", file.getOriginalFilename(), e);
			throw new FileUploadException("文件上传失败：未预料的错误（" + e.getMessage() + "）", e);
		}
	}

	/**
	 * 验证上传参数
	 */
	private static void validateUploadParameters(String baseDir, MultipartFile file) throws FileUploadException {
		if (StringUtils.isEmpty(baseDir)) {
			throw new FileUploadException("基础目录不能为空");
		}
		if (file == null || file.isEmpty()) {
			throw new FileUploadException("上传文件不能为空");
		}
	}

	/**
	 * 文件大小校验
	 */
	private static void assertFileSize(MultipartFile file, long maxSize) throws FileSizeLimitExceededException {
		if (maxSize > 0 && file.getSize() > maxSize) {
			throw new FileSizeLimitExceededException("文件大小超出限制", file.getSize(), maxSize);
		}
	}

	/**
	 * 文件名长度校验
	 */
	private static void assertFileNameLength(String fileName) throws FileNameLengthLimitExceededException {
		if (StringUtils.isNotEmpty(fileName) && fileName.length() > DEFAULT_FILE_NAME_LENGTH) {
			throw new FileNameLengthLimitExceededException("文件名长度超出限制", fileName.length(), DEFAULT_FILE_NAME_LENGTH);
		}
	}

	/**
	 * 文件扩展名校验
	 */
	private static void assertAllowed(MultipartFile file, String[] allowedExtension)
			throws InvalidExtensionException {
		String fileName = file.getOriginalFilename();
		if (StringUtils.isEmpty(fileName)) {
			throw new InvalidExtensionException("文件名不能为空");
		}

		// 检查文件名是否包含路径遍历字符
		if (StringUtils.contains(fileName, "..") || StringUtils.contains(fileName, "/")
				|| StringUtils.contains(fileName, "\\")) {
			throw new InvalidExtensionException("文件名不合法，不能包含路径字符");
		}

		String extension = getFileExtension(fileName);

		// 检查是否在拒绝列表中
		if (isInExtensionArray(extension, DEFAULT_DENIED_EXTENSION)) {
			throw new InvalidExtensionException("不允许上传的文件类型: " + extension);
		}

		// 检查是否在允许列表中
		if (allowedExtension != null && allowedExtension.length > 0
				&& !isInExtensionArray(extension, allowedExtension)) {
			throw new InvalidExtensionException("不支持的文件类型: " + extension);
		}
	}

	/**
	 * 判断文件扩展名是否在数组中
	 */
	private static boolean isInExtensionArray(String extension, String[] extensions) {
		if (StringUtils.isEmpty(extension) || extensions == null || extensions.length == 0) {
			return false;
		}
		for (String ext : extensions) {
			if (ext.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 */
	private static String getFileExtension(String fileName) {
		return FilenameUtils.getExtension(fileName);
	}

	/**
	 * 生成文件名
	 */
	private static String generateFileName(String extension) {
		return DateHelper.datePath() + "/" + UUID.randomUUID() + "." + extension;
	}

	/**
	 * 获取文件相对路径
	 */
	static String getPathFileName(String baseDir, String fileName) {
		String currentDir = StringUtils.replace(baseDir, "\\", "/");
		String fileDir = StringUtils.replace(fileName, "\\", "/");
		return StringUtils.substring(currentDir, 0, currentDir.lastIndexOf("/")) + "/" + fileDir;
	}

	/**
	 * 获取绝对路径的文件
	 */
	static File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
		String filePath = new File(uploadDir, fileName).getPath();
		File desc = new File(filePath).getCanonicalFile();

		// 检查文件是否在上传目录内，防止路径遍历攻击
		String canonicalPath = desc.getPath();
		String canonicalUploadPath = new File(uploadDir).getCanonicalPath();
		if (!canonicalPath.startsWith(canonicalUploadPath)) {
			throw new IOException("文件路径不合法: " + filePath);
		}

		if (!desc.getParentFile().exists()) {
			desc.getParentFile().mkdirs();
		}
		return desc;
	}

	/**
	 * 文件名称编码
	 */
	public static String extractFilename(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		if (StringUtils.isNotEmpty(fileName)) {
			try {
				return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
			} catch (Exception e) {
				log.warn("文件名编码失败: {}", fileName, e);
				return fileName;
			}
		}
		return "";
	}

	/**
	 * 设置默认上传参数
	 */
	public static void setDefaultBaseDir(String defaultBaseDir) {
		FileUploadUtils.defaultBaseDir = defaultBaseDir;
	}

	/**
	 * 获取默认上传目录
	 */
	public static String getDefaultBaseDir() {
		return defaultBaseDir;
	}
}