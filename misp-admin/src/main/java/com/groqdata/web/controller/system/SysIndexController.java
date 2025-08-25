package com.groqdata.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groqdata.common.config.MispConfig;
import com.groqdata.common.utils.basic.StringHelper;

/**
 * 首页
 *
 * @author MISP TEAM
 */
@RestController
public class SysIndexController {
	/** 系统基础配置 */

	private MispConfig mispConfig;

	@Autowired
	public void setMispConfig(MispConfig mispConfig) {
		this.mispConfig = mispConfig;
	}

	/**
	 * 访问首页，提示语
	 */
	@RequestMapping("/")
	public String index() {
		return StringHelper.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", mispConfig.getName(), mispConfig.getVersion());
	}
}
