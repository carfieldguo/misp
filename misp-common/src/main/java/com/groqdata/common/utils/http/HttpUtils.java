package com.groqdata.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groqdata.common.constant.Constants;

/**
 * 通用http发送方法
 */
public class HttpUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
	private static final String ACCEPT = "*/*";
	private static final String CONNECTION = "Keep-Alive";
	private static final String CHARSET = "utf-8";

	private static final String RECEIVED_RESPONSE_MSG = "Received response: {}";

	private HttpUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 *
	 * @param url 发送请求的 URL
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		return sendGet(url, StringUtils.EMPTY);
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 *
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		return sendGet(url, param, Constants.UTF8);
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 *
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @param contentType 编码类型
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, String contentType) {
		StringBuilder result = new StringBuilder();
		String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
		log.info("Sending GET request to URL: {}", urlNameString);

		try {
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			setCommonRequestProperties(connection);
			connection.connect();

			try (BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), contentType))) {
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug(RECEIVED_RESPONSE_MSG, result);
			}
		} catch (Exception e) {
			logHttpError("GET", url, param, e);
		}
		return result.toString();
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		StringBuilder result = new StringBuilder();
		log.info("Sending POST request to URL: {}", url);

		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			setCommonRequestProperties(conn);
			conn.setDoOutput(true);
			conn.setDoInput(true);

			try (PrintWriter out = new PrintWriter(conn.getOutputStream())) {
				out.print(param);
				out.flush();
			}

			try (BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug(RECEIVED_RESPONSE_MSG, result);
			}
		} catch (Exception e) {
			logHttpError("POST", url, param, e);
		}
		return result.toString();
	}

	/**
	 * 发送HTTPS POST请求
	 *
	 * @param url 请求URL
	 * @param param 请求参数
	 * @return 响应结果
	 */
	public static String sendSSLPost(String url, String param) {
		StringBuilder result = new StringBuilder();
		String urlNameString = url + "?" + param;
		log.info("Sending SSL POST request to URL: {}", urlNameString);

		try {
			SSLContext sc = createSSLContext();
			URL console = new URL(urlNameString);
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
			setCommonRequestProperties(conn);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(createHostnameVerifier());
			conn.connect();

			try (BufferedReader br = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (StringUtils.isNotBlank(line)) {
						result.append(line);
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug(RECEIVED_RESPONSE_MSG, result);
			}
			conn.disconnect();
		} catch (Exception e) {
			logHttpError("SSL POST", url, param, e);
		}
		return result.toString();
	}

	private static void setCommonRequestProperties(URLConnection connection) {
		connection.setRequestProperty("accept", ACCEPT);
		connection.setRequestProperty("connection", CONNECTION);
		connection.setRequestProperty("user-agent", USER_AGENT);
		connection.setRequestProperty("Accept-Charset", CHARSET);
		connection.setRequestProperty("contentType", CHARSET);
	}

	private static void logHttpError(String method, String url, String param, Exception e) {
		String errorMsg = String.format("Error in HTTP %s request - URL: %s, Parameters: %s",
			method, url, param);
		if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
			log.error("{} - Connection error", errorMsg, e);
		} else if (e instanceof IOException) {
			log.error("{} - IO error", errorMsg, e);
		} else {
			log.error("{} - Unexpected error", errorMsg, e);
		}
	}

	private static SSLContext createSSLContext()
		throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("TLSv1.3"); // 使用现代TLS协议
		// 初始化SSL上下文时使用系统默认的信任管理器
		sslContext.init(null, getSystemTrustManagers(), new SecureRandom());
		return sslContext;
	}

	private static HostnameVerifier createHostnameVerifier() {
		// 使用默认的主机名验证器作为基础
		HostnameVerifier defaultVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

		return defaultVerifier::verify;
	}

	// 获取系统默认的信任管理器
	private static TrustManager[] getSystemTrustManagers() throws NoSuchAlgorithmException, KeyStoreException {
		// 使用默认算法获取信任管理器工厂
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
			TrustManagerFactory.getDefaultAlgorithm());
		// 初始化工厂，使用系统默认的信任存储
		trustManagerFactory.init((KeyStore) null);
		return trustManagerFactory.getTrustManagers();
	}

}