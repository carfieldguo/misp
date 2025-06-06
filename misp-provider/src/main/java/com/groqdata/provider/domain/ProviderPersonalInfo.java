package com.groqdata.provider.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.groqdata.common.annotation.Excel;
import com.groqdata.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 服务提供方-个人信息对象 provider_personal_info
 * 
 * @author carfield
 * @date 2025-05-22
 */
@ApiModel("服务提供方-个人信息对象")
public class ProviderPersonalInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty("主键ID")
    private Long id;

    /** 证件类型 */
    @Excel(name = "证件类型")
    @ApiModelProperty("证件类型")
    private String cardType;

    /** 证件号码 */
    @Excel(name = "证件号码")
    @ApiModelProperty("证件号码")
    private String cardNo;

    /** 证件名称 */
    @Excel(name = "证件名称")
    @ApiModelProperty("证件名称")
    private String personName;

    /** 关联服务提供方账号 */
    @Excel(name = "关联服务提供方账号")
    @ApiModelProperty("关联服务提供方账号")
    private String providerAccount;

    
    /** 账号名称 */
    @Excel(name = "账号名称")
    @ApiModelProperty("账号名称")
    private String providerNickname;
    
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
    public void setCardType(String cardType) 
    {
        this.cardType = cardType;
    }

    public String getCardType() 
    {
        return cardType;
    }
    public void setCardNo(String cardNo) 
    {
        this.cardNo = cardNo;
    }

    public String getCardNo() 
    {
        return cardNo;
    }
    public void setPersonName(String personName) 
    {
        this.personName = personName;
    }

    public String getPersonName() 
    {
        return personName;
    }
    public void setProviderAccount(String providerAccount) 
    {
        this.providerAccount = providerAccount;
    }

    public String getProviderAccount() 
    {
        return providerAccount;
    }
    
    public String getProviderNickname() {
		return providerNickname;
	}

	public void setProviderNickname(String providerNickname) {
		this.providerNickname = providerNickname;
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
            .append("cardType", getCardType())
            .append("cardNo", getCardNo())
            .append("personName", getPersonName())
            .append("providerAccount", getProviderAccount())
            .append("auditStatus", getAuditStatus())
            .append("recordStatus", getRecordStatus())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
