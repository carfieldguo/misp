package com.groqdata.web.controller.provider;

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
import com.groqdata.common.enums.AuditStatus;
import com.groqdata.common.enums.BusinessType;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.provider.domain.ProviderEnterpriseInfo;
import com.groqdata.provider.service.ProviderEnterpriseInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 企业信息Controller
 * 
 * @author carfield
 * @date 2025-05-21
 */
@RestController
@Api(tags = "企业信息", value = "企业信息管理")
@RequestMapping("/provider/enterprise-info")
public class ProviderEnterpriseInfoController extends BaseController {

	private ProviderEnterpriseInfoService providerEnterpriseInfoService;

	@Autowired
	public void setProviderEnterpriseInfoService(ProviderEnterpriseInfoService providerEnterpriseInfoService) {
		this.providerEnterpriseInfoService = providerEnterpriseInfoService;
	}

	/**
	 * 查询企业信息列表
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:list')")
	@ApiOperation("查询企业信息列表")
	@GetMapping("/list")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
			@ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
	})
	public TableDataInfo list(ProviderEnterpriseInfo providerEnterpriseInfo) {
		startPage();
		List<ProviderEnterpriseInfo> list = providerEnterpriseInfoService
			.selectProviderEnterpriseInfoList(providerEnterpriseInfo);
		return getDataTable(list);
	}

	/**
	 * 导出企业信息列表
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:export')")
	@ApiOperation("导出企业信息列表")
	@Log(title = "企业信息", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(HttpServletResponse response, ProviderEnterpriseInfo providerEnterpriseInfo) {
		List<ProviderEnterpriseInfo> list = providerEnterpriseInfoService
			.selectProviderEnterpriseInfoList(providerEnterpriseInfo);
		ExcelUtil<ProviderEnterpriseInfo> util = new ExcelUtil<ProviderEnterpriseInfo>(ProviderEnterpriseInfo.class);
		util.exportExcel(response, list, "企业信息数据");
	}

	/**
	 * 获取企业信息详细信息
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:query')")
	@ApiOperation("获取企业信息详细信息")
	@ApiImplicitParam(name = "id", value = "企业信息主键", required = true, dataType = "Long", paramType = "path")
	@GetMapping(value = "/{id}")
	public AjaxResult getInfo(@PathVariable("id") Long id) {
		return success(providerEnterpriseInfoService.selectProviderEnterpriseInfoById(id));
	}

	/**
	 * 获取账号信息详细信息
	 */
	@PreAuthorize("@ss.hasPermi('provider:user-info:query')")
	@ApiOperation("根据账号获取企业信息详细信息")
	@ApiImplicitParam(name = "account", value = "服务提供方账号信息", required = true, dataType = "String", paramType = "path")
	@GetMapping(value = "/find-by-account/{account}")
	public AjaxResult getInfoByAccount(@PathVariable("account") String account) {
		return success(providerEnterpriseInfoService.selectProviderUserInfoByAccount(account));
	}

	/**
	 * 新增企业信息
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:add')")
	@ApiOperation("新增企业信息")
	@Log(title = "企业信息", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody ProviderEnterpriseInfo providerEnterpriseInfo) {
		providerEnterpriseInfo.setCreateBy(getUsername());
		return toAjax(providerEnterpriseInfoService.insertProviderEnterpriseInfo(providerEnterpriseInfo));
	}

	/**
	 * 修改企业信息
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:edit')")
	@ApiOperation("修改企业信息")
	@Log(title = "企业信息", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody ProviderEnterpriseInfo providerEnterpriseInfo) {
		providerEnterpriseInfo.setUpdateBy(getUsername());
		return toAjax(providerEnterpriseInfoService.updateProviderEnterpriseInfo(providerEnterpriseInfo));
	}

	/**
	 * 审核通过企业信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:enterprise-info:audit')")
	@ApiOperation("审核通过企业信息")
	@ApiImplicitParam(name = "id", value = "企业信息主键", required = true, dataType = "Long", paramType = "path")
	@PutMapping("/audit-pass/{id}")
	public AjaxResult auditPass(@PathVariable("id") Long id) {
		ProviderEnterpriseInfo providerEnterpriseInfo = providerEnterpriseInfoService
			.selectProviderEnterpriseInfoById(id);
		providerEnterpriseInfo.setUpdateBy(getUsername());
		providerEnterpriseInfo.setAuditStatus(AuditStatus.APPROVED.getCode());
		return toAjax(providerEnterpriseInfoService.updateProviderEnterpriseInfo(providerEnterpriseInfo));
	}

	/**
	 * 审核驳回企业信息
	 */
	@PreAuthorize("@ss.hasPermi('consumer:enterprise-info:audit')")
	@ApiOperation("审核驳回企业信息")
	@ApiImplicitParam(name = "id", value = "企业信息主键", required = true, dataType = "Long", paramType = "path")
	@PutMapping("/audit-reject/{id}")
	public AjaxResult auditReject(@PathVariable("id") Long id) {
		ProviderEnterpriseInfo providerEnterpriseInfo = providerEnterpriseInfoService
			.selectProviderEnterpriseInfoById(id);
		providerEnterpriseInfo.setUpdateBy(getUsername());
		providerEnterpriseInfo.setAuditStatus(AuditStatus.REJECTED.getCode());
		return toAjax(providerEnterpriseInfoService.updateProviderEnterpriseInfo(providerEnterpriseInfo));
	}

	/**
	 * 删除企业信息
	 */
	@PreAuthorize("@ss.hasPermi('provider:enterprise-info:remove')")
	@ApiOperation("删除企业信息")
	@ApiImplicitParam(name = "ids", value = "企业信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
	@Log(title = "企业信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@PathVariable Long[] ids) {
		return toAjax(providerEnterpriseInfoService.deleteProviderEnterpriseInfoByIds(ids));
	}
}
