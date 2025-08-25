package com.groqdata.common.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groqdata.common.core.text.Convert;
import com.groqdata.common.utils.basic.DateHelper;

/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 * 
 * @author MISP TEAM
 */
@SuppressWarnings("rawtypes")
public class ReflectUtils {
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

	private static final String SETTER_PREFIX = "set";
	private static final String GETTER_PREFIX = "get";
	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	// 定义常量避免重复字面量
	private static final String FIELD_NOT_FOUND_MSG = "在 [{}] 中，没有找到 [{}] 字段 ";
	private static final String METHOD_NOT_FOUND_MSG = "在 [{}] 中，没有找到 [{}] 方法 ";
	private static final String CLASS_NOT_PARAMETRIZED_MSG = "{}'s superclass not ParameterizedType";
	private static final String INDEX_OUT_OF_BOUNDS_MSG = "Index: {}, Size of {}'s Parameterized Type: {}";
	private static final String CLASS_NOT_SET_MSG = "{} not set the actual class on superclass generic parameter";

	private ReflectUtils() {
		throw new IllegalStateException("工具类不可实例化");
	}

	/**
	 * 调用Getter方法.
	 * 支持多级，如：对象名.对象名.方法
	 */
	@SuppressWarnings("unchecked")
	public static <E> E invokeGetter(Object obj, String propertyName) {
		Object object = obj;
		for (String name : StringUtils.split(propertyName, ".")) {
			String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
			object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
		}
		return (E) object;
	}

	/**
	 * 调用Setter方法, 仅匹配方法名。
	 * 支持多级，如：对象名.对象名.方法
	 */

