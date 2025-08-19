package com.groqdata.web.controller.monitor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.framework.web.domain.Server;

/**
 * 服务器监控
 * 
 * @author MISP TEAM
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
	@PreAuthorize("@ss.hasPermit('monitor:server:list')")
	@GetMapping()
	public AjaxResult getInfo() {
		Server server = new Server();
		server.copyTo();
		return AjaxResult.success(server);
	}
}
