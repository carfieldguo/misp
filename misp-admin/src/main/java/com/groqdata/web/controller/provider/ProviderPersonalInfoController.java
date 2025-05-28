package com.groqdata.web.controller.provider;

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
import com.groqdata.provider.domain.ProviderPersonalInfo;
import com.groqdata.provider.service.ProviderPersonalInfoService;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.common.core.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 服务提供方-个人信息Controller
 * 
 * @author carfield
 * @date 2025-05-21
 */
@RestController
@Api(tags = "服务提供方-个人信息", value = "服务提供方-个人信息管理")
@RequestMapping("/provider/provider-personal-info")
public class ProviderPersonalInfoController extends BaseController
{
    @Autowired
    private ProviderPersonalInfoService providerPersonalInfoService;

    /**
     * 查询服务提供方-个人信息列表
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:list')")
    @ApiOperation("查询服务提供方-个人信息列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数",   defaultValue = "10"),
    })
    public TableDataInfo list(ProviderPersonalInfo providerPersonalInfo)
    {
        startPage();
        List<ProviderPersonalInfo> list = providerPersonalInfoService.selectProviderPersonalInfoList(providerPersonalInfo);
        return getDataTable(list);
    }

    /**
     * 导出服务提供方-个人信息列表
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:export')")
    @ApiOperation("导出服务提供方-个人信息列表")
    @Log(title = "服务提供方-个人信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProviderPersonalInfo providerPersonalInfo)
    {
        List<ProviderPersonalInfo> list = providerPersonalInfoService.selectProviderPersonalInfoList(providerPersonalInfo);
        ExcelUtil<ProviderPersonalInfo> util = new ExcelUtil<ProviderPersonalInfo>(ProviderPersonalInfo.class);
        util.exportExcel(response, list, "服务提供方-个人信息数据");
    }

    /**
     * 获取服务提供方-个人信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:query')")
    @ApiOperation("获取服务提供方-个人信息详细信息")
    @ApiImplicitParam(name = "id", value = "服务提供方-个人信息主键", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(providerPersonalInfoService.selectProviderPersonalInfoById(id));
    }

    /**
     * 获取服务提供方-账号信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-user-info:query')")
    @ApiOperation("根据账号获取服务提供方-个人信息详细信息")
    @ApiImplicitParam(name = "account", value = "服务提供方账号信息", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/find-by-account/{account}")
    public AjaxResult getInfoByAccount(@PathVariable("account") String account)
    {
        return success(providerPersonalInfoService.selectProviderUserInfoByAccount(account));
    }
    
    /**
     * 新增服务提供方-个人信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:add')")
    @ApiOperation("新增服务提供方-个人信息")
    @Log(title = "服务提供方-个人信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProviderPersonalInfo providerPersonalInfo)
    {
    	providerPersonalInfo.setCreateBy(getUsername());
        return toAjax(providerPersonalInfoService.insertProviderPersonalInfo(providerPersonalInfo));
    }

    /**
     * 修改服务提供方-个人信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:edit')")
    @ApiOperation("修改服务提供方-个人信息")
    @Log(title = "服务提供方-个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProviderPersonalInfo providerPersonalInfo)
    {
        providerPersonalInfo.setUpdateBy(getUsername());
        return toAjax(providerPersonalInfoService.updateProviderPersonalInfo(providerPersonalInfo));
    }

    /**
     * 删除服务提供方-个人信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-personal-info:remove')")
    @ApiOperation("删除服务提供方-个人信息")
    @ApiImplicitParam(name = "ids", value = "服务提供方-个人信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
    @Log(title = "服务提供方-个人信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(providerPersonalInfoService.deleteProviderPersonalInfoByIds(ids));
    }
}
