package com.groqdata.generator.util;

import java.util.Properties;

import com.groqdata.common.exception.UtilException;
import org.apache.velocity.app.Velocity;
import com.groqdata.common.constant.Constants;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * VelocityEngine工厂
 * 
 * @author MISP TEAM
 */
public class VelocityInitializer {
	private VelocityInitializer() {
		throw new IllegalStateException("工具类不可实例化");
	}
	/**
	 * 初始化vm方法
	 */
	public static void initVelocity() {
		Properties p = new Properties();
		try {
			// 加载classpath目录下的vm文件
			p.setProperty("resource.loader.file.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			// 定义字符集
			p.setProperty(RuntimeConstants.INPUT_ENCODING, Constants.UTF8);
			// 初始化Velocity引擎，指定配置Properties
			Velocity.init(p);
		} catch (Exception e) {
			throw new UtilException(e);
		}
	}
}
