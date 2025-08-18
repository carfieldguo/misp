package com.groqdata.framework.security.context;

import org.springframework.security.core.Authentication;

/**
 * 身份验证信息
 * 
 * @author MISP TEAM
 */
public class AuthenticationContextHolder {
	private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    // 添加私有构造函数防止实例化
    private AuthenticationContextHolder() {
    }

	public static Authentication getContext() {
		return contextHolder.get();
	}

	public static void setContext(Authentication context) {
		contextHolder.set(context);
	}

	public static void clearContext() {
		contextHolder.remove();
	}
}
