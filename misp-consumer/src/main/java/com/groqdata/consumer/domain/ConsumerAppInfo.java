package com.groqdata.consumer.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.groqdata.common.annotation.Excel;
import com.groqdata.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务购买方-应用信息对象 consumer_app_info
 * 
 * @author carfield
 * @date 2025-05-21
 */
@ApiModel("服务购买方-应用信息对象")
public class ConsumerAppInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty("主键ID")
    private Long id;

    /** 应用ID */
    @Excel(name = "应用ID")
    @ApiModelProperty("应用ID")
    private String appId;

    /** 应用名称 */
    @Excel(name = "应用名称")
    @ApiModelProperty("应用名称")
    private String appName;

    /** 应用描述 */
    @Excel(name = "应用描述")
    @ApiModelProperty("应用描述")
    private String appDesc;

    /** 对接秘钥 */
    @Excel(name = "对接秘钥")
    @ApiModelProperty("对接秘钥")
    private String appSecret;

    /** 关联服务购买方账号，页面不显示此字段，只做鉴权用 */
    @Excel(name = "关联服务购买方账号，页面不显示此字段，只做鉴权用")
    @ApiModelProperty("关联服务购买方账号，页面不显示此字段，只做鉴权用")
    private String consumerAccount;

    /** 白名单IP */
    @Excel(name = "白名单IP")
    @ApiModelProperty("白名单IP")
    private String whiteIps;

    /** 应用状态 */
    @Excel(name = "应用状态")
    @ApiModelProperty("应用状态")
    private String appStatus;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAppId(String appId) 
    {
        this.appId = appId;
    }

    public String getAppId() 
    {
        return appId;
    }
    public void setAppName(String appName) 
    {
        this.appName = appName;
    }

    public String getAppName() 
    {
        return appName;
    }
    public void setAppDesc(String appDesc) 
    {
        this.appDesc = appDesc;
    }

    public String getAppDesc() 
    {
        return appDesc;
    }
    public void setAppSecret(String appSecret) 
    {
        this.appSecret = appSecret;
    }

    public String getAppSecret() 
    {
        return appSecret;
    }
    public void setConsumerAccount(String consumerAccount) 
    {
        this.consumerAccount = consumerAccount;
    }

    public String getConsumerAccount() 
    {
        return consumerAccount;
    }
    public void setWhiteIps(String whiteIps) 
    {
        this.whiteIps = whiteIps;
    }

    public String getWhiteIps() 
    {
        return whiteIps;
    }
    public void setAppStatus(String appStatus) 
    {
        this.appStatus = appStatus;
    }

    public String getAppStatus() 
    {
        return appStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("appId", getAppId())
            .append("appName", getAppName())
            .append("appDesc", getAppDesc())
            .append("appSecret", getAppSecret())
            .append("consumerAccount", getConsumerAccount())
            .append("whiteIps", getWhiteIps())
            .append("appStatus", getAppStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
