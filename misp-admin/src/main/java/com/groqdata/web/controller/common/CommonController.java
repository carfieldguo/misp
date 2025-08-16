package com.groqdata.web.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.groqdata.common.config.MispConfig;
import com.groqdata.common.constant.Constants;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.utils.StringHelper;
import com.groqdata.common.utils.file.FileUploadUtils;
import com.groqdata.common.utils.file.FileUtils;
import com.groqdata.framework.config.ServerConfig;

/**
 * 通用请求处理控制器
 * 负责文件上传、下载及本地资源下载等通用功能
 *
 * @author MISP TEAM
 */
@RestController
@RequestMapping("/common")
public class CommonController {
	private static final Logger log = LoggerFactory.getLogger(CommonController.class);
	private final ServerConfig serverConfig;

	// 常量定义：文件分隔符
	private static final String FILE_DELIMITER = ",";

	public CommonController(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	/**
	 * 通用下载请求
	 *
	 * @param fileName 文件名（必填）
	 * @param delete   是否下载后删除文件（可选，默认false）
	 * @param response 响应对象
	 * @param request  请求对象
	 */
	@GetMapping("/download")
	public void fileDownload(String fileName, Boolean delete, HttpServletResponse response,
		HttpServletRequest request) {
		// 参数校验
		if (StringUtils.isBlank(fileName)) {
			handleDownloadError(response, "文件名不能为空");
			return;
		}

		try {
			// 校验文件合法性
			if (!FileUtils.checkAllowDownload(fileName)) {
				String errorMsg = StringHelper.format("文件名称({})非法，不允许下载。", fileName);
				handleDownloadError(response, errorMsg);
				return;
			}

			// 处理文件名称和路径
			String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = MispConfig.getDownloadPath() + fileName;

			// 写入响应流
			writeFileToResponse(response, filePath, realFileName);

			// 如需删除文件
			if (Boolean.TRUE.equals(delete)) {
				FileUtils.deleteFile(filePath);
			}
		} catch (Exception e) {
			String errorMsg = "下载文件失败：" + e.getMessage();
			log.error(errorMsg, e);
			handleDownloadError(response, errorMsg);
		}
	}

	/**
	 * 通用上传请求（单个文件）
	 *
	 * @param file 待上传的文件（必填）
	 * @return 上传结果（包含文件URL、名称等信息）
	 */
	@PostMapping("/upload")
	public AjaxResult uploadFile(MultipartFile file) {
		// 参数校验
		if (file == null || file.isEmpty()) {
			return AjaxResult.error("上传文件不能为空");
		}

		try {
			Map<String, String> fileInfo = handleSingleFileUpload(file);
			return AjaxResult.success(fileInfo);
		} catch (Exception e) {
			log.error("单个文件上传失败", e);
			return AjaxResult.error("上传失败：" + e.getMessage());
		}
	}

	/**
	 * 通用上传请求（多个文件）
	 *
	 * @param files 待上传的文件列表（必填，不可为空）
	 * @return 上传结果（包含多个文件的URL、名称等信息，用分隔符拼接）
	 */
	@PostMapping("/uploads")
	public AjaxResult uploadFiles(List<MultipartFile> files) {
		// 参数校验
		if (files == null || files.isEmpty()) {
			return AjaxResult.error("上传文件列表不能为空");
		}

		try {
			List<String> urls = new ArrayList<>();
			List<String> fileNames = new ArrayList<>();
			List<String> newFileNames = new ArrayList<>();
			List<String> originalFilenames = new ArrayList<>();

			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					log.warn("跳过空文件");
					continue;
				}
				Map<String, String> fileInfo = handleSingleFileUpload(file);
				urls.add(fileInfo.get("url"));
				fileNames.add(fileInfo.get("fileName"));
				newFileNames.add(fileInfo.get("newFileName"));
				originalFilenames.add(fileInfo.get("originalFilename"));
			}

			return AjaxResult.success()
				.put("urls", StringUtils.join(urls, FILE_DELIMITER))
				.put("fileNames", StringUtils.join(fileNames, FILE_DELIMITER))
				.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMITER))
				.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMITER));
		} catch (Exception e) {
			log.error("多个文件上传失败", e);
			return AjaxResult.error("上传失败：" + e.getMessage());
		}
	}

	/**
	 * 本地资源通用下载
	 *
	 * @param resource 资源路径（必填，格式需包含资源前缀）
	 * @param response 响应对象
	 */
	@GetMapping("/download/resource")
	public void resourceDownload(String resource, HttpServletResponse response) {
		// 参数校验
		if (StringUtils.isBlank(resource)) {
			handleDownloadError(response, "资源路径不能为空");
			return;
		}

		try {
			// 校验资源合法性
			if (!FileUtils.checkAllowDownload(resource)) {
				String errorMsg = StringHelper.format("资源文件({})非法，不允许下载。", resource);
				handleDownloadError(response, errorMsg);
				return;
			}

			// 处理资源路径和名称
			String localPath = MispConfig.getProfile();
			String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
			String downloadName = StringUtils.substringAfterLast(downloadPath, "/");

			// 写入响应流
			writeFileToResponse(response, downloadPath, downloadName);
		} catch (Exception e) {
			String errorMsg = "资源下载失败：" + e.getMessage();
			log.error(errorMsg, e);
			handleDownloadError(response, errorMsg);
		}
	}

	/**
	 * 处理单个文件上传的公共逻辑
	 *
	 * @param file 待上传文件
	 * @return 包含文件信息的Map（url、fileName、newFileName、originalFilename）
	 * @throws Exception 上传过程中的异常
	 */
	private Map<String, String> handleSingleFileUpload(MultipartFile file) throws Exception {
		String filePath = MispConfig.getUploadPath();
		String fileName = FileUploadUtils.upload(filePath, file);
		String url = serverConfig.getUrl() + fileName;

		Map<String, String> fileInfo = new HashMap<>(4);
		fileInfo.put("url", url);
		fileInfo.put("fileName", fileName);
		fileInfo.put("newFileName", FileUtils.getName(fileName));
		fileInfo.put("originalFilename", file.getOriginalFilename());
		return fileInfo;
	}

	/**
	 * 将文件写入响应流（下载公共逻辑）
	 *
	 * @param response     响应对象
	 * @param filePath     文件路径
	 * @param displayName  响应头中显示的文件名
	 * @throws Exception 写入过程中的异常
	 */
	private void writeFileToResponse(HttpServletResponse response, String filePath, String displayName)
		throws Exception {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		FileUtils.setAttachmentResponseHeader(response, displayName);
		FileUtils.writeBytes(filePath, response.getOutputStream());
	}

	/**
	 * 处理下载错误，设置响应状态并返回错误信息
	 *
	 * @param response 响应对象
	 * @param errorMsg 错误信息
	 */
	private void handleDownloadError(HttpServletResponse response, String errorMsg) {
		try {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(errorMsg);
		} catch (Exception e) {
			log.error("处理下载错误时发生异常", e);
		}
	}
}
