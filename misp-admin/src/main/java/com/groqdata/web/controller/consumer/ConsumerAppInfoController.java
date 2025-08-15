package com.groqdata.web.controller.consumer;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groqdata.common.annotation.Log;
import com.groqdata.common.core.controller.BaseController;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.core.page.TableDataInfo;
import com.groqdata.common.enums.ApplicationStatus;
import com.groqdata.common.enums.AuditStatus;
import com.groqdata.common.enums.BusinessType;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.consumer.domain.ConsumerAppInfo;
import com.groqdata.consumer.service.ConsumerAppInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 应用信息Controller
 * 
 * @author carfield
 * @date 2025-05-21
 */
@RestController
@Api(tags = "应用信息", value = "应用信息管理")
@RequestMapping("/consumer/app-info")
public class ConsumerAppInfoController extends BaseController {

	private ConsumerAppInfoService consumerAppInfoService;

	@Autowired
	public void setConsumerAppInfoService(ConsumerAppInfoService consumerAppInfoService) {
		this.consumerAppInfoService = consumerAppInfoService;
	}

	/**
	 * 查询应用信息列表
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:list')")
	@ApiOperation("查询应用信息列表")
	@GetMapping("/list")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
	})
	public TableDataInfo list(ConsumerAppInfo consumerAppInfo) {
		startPage();
		List<ConsumerAppInfo> list = consumerAppInfoService.selectConsumerAppInfoList(consumerAppInfo);
		return getDataTable(list);
	}

	/**
	 * 导出应用信息列表
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:export')")
	@ApiOperation("导出应用信息列表")
	@Log(title = "应用信息", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(HttpServletResponse response, ConsumerAppInfo consumerAppInfo) {
		List<ConsumerAppInfo> list = consumerAppInfoService.selectConsumerAppInfoList(consumerAppInfo);
		ExcelUtil<ConsumerAppInfo> util = new ExcelUtil<ConsumerAppInfo>(ConsumerAppInfo.class);
		util.exportExcel(response, list, "应用信息数据");
	}

	/**
	 * 获取应用信息详细信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:query')")
	@ApiOperation("获取应用信息详细信息")
	@ApiImplicitParam(name = "id", value = "应用信息主键", required = true, dataType = "Long", paramType = "path")
	@GetMapping(value = "/{id}")
	public AjaxResult getInfo(@PathVariable("id") Long id) {
		return success(consumerAppInfoService.selectConsumerAppInfoById(id));
	}

	/**
	 * 新增应用信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:add')")
	@ApiOperation("新增应用信息")
	@Log(title = "应用信息", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody ConsumerAppInfo consumerAppInfo) {
		consumerAppInfo.setCreateBy(getUsername());
		return toAjax(consumerAppInfoService.insertConsumerAppInfo(consumerAppInfo));
	}

	/**
	 * 修改应用信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:edit')")
	@ApiOperation("修改应用信息")
	@Log(title = "应用信息", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody ConsumerAppInfo consumerAppInfo) {
		consumerAppInfo.setUpdateBy(getUsername());
		return toAjax(consumerAppInfoService.updateConsumerAppInfo(consumerAppInfo));
	}

	/**
	 * 审核通过应用信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:enterprise-info:audit')")
	@ApiOperation("审核通过应用信息")
	@ApiImplicitParam(name = "id", value = "应用信息主键", required = true, dataType = "Long", paramType = "path")
	@PutMapping("/audit-pass/{id}")
	public AjaxResult auditPass(@PathVariable("id") Long id) {
		ConsumerAppInfo consumerAppInfo = consumerAppInfoService.selectConsumerAppInfoById(id);
		consumerAppInfo.setUpdateBy(getUsername());
		consumerAppInfo.setAuditStatus(AuditStatus.APPROVED.getCode());
		consumerAppInfo.setAppStatus(ApplicationStatus.NORMAL.getCode());
		return toAjax(consumerAppInfoService.updateConsumerAppInfo(consumerAppInfo));
	}

	/**
	 * 审核驳回应用信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:enterprise-info:audit')")
	@ApiOperation("审核驳回应用信息")
	@ApiImplicitParam(name = "id", value = "应用信息主键", required = true, dataType = "Long", paramType = "path")
	@PutMapping("/audit-reject/{id}")
	public AjaxResult auditReject(@PathVariable("id") Long id) {
		ConsumerAppInfo consumerAppInfo = consumerAppInfoService.selectConsumerAppInfoById(id);
		consumerAppInfo.setUpdateBy(getUsername());
		consumerAppInfo.setAuditStatus(AuditStatus.REJECTED.getCode());
		return toAjax(consumerAppInfoService.updateConsumerAppInfo(consumerAppInfo));
	}

	/**
	 * 删除应用信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:app-info:remove')")
	@ApiOperation("删除应用信息")
	@ApiImplicitParam(name = "ids", value = "应用信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
	@Log(title = "应用信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@PathVariable Long[] ids) {
		return toAjax(consumerAppInfoService.deleteConsumerAppInfoByIds(ids));
	}
}
