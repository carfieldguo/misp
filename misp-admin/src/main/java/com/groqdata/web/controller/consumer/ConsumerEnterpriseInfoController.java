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
import com.groqdata.consumer.domain.ConsumerEnterpriseInfo;
import com.groqdata.consumer.service.ConsumerEnterpriseInfoService;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.common.core.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 服务购买方-企业信息Controller
 * 
 * @author carfield
 * @date 2025-05-21
 */
@RestController
@Api(tags = "服务购买方-企业信息", value = "服务购买方-企业信息管理")
@RequestMapping("/consumer/consumer-enterprise-info")
public class ConsumerEnterpriseInfoController extends BaseController
{
    @Autowired
    private ConsumerEnterpriseInfoService consumerEnterpriseInfoService;

    /**
     * 查询服务购买方-企业信息列表
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:list')")
    @ApiOperation("查询服务购买方-企业信息列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数",   defaultValue = "10"),
    })
    public TableDataInfo list(ConsumerEnterpriseInfo consumerEnterpriseInfo)
    {
        startPage();
        List<ConsumerEnterpriseInfo> list = consumerEnterpriseInfoService.selectConsumerEnterpriseInfoList(consumerEnterpriseInfo);
        return getDataTable(list);
    }

    /**
     * 导出服务购买方-企业信息列表
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:export')")
    @ApiOperation("导出服务购买方-企业信息列表")
    @Log(title = "服务购买方-企业信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConsumerEnterpriseInfo consumerEnterpriseInfo)
    {
        List<ConsumerEnterpriseInfo> list = consumerEnterpriseInfoService.selectConsumerEnterpriseInfoList(consumerEnterpriseInfo);
        ExcelUtil<ConsumerEnterpriseInfo> util = new ExcelUtil<ConsumerEnterpriseInfo>(ConsumerEnterpriseInfo.class);
        util.exportExcel(response, list, "服务购买方-企业信息数据");
    }

    /**
     * 获取服务购买方-企业信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:query')")
    @ApiOperation("获取服务购买方-企业信息详细信息")
    @ApiImplicitParam(name = "id", value = "服务购买方-企业信息主键", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(consumerEnterpriseInfoService.selectConsumerEnterpriseInfoById(id));
    }

    /**
     * 获取服务购买方-账号信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('provider:provider-user-info:query')")
    @ApiOperation("根据账号获取服务购买方-企业信息详细信息")
    @ApiImplicitParam(name = "account", value = "服务服务购买方账号信息", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/find-by-account/{account}")
    public AjaxResult getInfoByAccount(@PathVariable("account") String account)
    {
        return success(consumerEnterpriseInfoService.selectConsumerUserInfoByAccount(account));
    }
    
    /**
     * 新增服务购买方-企业信息
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:add')")
    @ApiOperation("新增服务购买方-企业信息")
    @Log(title = "服务购买方-企业信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ConsumerEnterpriseInfo consumerEnterpriseInfo)
    {
    	consumerEnterpriseInfo.setCreateBy(getUsername());
        return toAjax(consumerEnterpriseInfoService.insertConsumerEnterpriseInfo(consumerEnterpriseInfo));
    }

    /**
     * 修改服务购买方-企业信息
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:edit')")
    @ApiOperation("修改服务购买方-企业信息")
    @Log(title = "服务购买方-企业信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ConsumerEnterpriseInfo consumerEnterpriseInfo)
    {
        consumerEnterpriseInfo.setUpdateBy(getUsername());
        return toAjax(consumerEnterpriseInfoService.updateConsumerEnterpriseInfo(consumerEnterpriseInfo));
    }

    /**
     * 删除服务购买方-企业信息
     */
    @PreAuthorize("@ss.hasPermi('consumer:consumer-enterprise-info:remove')")
    @ApiOperation("删除服务购买方-企业信息")
    @ApiImplicitParam(name = "ids", value = "服务购买方-企业信息主键集合，以逗号分隔的数组", required = true, dataType = "Long", paramType = "path")
    @Log(title = "服务购买方-企业信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(consumerEnterpriseInfoService.deleteConsumerEnterpriseInfoByIds(ids));
    }
}
