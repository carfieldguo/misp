package com.groqdata.framework.aspectj;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSON;
import com.groqdata.common.annotation.Log;
import com.groqdata.common.core.domain.entity.SysUser;
import com.groqdata.common.core.domain.model.LoginUser;
import com.groqdata.common.enums.BusinessStatus;
import com.groqdata.common.enums.HttpMethod;
import com.groqdata.common.filter.PropertyPreExcludeFilter;
import com.groqdata.common.utils.SecurityUtils;
import com.groqdata.common.utils.ServletUtils;
import com.groqdata.common.utils.StringHelper;
import com.groqdata.common.utils.ip.IpUtils;
import com.groqdata.framework.manager.AsyncManager;
import com.groqdata.framework.manager.factory.AsyncFactory;
import com.groqdata.system.domain.SysOperLog;

/**
 * 操作日志记录处理
 * 
 * @author MISP TEAM
 */
@Aspect
@Component
public class LogAspect {
	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

	/** 排除敏感属性字段 */
	protected static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword" };

	/** 计算操作消耗时间 */
	private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("Cost Time");

	/**
	 * 字符串长度阈值，超过此值的字符串将被过滤
	 */
	private static final int STRING_LENGTH_THRESHOLD = 256;

	/**
	 * 处理请求前执行
	 */
	@Before(value = "@annotation(controllerLog)")
	public void boBefore(JoinPoint joinPoint, Log controllerLog) {
		TIME_THREADLOCAL.set(System.currentTimeMillis());
	}

