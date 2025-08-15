package com.groqdata.common.enums;

/**
 * 账号类型枚举类
 * 对应数据库字典类型: account_type
 */
public enum AccountType {

	/** 个人账号 */
	PERSONAL("personal", "个人账号", "个人账号"),

	/** 企业账号 */
	ENTERPRISE("enterprise", "企业账号", "企业账号");

	private final String code;
	private final String label;
	private final String desc;

	AccountType(String code, String label, String desc) {
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
	public static AccountType fromCode(String code) {
		for (AccountType type : AccountType.values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		throw new IllegalArgumentException("未知的账号类型code: " + code);
	}
}
