package com.groqdata.framework.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

import com.groqdata.common.exception.mybatis.MyBatisInitException;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Mybatis支持*匹配扫描包
 * 
 * @author MISP TEAM
 */
@Configuration
public class MyBatisConfig {
	private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);

	private final Environment env;

	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	public MyBatisConfig(Environment env) {
		this.env = env;
	}

	/**
	 * 设置MyBatis类型别名包路径
	 *
	 * @param typeAliasesPackage 类型别名包路径，支持多个路径用逗号分隔
	 * @return 处理后的包路径字符串
	 */
	public static String setTypeAliasesPackage(String typeAliasesPackage) {
		// 创建资源模式解析器和元数据读取器工厂
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
		List<String> allResult = new ArrayList<>();

		try {
			// 按逗号分割多个包路径并逐一处理
			for (String aliasesPackage : typeAliasesPackage.split(",")) {
				// 扫描单个包路径下的所有类
				List<String> result = scanPackageForClasses(resolver, metadataReaderFactory, aliasesPackage.trim());
				if (!result.isEmpty()) {
					// 使用HashSet去重后添加到结果列表
					allResult.addAll(new HashSet<>(result));
				}
			}

			// 如果没有扫描到任何包，抛出异常
			if (allResult.isEmpty()) {
				throw new MyBatisInitException("mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:" + typeAliasesPackage + "未找到任何包");
			}

			// 将所有包路径用逗号连接返回
			return String.join(",", allResult);
		} catch (IOException e) {
			logger.error("扫描MyBatis类型别名包时发生IO异常: {}", typeAliasesPackage, e);
			return "";
		}
	}

	/**
	 * 扫描指定包路径下的所有类并提取包名
	 *
	 * @param resolver 资源模式解析器
	 * @param metadataReaderFactory 元数据读取器工厂
	 * @param basePackage 基础包路径
	 * @return 包名列表
	 * @throws IOException IO异常
	 */
	private static List<String> scanPackageForClasses(ResourcePatternResolver resolver,
													  MetadataReaderFactory metadataReaderFactory,
													  String basePackage) throws IOException {
		// 构建资源搜索路径模式
		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;

		// 获取匹配的所有资源
		Resource[] resources = resolver.getResources(packageSearchPath);
		List<String> result = new ArrayList<>();

		// 遍历所有资源文件
		for (Resource resource : resources) {
			// 检查资源是否可读
			if (resource.isReadable()) {
				try {
					// 读取类的元数据
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
					String className = metadataReader.getClassMetadata().getClassName();
					// 获取类所在的包名并添加到结果中
					result.add(Class.forName(className).getPackage().getName());
				} catch (ClassNotFoundException e) {
					logger.warn("类未找到: {}", e.getMessage(), e);
				}
			}
		}

		return result;
	}


	public Resource[] resolveMapperLocations(String[] mapperLocations) {
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		List<Resource> resources = new ArrayList<>();
		if (mapperLocations != null) {
			for (String mapperLocation : mapperLocations) {
				try {
					Resource[] mappers = resourceResolver.getResources(mapperLocation);
					resources.addAll(Arrays.asList(mappers));
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return resources.toArray(new Resource[0]);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		String typeAliasesPackage = env.getProperty("mybatis.typeAliasesPackage");
		String mapperLocations = env.getProperty("mybatis.mapperLocations");
		String configLocation = env.getProperty("mybatis.configLocation");
		if (typeAliasesPackage != null && !typeAliasesPackage.trim().isEmpty()) {
			typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage);
		} else {
			typeAliasesPackage = ""; // 可设置默认包路径
		}

		VFS.addImplClass(SpringBootVFS.class);

		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
		sessionFactory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ",")));
		if (StringUtils.isNotBlank(configLocation)) {
			sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
		}

		return sessionFactory.getObject();
	}
}