	/**
	 * 处理完请求后执行
	 *
	 * @param joinPoint 切点
	 */
	@AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
	public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
		handleLog(joinPoint, controllerLog, null, jsonResult);
	}

	/**
	 * 拦截异常操作
	 * 
	 * @param joinPoint 切点
	 * @param e 异常
	 */
	@AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
		handleLog(joinPoint, controllerLog, e, null);
	}

	protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
		try {
			// 获取当前的用户
			LoginUser loginUser = SecurityUtils.getLoginUser();

			// *========数据库日志=========*//
			SysOperLog operLog = new SysOperLog();
			operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
			// 请求的地址
			String ip = IpUtils.getIpAddr();
			operLog.setOperIp(ip);
			operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
			if (loginUser != null) {
				operLog.setOperName(loginUser.getUsername());
				SysUser currentUser = loginUser.getUser();
				if (StringHelper.isNotNull(currentUser) && StringHelper.isNotNull(currentUser.getDept())) {
					operLog.setDeptName(currentUser.getDept().getDeptName());
				}
			}

			if (e != null) {
				operLog.setStatus(BusinessStatus.FAIL.ordinal());
				operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
			}
			// 设置方法名称
			String className = joinPoint.getTarget().getClass().getName();
			String methodName = joinPoint.getSignature().getName();
			operLog.setMethod(className + "." + methodName + "()");
			// 设置请求方式
			operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
			// 处理设置注解上的参数
			getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
			// 设置消耗时间
			operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
			// 保存数据库
			AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
		} catch (Exception exp) {
			// 记录本地异常日志
			log.error("异常信息:{}", exp.getMessage());
			log.error("异常堆栈：", exp);
		} finally {
			TIME_THREADLOCAL.remove();
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param log 日志
	 * @param operLog 操作日志
	 */
	public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) {
		// 设置action动作
		operLog.setBusinessType(log.businessType().ordinal());
		// 设置标题
		operLog.setTitle(log.title());
		// 设置操作人类别
		operLog.setOperatorType(log.operatorType().ordinal());
		// 是否需要保存request，参数和值
		if (log.isSaveRequestData()) {
			// 获取参数的信息，传入到数据库中。
			setRequestValue(joinPoint, operLog, log.excludeParamNames());
		}
		// 是否需要保存response，参数和值
		if (log.isSaveResponseData() && StringHelper.isNotNull(jsonResult)) {
			operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
		}
	}

	/**
	 * 获取请求的参数，放到log中
	 * 
	 * @param operLog 操作日志
	 */
	private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog, String[] excludeParamNames) {
		Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
		String requestMethod = operLog.getRequestMethod();
		if (StringHelper.isEmpty(paramsMap)
				&& (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))) {
			String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
			operLog.setOperParam(StringUtils.substring(params, 0, 2000));
		} else {
			operLog.setOperParam(StringUtils
					.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000));
		}
	}

	/**
	 * 参数拼装
	 */
	private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
		StringBuilder params = new StringBuilder();
		if (paramsArray != null) {
			for (Object o : paramsArray) {
				if (StringHelper.isNotNull(o) && !isFilterObject(o)) {
					try {
						String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
						params.append(jsonObj).append(" ");
					} catch (Exception e) {
						log.warn("参数转换为JSON字符串时发生异常: ", e);
					}
				}
			}
		}
		return params.toString().trim();
	}

	/**
	 * 忽略敏感属性
	 */
	public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) {
		return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
	}

	/**
	 * 判断对象是否为需要过滤的类型（包括嵌套结构和超长字符串）
	 *
	 * @param obj 待检查的对象
	 * @return 若为需要过滤的对象则返回true，否则返回false
	 */
	public boolean isFilterObject(final Object obj) {
		// 空对象无需过滤
		if (obj == null) {
			return false;
		}

		// 1. 检查基础敏感类型
		if (isBaseSensitiveType(obj)) {
			return true;
		}

		// 2. 检查超长字符串
		if (isLongString(obj)) {
			return true;
		}

		// 3. 检查数组类型（递归检查元素）
		if (obj.getClass().isArray()) {
			return isArrayContainsFilterType(obj);
		}

		// 4. 检查集合类型（递归检查元素）
		if (obj instanceof Collection<?>) {
			return isCollectionContainsFilterType((Collection<?>) obj);
		}

		// 5. 检查Map类型（递归检查值）
		if (obj instanceof Map<?, ?>) {
			return isMapContainsFilterType((Map<?, ?>) obj);
		}

		// 其他类型无需过滤
		return false;
	}

	/**
	 * 判断对象是否为基础敏感类型
	 *
	 * @param obj 待检查对象
	 * @return 基础敏感类型返回true
	 */
	private boolean isBaseSensitiveType(Object obj) {
		return obj instanceof MultipartFile
				|| obj instanceof HttpServletRequest
				|| obj instanceof HttpServletResponse
				|| obj instanceof BindingResult;
	}

	/**
	 * 判断对象是否为超长字符串
	 *
	 * @param obj 待检查对象
	 * @return 若为字符串且长度超过阈值则返回true
	 */
	private boolean isLongString(Object obj) {
		if (obj instanceof String str) {
			return str.length() > STRING_LENGTH_THRESHOLD;
		}
		return false;
	}

	/**
	 * 检查数组是否包含需要过滤的元素
	 *
	 * @param array 待检查数组
	 * @return 包含过滤元素返回true
	 */
	private boolean isArrayContainsFilterType(Object array) {
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++) {
			Object element = Array.get(array, i);
			if (isFilterObject(element)) { // 递归检查元素
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查集合是否包含需要过滤的元素
	 *
	 * @param collection 待检查集合
	 * @return 包含过滤元素返回true
	 */
	private boolean isCollectionContainsFilterType(Collection<?> collection) {
		for (Object element : collection) {
			if (isFilterObject(element)) { // 递归检查元素
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查Map的值是否包含需要过滤的类型
	 *
	 * @param map 待检查Map
	 * @return 包含过滤值返回true
	 */
	private boolean isMapContainsFilterType(Map<?, ?> map) {
		for (Object value : map.values()) {
			if (isFilterObject(value)) { // 递归检查值
				return true;
			}
		}
		return false;
	}

}
