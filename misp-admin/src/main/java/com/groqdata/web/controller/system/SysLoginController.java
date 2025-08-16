package com.groqdata.web.controller.system;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.groqdata.common.constant.Constants;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.core.domain.entity.SysMenu;
import com.groqdata.common.core.domain.entity.SysUser;
import com.groqdata.common.core.domain.model.LoginBody;
import com.groqdata.common.utils.SecurityUtils;
import com.groqdata.framework.web.service.SysLoginService;
import com.groqdata.framework.web.service.SysPermissionService;
import com.groqdata.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author MISP TEAM
 */
@RestController
public class SysLoginController {

	private SysLoginService loginService;

	@Autowired
	public void setLoginService(SysLoginService loginService) {
		this.loginService = loginService;
	}

	private ISysMenuService menuService;

	@Autowired
	public void setMenuService(ISysMenuService menuService) {
		this.menuService = menuService;
	}

	private SysPermissionService permissionService;

	@Autowired
	public void setPermissionService(SysPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	/**
	 * 登录方法
	 * 
	 * @param loginBody 登录信息
	 * @return 结果
	 */
	@PostMapping("/login")
	public AjaxResult login(@RequestBody LoginBody loginBody) {
		AjaxResult ajax = AjaxResult.success();
		// 生成令牌
		String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
			loginBody.getUuid());
		ajax.put(Constants.TOKEN, token);
		return ajax;
	}

	/**
	 * 获取用户信息
	 * 
	 * @return 用户信息
	 */
	@GetMapping("getInfo")
	public AjaxResult getInfo() {
		SysUser user = SecurityUtils.getLoginUser().getUser();
		// 角色集合
		Set<String> roles = permissionService.getRolePermission(user);
		// 权限集合
		Set<String> permissions = permissionService.getMenuPermission(user);
		AjaxResult ajax = AjaxResult.success();
		ajax.put("user", user);
		ajax.put("roles", roles);
		ajax.put("permissions", permissions);
		return ajax;
	}

	/**
	 * 获取路由信息
	 * 
	 * @return 路由信息
	 */
	@GetMapping("getRouters")
	public AjaxResult getRouters() {
		Long userId = SecurityUtils.getUserId();
		List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
		return AjaxResult.success(menuService.buildMenus(menus));
	}
}
