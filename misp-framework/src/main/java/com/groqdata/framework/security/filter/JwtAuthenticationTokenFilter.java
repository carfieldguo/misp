package com.groqdata.framework.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.groqdata.common.core.domain.model.LoginUser;
import com.groqdata.common.utils.SecurityUtils;
import com.groqdata.common.utils.basic.StringHelper;
import com.groqdata.framework.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 *
 * @author MISP TEAM
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private final TokenService tokenService;

	public JwtAuthenticationTokenFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		LoginUser loginUser = tokenService.getLoginUser(request);
		if (StringHelper.isNotNull(loginUser) && StringHelper.isNull(SecurityUtils.getAuthentication())) {
			tokenService.verifyToken(loginUser);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,
					null, loginUser.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		chain.doFilter(request, response);
	}
}
