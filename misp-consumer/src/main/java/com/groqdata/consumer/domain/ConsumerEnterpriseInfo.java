package com.groqdata.consumer.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.groqdata.common.annotation.Excel;
import com.groqdata.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务购买方-企业信息对象 consumer_enterprise_info
 * 
 * @author carfield
 * @date 2025-05-22
 */
@ApiModel("服务购买方-企业信息对象")
public class ConsumerEnterpriseInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty("主键ID")
    private Long id;

    /** 企业名称 */
    @Excel(name = "企业名称")
    @ApiModelProperty("企业名称")
    private String entName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    @ApiModelProperty("统一信用代码")
    private String entUscc;

    /** 营业执照存储路径 */
    @Excel(name = "营业执照存储路径")
    @ApiModelProperty("营业执照存储路径")
    private String entLicenseUrl;

    /** 企业LOGO存储路径 */
    @Excel(name = "企业LOGO存储路径")
    @ApiModelProperty("企业LOGO存储路径")
    private String entLogoUrl;

    /** 关联服务购买方账号 */
    @Excel(name = "关联服务购买方账号")
    @ApiModelProperty("关联服务购买方账号")
    private String consumerAccount;

    /** 信息审核状态 */
    @Excel(name = "信息审核状态")
    @ApiModelProperty("信息审核状态")
    private String auditStatus;

    /** 记录状态 */
    @Excel(name = "记录状态")
    @ApiModelProperty("记录状态")
    private String recordStatus;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setEntName(String entName) 
    {
        this.entName = entName;
    }

    public String getEntName() 
    {
        return entName;
    }
    public void setEntUscc(String entUscc) 
    {
        this.entUscc = entUscc;
    }

    public String getEntUscc() 
    {
        return entUscc;
    }
    public void setEntLicenseUrl(String entLicenseUrl) 
    {
        this.entLicenseUrl = entLicenseUrl;
    }

    public String getEntLicenseUrl() 
    {
        return entLicenseUrl;
    }
    public void setEntLogoUrl(String entLogoUrl) 
    {
        this.entLogoUrl = entLogoUrl;
    }

    public String getEntLogoUrl() 
    {
        return entLogoUrl;
    }
    public void setConsumerAccount(String consumerAccount) 
    {
        this.consumerAccount = consumerAccount;
    }

    public String getConsumerAccount() 
    {
        return consumerAccount;
    }
    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }
    public void setRecordStatus(String recordStatus) 
    {
        this.recordStatus = recordStatus;
    }

    public String getRecordStatus() 
    {
        return recordStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("entName", getEntName())
            .append("entUscc", getEntUscc())
            .append("entLicenseUrl", getEntLicenseUrl())
            .append("entLogoUrl", getEntLogoUrl())
            .append("consumerAccount", getConsumerAccount())
            .append("auditStatus", getAuditStatus())
            .append("recordStatus", getRecordStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
