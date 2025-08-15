package com.groqdata.common.enums;

/**
 * 数据审核状态枚举类
 * 对应数据库字典类型: audit_status
 */
public enum AuditStatus {

	/** 待审核 - 已提交但未开始审核 */
	PENDING("Pending", "待审核", "已提交但未开始审核"),

	/** 审核通过 - 审核通过，数据可用 */
	APPROVED("Approved", "审核通过", "审核通过，数据可用"),

	/** 审核驳回 - 未通过审核，需修改重提 */
	REJECTED("Rejected", "审核驳回", "未通过审核，需修改重提");

	private final String code;
	private final String label;
	private final String desc;

	AuditStatus(String code, String label, String desc) {
		this.code = code;
		this.label = label;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 根据code获取枚举实例
	 */
	public static AuditStatus fromCode(String code) {
		for (AuditStatus status : AuditStatus.values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		throw new IllegalArgumentException("未知的审核状态code: " + code);
	}
}
