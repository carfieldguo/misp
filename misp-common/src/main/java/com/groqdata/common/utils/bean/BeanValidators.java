package com.groqdata.common.utils.bean;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * bean对象属性验证
 * 
 * @author MISP TEAM
 */
public class BeanValidators {

	private BeanValidators() {
		// 防止实例化
		throw new IllegalStateException("工具类直接使用不需要实例化");
	}
	public static void validateWithException(Validator validator, Object object, Class<?>... groups)
			throws ConstraintViolationException {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
	}
}
