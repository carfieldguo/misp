package com.groqdata.framework.aspectj;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.groqdata.common.annotation.DataScope;
import com.groqdata.common.core.domain.BaseEntity;
import com.groqdata.common.core.domain.entity.SysRole;
import com.groqdata.common.core.domain.entity.SysUser;
import com.groqdata.common.core.domain.model.LoginUser;
import com.groqdata.common.core.text.Convert;
import com.groqdata.common.utils.SecurityUtils;
import com.groqdata.common.utils.StringHelper;
import com.groqdata.framework.security.context.PermissionContextHolder;

/**
 * 数据过滤处理切面
 * 用于根据用户的数据权限动态生成SQL查询条件，实现数据级别的权限控制
 *
 * @author MISP TEAM
 */
@Aspect
@Component
public class DataScopeAspect {
	/**
	 * 全部数据权限 - 可查看所有数据
	 */
	public static final String DATA_SCOPE_ALL = "1";

	/**
	 * 自定数据权限 - 只能查看指定部门的数据
	 */
	public static final String DATA_SCOPE_CUSTOM = "2";

	/**
	 * 部门数据权限 - 只能查看本部门数据
	 */
	public static final String DATA_SCOPE_DEPT = "3";

	/**
	 * 部门及以下数据权限 - 可查看本部门及下属部门数据
	 */
	public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

	/**
	 * 仅本人数据权限 - 只能查看自己的数据
	 */
	public static final String DATA_SCOPE_SELF = "5";

	/**
	 * 数据权限过滤关键字 - 用于在SQL中标识数据权限过滤条件
	 */
	public static final String DATA_SCOPE = "dataScope";

	/**
	 * 前置通知，在执行被@DataScope注解标记的方法前执行
	 *
	 * @param point 切点信息
	 * @param controllerDataScope 数据权限注解
	 */
	@Before("@annotation(controllerDataScope)")
	public void doBefore(JoinPoint point, DataScope controllerDataScope) {
		// 清空之前可能存在的数据权限条件，防止SQL注入
		clearDataScope(point);
		// 处理数据权限过滤
		handleDataScope(point, controllerDataScope);
	}

