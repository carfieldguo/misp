package com.groqdata.framework.config;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.groqdata.common.filter.RepeatableFilter;
import com.groqdata.common.filter.XssFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered; // 引入Ordered类

/**
 * Filter配置
 *
 * @author MISP TEAM
 */
@Configuration
public class FilterConfig {
	@Value("${xss.excludes}")
	private String excludes;

	@Value("${xss.urlPatterns}")
	private String urlPatterns;

	@SuppressWarnings({"rawtypes", "unchecked" })
	@Bean
	@ConditionalOnProperty(value = "xss.enabled", havingValue = "true")
	public FilterRegistrationBean xssFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.setFilter(new XssFilter());
		registration.addUrlPatterns(StringUtils.split(urlPatterns, ","));
		registration.setName("xssFilter");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 修复点
		Map<String, String> initParameters = new HashMap<>();
		initParameters.put("excludes", excludes);
		registration.setInitParameters(initParameters);
		return registration;
	}

	@SuppressWarnings({"rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new RepeatableFilter());
		registration.addUrlPatterns("/*");
		registration.setName("repeatableFilter");
		registration.setOrder(Ordered.LOWEST_PRECEDENCE); // 可选优化点
		return registration;
	}

}
