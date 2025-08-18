package com.groqdata.framework.security.handle;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.groqdata.common.constant.HttpStatus;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.utils.ServletUtils;
import com.groqdata.common.utils.StringHelper;

/**
 * 认证失败处理类 返回未授权
 * 
 * @author MISP TEAM
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
	@Serial
	private static final long serialVersionUID = -8970718410437077606L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException {
		int code = HttpStatus.UNAUTHORIZED;
		String msg = StringHelper.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
		ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
	}
}
