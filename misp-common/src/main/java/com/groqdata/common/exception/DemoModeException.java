package com.groqdata.common.exception;

import java.io.Serial;

/**
 * 演示模式异常
 *
 * @author MISP TEAM
 */
public class DemoModeException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 演示模式异常构造函数
	 * 用于在演示模式下阻止某些操作执行
	 */
	public DemoModeException() {
		super("演示模式下不允许执行此操作");
	}
}
