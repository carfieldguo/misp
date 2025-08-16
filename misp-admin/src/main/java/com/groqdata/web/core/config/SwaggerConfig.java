package com.groqdata.web.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.groqdata.common.config.MispConfig;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger2的接口配置
 * 
 * @author MISP TEAM
 */
@Configuration
public class SwaggerConfig {
	/** 系统基础配置 */
	private final MispConfig mispConfig;

	public SwaggerConfig(MispConfig mispConfig) {
		this.mispConfig = mispConfig;
	}

	/** 是否开启swagger */
	@Value("${swagger.enabled}")
	private boolean enabled;

	/** 设置请求的统一前缀 */
	@Value("${swagger.pathMapping}")
	private String pathMapping;

	/**
	 * 创建API
	 */
	@Bean
	public Docket createSystemRestApi() {
		return new Docket(DocumentationType.OAS_30)
			// 是否启用Swagger
			.enable(enabled)
			// 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
			.apiInfo(apiInfo())
			// 设置分组名称
			.groupName("系统模块")
			// 设置哪些接口暴露给Swagger展示
			.select()
			// 扫描所有有注解的api，用这种方式更灵活
			.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
			// 扫描指定包中的swagger注解
			.apis(RequestHandlerSelectors.basePackage("com.groqdata.web.controller.system"))
			// 扫描所有 .apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any()).build()
			/* 设置安全模式，swagger可以设置访问token */
			.securitySchemes(securitySchemes()).securityContexts(securityContexts()).pathMapping(pathMapping);
	}

	@Bean
	public Docket createConsumerRestApi() {
		return new Docket(DocumentationType.OAS_30)
			// 是否启用Swagger
			.enable(enabled)
			// 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
			.apiInfo(apiInfo())
			// 设置分组名称
			.groupName("服务购买者模块")
			// 设置哪些接口暴露给Swagger展示
			.select()
			// 扫描所有有注解的api，用这种方式更灵活
			.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
			// 扫描指定包中的swagger注解
			.apis(RequestHandlerSelectors.basePackage("com.groqdata.web.controller.consumer"))
			// 扫描所有 .apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any()).build()
			/* 设置安全模式，swagger可以设置访问token */
			.securitySchemes(securitySchemes()).securityContexts(securityContexts()).pathMapping(pathMapping);
	}

	@Bean
	public Docket createProviderRestApi() {
		return new Docket(DocumentationType.OAS_30)
			// 是否启用Swagger
			.enable(enabled)
			// 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
			.apiInfo(apiInfo())
			// 设置分组名称
			.groupName("服务提供者模块")
			// 设置哪些接口暴露给Swagger展示
			.select()
			// 扫描所有有注解的api，用这种方式更灵活
			.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
			// 扫描指定包中的swagger注解
			.apis(RequestHandlerSelectors.basePackage("com.groqdata.web.controller.provider"))
			// 扫描所有 .apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any()).build()
			/* 设置安全模式，swagger可以设置访问token */
			.securitySchemes(securitySchemes()).securityContexts(securityContexts()).pathMapping(pathMapping);
	}

	/**
	 * 安全模式，这里指定token通过Authorization头请求头传递
	 */
	private List<SecurityScheme> securitySchemes() {
		List<SecurityScheme> apiKeyList = new ArrayList<SecurityScheme>();
		apiKeyList.add(new ApiKey("Authorization", "Authorization", In.HEADER.toValue()));
		return apiKeyList;
	}

	/**
	 * 安全上下文
	 */
	private List<SecurityContext> securityContexts() {
		List<SecurityContext> securityContexts = new ArrayList<>();
		securityContexts.add(SecurityContext.builder().securityReferences(defaultAuth())
			.operationSelector(o -> o.requestMappingPattern().matches("/.*")).build());
		return securityContexts;
	}

	/**
	 * 默认的安全上引用
	 */
	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		List<SecurityReference> securityReferences = new ArrayList<>();
		securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
		return securityReferences;
	}

	/**
	 * 添加摘要信息
	 */
	private ApiInfo apiInfo() {
		// 用ApiInfoBuilder进行定制
		return new ApiInfoBuilder()
			// 设置标题
			.title("MISP管理系统_接口文档")
			// 描述
			.description("用于管理MISP系统的接口文档，提供了所有API的详细信息和使用方法。")
			// 作者信息
			.contact(new Contact(mispConfig.getName(), null, null))
			// 版本
			.version(mispConfig.getVersion()).build();
	}
}