	/**
	 * 处理数据权限过滤逻辑
	 *
	 * @param joinPoint 切点信息
	 * @param controllerDataScope 数据权限注解
	 */
	protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
		// 获取当前登录用户信息
		LoginUser loginUser = SecurityUtils.getLoginUser();
		if (StringHelper.isNotNull(loginUser)) {
			SysUser currentUser = loginUser.getUser();
			// 如果不是超级管理员，则进行数据权限过滤
			if (StringHelper.isNotNull(currentUser) && !currentUser.isAdmin()) {
				// 获取权限字符串，如果注解中未指定则从权限上下文中获取
				String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(),
						PermissionContextHolder.getContext());
				// 执行数据权限过滤
				dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
						controllerDataScope.userAlias(), permission);
			}
		}
	}

	/**
	 * 数据范围过滤核心方法
	 * 根据用户角色的数据权限生成相应的SQL过滤条件
	 *
	 * @param joinPoint 切点
	 * @param user 用户信息
	 * @param deptAlias 部门表别名
	 * @param userAlias 用户表别名
	 * @param permission 权限字符
	 */
	public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias,
									   String permission) {
		StringBuilder sqlString = new StringBuilder();
		List<String> conditions = new ArrayList<>();
		List<String> scopeCustomIds = new ArrayList<>();

		// 提前筛选包含指定权限的角色
		List<SysRole> rolesWithPermission = filterRolesByPermission(user.getRoles(), permission);

		// 收集自定义数据权限的角色ID
		collectCustomScopeRoleIds(rolesWithPermission, scopeCustomIds);

		// 构建SQL条件
		buildSqlConditions(rolesWithPermission, user, deptAlias, userAlias, scopeCustomIds, sqlString, conditions);

		// 如果没有任何权限匹配，添加限制条件确保不查数据
		if (conditions.isEmpty()) {
			sqlString.append(StringHelper.format(" OR {}.dept_id = 0 ", deptAlias));
		}

		// 设置数据权限过滤参数
		if (StringUtils.isNotBlank(sqlString.toString())) {
			Object params = joinPoint.getArgs()[0];
			// 使用Java 14+的模式匹配语法检查参数类型并转换
			if (StringHelper.isNotNull(params) && params instanceof BaseEntity baseEntity) {
				baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
			}
		}
	}

	/**
	 * 根据权限字符串筛选具有相应权限的角色
	 *
	 * @param roles 用户的所有角色
	 * @param permission 权限字符串
	 * @return 具有指定权限的角色列表
	 */
	private static List<SysRole> filterRolesByPermission(List<SysRole> roles, String permission) {
		List<SysRole> result = new ArrayList<>();
		String[] permissions = Convert.toStrArray(permission);
		for (SysRole role : roles) {
			if (StringHelper.containsAny(role.getPermissions(), permissions)) {
				result.add(role);
			}
		}
		return result;
	}

	/**
	 * 收集具有自定义数据权限的角色ID
	 *
	 * @param roles 具有指定权限的角色列表
	 * @param scopeCustomIds 用于存储角色ID的列表
	 */
	private static void collectCustomScopeRoleIds(List<SysRole> roles, List<String> scopeCustomIds) {
		for (SysRole role : roles) {
			if (DATA_SCOPE_CUSTOM.equals(role.getDataScope())) {
				scopeCustomIds.add(Convert.toStr(role.getRoleId()));
			}
		}
	}

	/**
	 * 根据角色的数据权限类型构建相应的SQL条件
	 *
	 * @param roles 具有指定权限的角色列表
	 * @param user 当前用户
	 * @param deptAlias 部门表别名
	 * @param userAlias 用户表别名
	 * @param scopeCustomIds 自定义权限的角色ID列表
	 * @param sqlString SQL条件构建器
	 * @param conditions 已处理的权限类型列表，避免重复处理
	 */
	private static void buildSqlConditions(List<SysRole> roles, SysUser user, String deptAlias, String userAlias,
										   List<String> scopeCustomIds, StringBuilder sqlString, List<String> conditions) {
		for (SysRole role : roles) {
			String dataScope = role.getDataScope();
			// 如果该权限类型已处理过，则跳过
			if (conditions.contains(dataScope)) {
				continue;
			}

			conditions.add(dataScope);

			// 根据不同的数据权限类型构建相应的SQL条件
			switch (dataScope) {
				case DATA_SCOPE_ALL:
					sqlString.setLength(0); // 清空之前条件
					return; // 全部数据权限，无需继续构建条件
				case DATA_SCOPE_CUSTOM:
					appendCustomCondition(sqlString, deptAlias, scopeCustomIds, role);
					break;
				case DATA_SCOPE_DEPT:
					sqlString.append(StringHelper.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
					break;
				case DATA_SCOPE_DEPT_AND_CHILD:
					sqlString.append(StringHelper.format(
							" OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
							deptAlias, user.getDeptId(), user.getDeptId()));
					break;
				case DATA_SCOPE_SELF:
					if (StringUtils.isNotBlank(userAlias)) {
						sqlString.append(StringHelper.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
					} else {
						sqlString.append(StringHelper.format(" OR {}.dept_id = 0 ", deptAlias));
					}
					break;
				default:
					// 未知权限类型忽略
					break;
			}
		}
	}

	/**
	 * 构建自定义数据权限的SQL条件
	 *
	 * @param sqlString SQL条件构建器
	 * @param deptAlias 部门表别名
	 * @param scopeCustomIds 自定义权限的角色ID列表
	 * @param role 当前处理的角色
	 */
	private static void appendCustomCondition(StringBuilder sqlString, String deptAlias, List<String> scopeCustomIds, SysRole role) {
		if (scopeCustomIds.size() > 1) {
			sqlString.append(StringHelper.format(
					" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ",
					deptAlias, String.join(",", scopeCustomIds)));
		} else {
			sqlString.append(StringHelper.format(
					" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ",
					deptAlias, role.getRoleId()));
		}
	}

	/**
	 * 拼接权限sql前先清空params.dataScope参数防止注入
	 *
	 * @param joinPoint 切点信息
	 */
	private void clearDataScope(final JoinPoint joinPoint) {
		Object params = joinPoint.getArgs()[0];
		// 使用Java 14+的模式匹配语法检查参数类型并转换
		if (StringHelper.isNotNull(params) && params instanceof BaseEntity baseEntity) {
			baseEntity.getParams().put(DATA_SCOPE, "");
		}
	}
}
