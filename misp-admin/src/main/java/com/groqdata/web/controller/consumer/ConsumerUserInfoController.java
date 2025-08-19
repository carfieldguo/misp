package com.groqdata.web.controller.consumer;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.groqdata.common.annotation.Log;
import com.groqdata.common.core.controller.BaseController;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.enums.BusinessType;
import com.groqdata.consumer.domain.ConsumerUserInfo;
import com.groqdata.consumer.service.ConsumerUserInfoService;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.common.core.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 账号信息Controller
 * 
 * @author carfield
 * @date 2025-05-21
 */
@RestController
@Api(tags = "账号信息", value = "账号信息管理")
@RequestMapping("/consumer/user-info")
public class ConsumerUserInfoController extends BaseController {

	private ConsumerUserInfoService consumerUserInfoService;

	@Autowired
	public void setConsumerUserInfoService(ConsumerUserInfoService consumerUserInfoService) {
		this.consumerUserInfoService = consumerUserInfoService;
	}

	/**
	 * 查询账号信息列表
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:list')")
	@ApiOperation("查询账号信息列表")
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10"),
	})
	public TableDataInfo<ConsumerUserInfo> list(ConsumerUserInfo consumerUserInfo) {
		startPage();
		List<ConsumerUserInfo> list = consumerUserInfoService.selectConsumerUserInfoList(consumerUserInfo);
		return getDataTable(list);
	}

	/**
	 * 导出账号信息列表
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:export')")
	@ApiOperation("导出账号信息列表")
	@Log(title = "账号信息", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(HttpServletResponse response, ConsumerUserInfo consumerUserInfo) {
		List<ConsumerUserInfo> list = consumerUserInfoService.selectConsumerUserInfoList(consumerUserInfo);
		ExcelUtil<ConsumerUserInfo> util = new ExcelUtil<>(ConsumerUserInfo.class);
		util.exportExcel(response, list, "账号信息数据");
	}

	/**
	 * 获取账号信息详细信息
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:query')")
	@ApiOperation("获取账号信息详细信息")
	@ApiImplicitParam(name = "id", value = "账号信息主键", required = true, dataType = "Long", paramType = "path")
	@GetMapping(value = "/{id}")
	public AjaxResult getInfo(@PathVariable("id") Long id) {
		return success(consumerUserInfoService.selectConsumerUserInfoById(id));
	}

	/**
	 * 新增账号信息
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:add')")
	@ApiOperation("新增账号信息")
	@Log(title = "账号信息", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody ConsumerUserInfo consumerUserInfo) {
		consumerUserInfo.setCreateBy(getUsername());
		return toAjax(consumerUserInfoService.insertConsumerUserInfo(consumerUserInfo));
	}

	/**
	 * 修改账号信息
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:edit')")
	@ApiOperation("修改账号信息")
	@Log(title = "账号信息", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody ConsumerUserInfo consumerUserInfo) {
		consumerUserInfo.setUpdateBy(getUsername());
		return toAjax(consumerUserInfoService.updateConsumerUserInfo(consumerUserInfo));
	}

	/**
	 * 删除账号信息
	 */
	@PreAuthorize("@ss.hasPermit('consumer:user-info:remove')")
	@ApiOperation("删除账号信息")
	@ApiImplicitParam(name = "ids", value = "账号信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
	@Log(title = "账号信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@PathVariable Long[] ids) {
		return toAjax(consumerUserInfoService.deleteConsumerUserInfoByIds(ids));
	}
}
