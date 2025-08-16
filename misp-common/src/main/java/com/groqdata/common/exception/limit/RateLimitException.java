package com.groqdata.common.exception.limit;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;

/**
 * 限流异常类，用于表示接口调用频率超过限制时抛出的异常
 * <p>
 * 包含限流相关的上下文信息，如限流阈值、剩余重试时间等，便于问题排查和客户端处理
 *
 * @author MISP Team
 */
public class RateLimitException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 限流阈值（单位：次/时间单位）
	 */
	private final int limit;

	/**
	 * 时间单位描述（如"分钟"、"小时"）
	 */
	private final String timeUnit;

	/**
	 * 建议重试时间（单位：秒），-1表示未指定
	 */
	private final int retryAfterSeconds;

	/**
	 * 原始异常原因
	 */
	private final Throwable cause;

	/**
	 * 构造一个包含完整限流信息的异常
	 *
	 * @param message          异常消息
	 * @param limit            限流阈值
	 * @param timeUnit         时间单位描述
	 * @param retryAfterSeconds 建议重试时间（秒）
	 * @param cause            原始异常原因（可为null）
	 */
	public RateLimitException(String message, int limit, String timeUnit, int retryAfterSeconds, Throwable cause) {
		super(message);
		this.limit = limit;
		this.timeUnit = timeUnit;
		this.retryAfterSeconds = retryAfterSeconds;
		this.cause = cause;
	}

	/**
	 * 构造一个包含基本限流信息的异常（无原始原因）
	 *
	 * @param message          异常消息
	 * @param limit            限流阈值
	 * @param timeUnit         时间单位描述
	 * @param retryAfterSeconds 建议重试时间（秒）
	 */
	public RateLimitException(String message, int limit, String timeUnit, int retryAfterSeconds) {
		this(message, limit, timeUnit, retryAfterSeconds, null);
	}

	/**
	 * 构造一个仅包含消息的简单限流异常
	 *
	 * @param message 异常消息
	 */
	public RateLimitException(String message) {
		this(message, -1, "", -1, null);
	}

	/**
	 * 构造一个包含消息和原因的限流异常
	 *
	 * @param message 异常消息
	 * @param cause   异常原因
	 */
	public RateLimitException(String message, Throwable cause) {
		this(message, -1, "", -1, cause);
	}

	/**
	 * 获取限流阈值
	 *
	 * @return 限流阈值（次/时间单位）
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * 获取时间单位描述
	 *
	 * @return 时间单位（如"分钟"）
	 */
	public String getTimeUnit() {
		return timeUnit;
	}

	/**
	 * 获取建议重试时间
	 *
	 * @return 重试时间（秒），-1表示未指定
	 */
	public int getRetryAfterSeconds() {
		return retryAfterSeconds;
	}

	/**
	 * 获取原始异常原因
	 *
	 * @return 原始异常原因，可为null
	 */
	@Override
	public synchronized Throwable getCause() {
		return cause;
	}

	/**
	 * 打印异常堆栈信息，包含限流上下文
	 */
	@Override
	public void printStackTrace(PrintStream s) {
		s.println("RateLimitException: 限流阈值=" + limit + "次/" + timeUnit
			+ ", 建议重试时间=" + (retryAfterSeconds > 0 ? retryAfterSeconds + "秒" : "未指定"));
		super.printStackTrace(s);
		if (cause != null) {
			s.println("Caused by:");
			cause.printStackTrace(s);
		}
	}

	/**
	 * 打印异常堆栈信息，包含限流上下文
	 */
	@Override
	public void printStackTrace(PrintWriter s) {
		s.println("RateLimitException: 限流阈值=" + limit + "次/" + timeUnit
			+ ", 建议重试时间=" + (retryAfterSeconds > 0 ? retryAfterSeconds + "秒" : "未指定"));
		super.printStackTrace(s);
		if (cause != null) {
			s.println("Caused by:");
			cause.printStackTrace(s);
		}
	}
}
