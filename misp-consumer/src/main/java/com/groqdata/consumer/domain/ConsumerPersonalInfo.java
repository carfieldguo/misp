package com.groqdata.consumer.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.groqdata.common.annotation.Excel;
import com.groqdata.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 个人信息对象 consumer_personal_info
 * 
 * @author carfield
 * @date 2025-05-22
 */
@ApiModel("个人信息对象")
public class ConsumerPersonalInfo extends BaseEntity {
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

	/** 关联服务购买方账号 */
	@Excel(name = "关联服务购买方账号")
	@ApiModelProperty("关联服务购买方账号")
	private String consumerAccount;

	/** 账号名称 */
	@Excel(name = "账号名称")
	@ApiModelProperty("账号名称")
	private String consumerNickname;

	/** 信息审核状态 */
	@Excel(name = "信息审核状态")
	@ApiModelProperty("信息审核状态")
	private String auditStatus;

	/** 记录状态 */
	@Excel(name = "记录状态")
	@ApiModelProperty("记录状态")
	private String recordStatus;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardType() {
		return cardType;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardNo() {
		return cardNo;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPersonName() {
		return personName;
	}
	public void setConsumerAccount(String consumerAccount) {
		this.consumerAccount = consumerAccount;
	}

	public String getConsumerAccount() {
		return consumerAccount;
	}

	public String getConsumerNickname() {
		return consumerNickname;
	}

	public void setConsumerNickname(String consumerNickname) {
		this.consumerNickname = consumerNickname;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAuditStatus() {
		return auditStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("id", getId())
			.append("cardType", getCardType())
			.append("cardNo", getCardNo())
			.append("personName", getPersonName())
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
