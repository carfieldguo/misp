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
import com.groqdata.provider.domain.ProviderUserInfo;
import com.groqdata.provider.service.ProviderUserInfoService;
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
@Api(tags = "服务提供方》账号信息", value = "账号信息管理")
@RequestMapping("/provider/user-info")
public class ProviderUserInfoController extends BaseController
{
    @Autowired
    private ProviderUserInfoService providerUserInfoService;

    /**
     * 查询账号信息列表
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:list')")
    @ApiOperation("查询账号信息列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数",   defaultValue = "10"),
    })
    public TableDataInfo list(ProviderUserInfo providerUserInfo)
    {
        startPage();
        List<ProviderUserInfo> list = providerUserInfoService.selectProviderUserInfoList(providerUserInfo);
        return getDataTable(list);
    }

    /**
     * 导出账号信息列表
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:export')")
    @ApiOperation("导出账号信息列表")
    @Log(title = "账号信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProviderUserInfo providerUserInfo)
    {
        List<ProviderUserInfo> list = providerUserInfoService.selectProviderUserInfoList(providerUserInfo);
        ExcelUtil<ProviderUserInfo> util = new ExcelUtil<ProviderUserInfo>(ProviderUserInfo.class);
        util.exportExcel(response, list, "账号信息数据");
    }

    /**
     * 获取账号信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:query')")
    @ApiOperation("获取账号信息详细信息")
    @ApiImplicitParam(name = "id", value = "账号信息主键", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(providerUserInfoService.selectProviderUserInfoById(id));
    }
    
    
    
    /**
     * 获取账号信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:query')")
    @ApiOperation("获取账号信息详细信息")
    @ApiImplicitParam(name = "id", value = "账号信息主键", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/find-by-account/{account}")
    public AjaxResult getInfoByAccount(@PathVariable("account") String account)
    {
        return success(providerUserInfoService.selectProviderUserInfoByAccount(account));
    }
    

    /**
     * 新增账号信息
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:add')")
    @ApiOperation("新增账号信息")
    @Log(title = "账号信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProviderUserInfo providerUserInfo)
    {
    	providerUserInfo.setCreateBy(getUsername());
        return toAjax(providerUserInfoService.insertProviderUserInfo(providerUserInfo));
    }

    /**
     * 修改账号信息
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:edit')")
    @ApiOperation("修改账号信息")
    @Log(title = "账号信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProviderUserInfo providerUserInfo)
    {
        providerUserInfo.setUpdateBy(getUsername());
        return toAjax(providerUserInfoService.updateProviderUserInfo(providerUserInfo));
    }

    /**
     * 删除账号信息
     */
    @PreAuthorize("@ss.hasPermi('provider:user-info:remove')")
    @ApiOperation("删除账号信息")
    @ApiImplicitParam(name = "ids", value = "账号信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
    @Log(title = "账号信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(providerUserInfoService.deleteProviderUserInfoByIds(ids));
    }
}
