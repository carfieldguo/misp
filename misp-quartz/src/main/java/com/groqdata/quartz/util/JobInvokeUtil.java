package com.groqdata.quartz.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.groqdata.common.utils.spring.SpringUtils;
import com.groqdata.quartz.domain.SysJob;

/**
 * 任务执行工具
 *
 * @author MISP TEAM
 */
public class JobInvokeUtil {
	private JobInvokeUtil() {
		throw new IllegalStateException("工具类不可实例化");
	}

	/**
	 * 执行方法
	 *
	 * @param sysJob 系统任务
	 */
	public static void invokeMethod(SysJob sysJob) throws Exception {
		String invokeTarget = sysJob.getInvokeTarget();
		String beanName = getBeanName(invokeTarget);
		String methodName = getMethodName(invokeTarget);
		List<Object[]> methodParams = getMethodParams(invokeTarget);

		if (!isValidClassName(beanName)) {
			Object bean = SpringUtils.getBean(beanName);
			invokeMethod(bean, methodName, methodParams);
		} else {
			Object bean = Class.forName(beanName).getDeclaredConstructor().newInstance();
			invokeMethod(bean, methodName, methodParams);
		}
	}

	/**
	 * 调用任务方法
	 *
	 * @param bean 目标对象
	 * @param methodName 方法名称
	 * @param methodParams 方法参数
	 */
	private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (methodParams != null && !methodParams.isEmpty()) {
			Method method = bean.getClass().getMethod(methodName, getMethodParamsType(methodParams));
			method.invoke(bean, getMethodParamsValue(methodParams));
		} else {
			Method method = bean.getClass().getMethod(methodName);
			method.invoke(bean);
		}
	}

	/**
	 * 校验是否为为class包名
	 * 
	 * @param invokeTarget 名称
	 * @return true是 false否
	 */
	public static boolean isValidClassName(String invokeTarget) {
		return StringUtils.countMatches(invokeTarget, ".") > 1;
	}

	/**
	 * 获取bean名称
	 * 
	 * @param invokeTarget 目标字符串
	 * @return bean名称
	 */
	public static String getBeanName(String invokeTarget) {
		String beanName = StringUtils.substringBefore(invokeTarget, "(");
		return StringUtils.substringBeforeLast(beanName, ".");
	}

	/**
	 * 获取bean方法
	 * 
	 * @param invokeTarget 目标字符串
	 * @return method方法
	 */
	public static String getMethodName(String invokeTarget) {
		String methodName = StringUtils.substringBefore(invokeTarget, "(");
		return StringUtils.substringAfterLast(methodName, ".");
	}

	/**
	 * 获取method方法参数相关列表
	 * 
	 * @param invokeTarget 目标字符串
	 * @return method方法相关参数列表
	 */
	public static List<Object[]> getMethodParams(String invokeTarget) {
		String methodStr = StringUtils.substringBetween(invokeTarget, "(", ")");
		if (StringUtils.isEmpty(methodStr)) {
			return Collections.emptyList();
		}
		// 使用更安全的方式解析参数，避免复杂的正则表达式
		List<String> methodParams = parseMethodParams(methodStr);
		List<Object[]> classs = new LinkedList<>();
		for (String str : methodParams) {
			String trimmedStr = StringUtils.trimToEmpty(str);
			// String字符串类型，以'或"开头
			if (StringUtils.startsWithAny(trimmedStr, "'", "\"")) {
				classs.add(new Object[]{StringUtils.substring(trimmedStr, 1, trimmedStr.length() - 1), String.class });
			}
			// boolean布尔类型，等于true或者false
			else if ("true".equalsIgnoreCase(trimmedStr) || "false".equalsIgnoreCase(trimmedStr)) {
				classs.add(new Object[]{Boolean.valueOf(trimmedStr), Boolean.class });
			}
			// long长整形，以L结尾
			else if (StringUtils.endsWith(trimmedStr, "L")) {
				classs.add(new Object[]{Long.valueOf(StringUtils.substring(trimmedStr, 0, trimmedStr.length() - 1)),
					Long.class });
			}
			// double浮点类型，以D结尾
			else if (StringUtils.endsWith(trimmedStr, "D")) {
				classs
						.add(new Object[]{Double.valueOf(StringUtils.substring(trimmedStr, 0, trimmedStr.length() - 1)),
							Double.class });
			}
			// 其他类型归类为整形
			else {
				classs.add(new Object[]{Integer.valueOf(trimmedStr), Integer.class });
			}
		}
		return classs;
	}

	/**
	 * 解析方法参数字符串
	 *
	 * @param methodStr 方法参数字符串
	 * @return 参数列表
	 */
	private static List<String> parseMethodParams(String methodStr) {
		List<String> params = new LinkedList<>();
		StringBuilder currentParam = new StringBuilder();
		boolean inQuotes = false;
		char quoteChar = '\0';

		for (int i = 0; i < methodStr.length(); i++) {
			char c = methodStr.charAt(i);

			if (!inQuotes) {
				if (c == '\'' || c == '"') {
					inQuotes = true;
					quoteChar = c;
					currentParam.append(c);
				} else if (c == ',') {
					params.add(currentParam.toString());
					currentParam = new StringBuilder();
				} else {
					currentParam.append(c);
				}
			} else {
				if (c == quoteChar) {
					inQuotes = false;
					quoteChar = '\0';
				}
				currentParam.append(c);
			}
		}

		// 添加最后一个参数
		if (!currentParam.isEmpty()) {
			params.add(currentParam.toString());
		}

		return params;
	}

	/**
	 * 获取参数类型
	 * 
	 * @param methodParams 参数相关列表
	 * @return 参数类型列表
	 */
	public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) {
		Class<?>[] classs = new Class<?>[methodParams.size()];
		int index = 0;
		for (Object[] os : methodParams) {
			classs[index] = (Class<?>) os[1];
			index++;
		}
		return classs;
	}

	/**
	 * 获取参数值
	 * 
	 * @param methodParams 参数相关列表
	 * @return 参数值列表
	 */
	public static Object[] getMethodParamsValue(List<Object[]> methodParams) {
		Object[] classs = new Object[methodParams.size()];
		int index = 0;
		for (Object[] os : methodParams) {
			classs[index] = os[0];
			index++;
		}
		return classs;
	}
}