	public static <E> void invokeSetter(Object obj, String propertyName, E value) {
		Object object = obj;
		String[] names = StringUtils.split(propertyName, ".");
		for (int i = 0; i < names.length; i++) {
			if (i < names.length - 1) {
				String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
				object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
				if (object == null) {
					logger.warn("在 [{}] 中获取属性 [{}] 时返回 null，无法继续设置后续属性",
							obj != null ? obj.getClass().getName() : "null", names[i]);
					return;
				}
			} else {
				String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
				if (object != null) {
					invokeMethodByName(object, setterMethodName, new Object[]{value });
				} else {
					logger.warn("对象为 null，无法调用 setter 方法 [{}]", setterMethodName);
				}
			}
		}
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);
		if (field == null) {
			logger.debug(FIELD_NOT_FOUND_MSG, obj.getClass(), fieldName);
			return null;
		}
		E result = null;
		try {
			result = (E) field.get(obj);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static <E> void setFieldValue(final Object obj, final String fieldName, final E value) {
		Field field = getAccessibleField(obj, fieldName);
		if (field == null) {
			logger.debug(FIELD_NOT_FOUND_MSG, obj.getClass(), fieldName);
			return;
		}

		try {
			// 检查字段是否为final
			if (Modifier.isFinal(field.getModifiers())) {
				logger.warn("尝试修改final字段: {}.{}", obj.getClass().getName(), fieldName);
			}

			// 设置字段值
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			logger.error(FIELD_NOT_FOUND_MSG, obj.getClass().getName(), fieldName, e);
			throw new IllegalArgumentException("无法设置字段值: " + fieldName, e);
		} catch (Exception e) {
			logger.error("设置字段值时发生异常: {}.{}", obj.getClass().getName(), fieldName, e);
			throw new RuntimeException("设置字段值失败: " + fieldName, e);
		}
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
	 * 同时匹配方法名+参数类型，
	 */
	@SuppressWarnings("unchecked")
	public static <E> E invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
			final Object[] args) {
		if (obj == null || methodName == null) {
			return null;
		}
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			logger.debug(METHOD_NOT_FOUND_MSG, obj.getClass(), methodName);
			return null;
		}
		try {
			return (E) method.invoke(obj, args);
		} catch (Exception e) {
			String msg = String.format("method: %s, obj: %s, args: %s", method, obj, args);
			throw convertReflectionExceptionToUnchecked(msg, e);
		}
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符，
	 * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
	 * 只匹配函数名，如果有多个同名函数调用第一个。
	 */
	@SuppressWarnings("unchecked")
	public static <E> E invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName, args.length);
		if (method == null) {
			logger.debug(METHOD_NOT_FOUND_MSG, obj.getClass(), methodName);
			return null;
		}
		try {
			convertArgumentsIfNeeded(method, args);
			return (E) method.invoke(obj, args);
		} catch (Exception e) {
			String msg = String.format("method: %s, obj: %s, args: %s", method, obj, args);
			throw convertReflectionExceptionToUnchecked(msg, e);
		}
	}

	private static void convertArgumentsIfNeeded(Method method, Object[] args) {
		Class<?>[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			if (args[i] != null) {
				Class<?> targetType = paramTypes[i];
				Class<?> sourceType = args[i].getClass();

				if (!sourceType.equals(targetType)) {
					args[i] = convertArgument(targetType, args[i]);
				}
			}
		}
	}

	private static Object convertArgument(Class<?> targetType, Object arg) {
		if (targetType == String.class) {
			return convertToString(arg);
		} else if (targetType == Integer.class || targetType == int.class) {
			return Convert.toInt(arg);
		} else if (targetType == Long.class || targetType == long.class) {
			return Convert.toLong(arg);
		} else if (targetType == Double.class || targetType == double.class) {
			return Convert.toDouble(arg);
		} else if (targetType == Float.class || targetType == float.class) {
			return Convert.toFloat(arg);
		} else if (targetType == Date.class) {
			return convertToDate(arg);
		} else if (targetType == Boolean.class || targetType == boolean.class) {
			return Convert.toBool(arg);
		}
		return arg;
	}

	private static String convertToString(Object arg) {
		String result = Convert.toStr(arg);
		if (StringUtils.endsWith(result, ".0")) {
			result = StringUtils.substringBefore(result, ".0");
		}
		return result;
	}

	private static Date convertToDate(Object arg) {
		if (arg instanceof String) {
			return DateHelper.parseDate(arg);
		} else if (arg instanceof Double doubleVal) {
			return DateUtil.getJavaDate(doubleVal);
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		// 为空不报错。直接返回 null
		if (obj == null) {
			return null;
		}
		Validate.notBlank(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {
				logger.error(FIELD_NOT_FOUND_MSG, superClass, fieldName);
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 匹配函数名+参数类型。
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
			final Class<?>... parameterTypes) {
		// 为空不报错。直接返回 null
		if (obj == null) {
			return null;
		}
		Validate.notBlank(methodName, "methodName can't be blank");
		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			} catch (NoSuchMethodException e) {
				logger.error(METHOD_NOT_FOUND_MSG, searchType, methodName);
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 只匹配函数名。
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 */
	public static Method getAccessibleMethodByName(final Object obj, final String methodName, int argsNum) {
		// 为空不报错。直接返回 null
		if (obj == null) {
			return null;
		}
		Validate.notBlank(methodName, "methodName can't be blank");
		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName) && method.getParameterTypes().length == argsNum) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
	 * 如无法找到, 返回Object.class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassGenricType(final Class clazz) {
		return getClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 */
	public static Class getClassGenricType(final Class clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.debug(CLASS_NOT_PARAMETRIZED_MSG, clazz.getSimpleName());
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.debug(INDEX_OUT_OF_BOUNDS_MSG, index, clazz.getSimpleName(), params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.debug(CLASS_NOT_SET_MSG, clazz.getSimpleName());
			return Object.class;
		}

		return (Class) params[index];
	}

	public static Class<?> getUserClass(Object instance) throws ReflectiveOperationException {
		if (instance == null) {
			throw new ReflectiveOperationException("Instance must not be null");
		}
		Class clazz = instance.getClass();
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(msg, e);
		} else if (e instanceof InvocationTargetException invocationtargetexception) {
			return new RuntimeException(msg, invocationtargetexception.getTargetException());
		}
		return new RuntimeException(msg, e);
	}
}
