package com.groqdata.common.enums;

/**
 * 个人身份证件类型枚举类
 * 对应数据库字典类型: personal_id_type
 */
public enum PersonalIdType {

	/** 居民身份证 - 中国大陆公民法定身份证件 */
	RESIDENT_ID_CARD("ResidentIDCard", "居民身份证", "中国大陆公民法定身份证件，18 位数字编码，含芯片存储个人信息"),

	/** 普通护照 - 各国公民出入境和在国外证明国籍身份的证件 */
	ORDINARY_PASSPORT("OrdinaryPassport", "普通护照", "各国公民出入境和在国外证明国籍身份的证件，含护照号、有效期等信息"),

	/** 外国人永居身份证 - 在中国境内永久居留的外国人持有的身份证件 */
	FOREIGNER_ID_CARD("ForeignerIDCard", "外国人永居身份证", "在中国境内永久居留的外国人持有的身份证件，简称 “永居证”，功能等同身份证"),

	/** 港澳台通行证 - 港澳地区居民的本地身份证件 */
	EXIT_ENTRY_PERMIT("ExitEntryPermit", "港澳台通行证", "港澳地区居民的本地身份证件，用于港澳本地事务，在大陆需配合回乡证使用"),

	/** 军官证 - 中国人民解放军现役军官的身份证件 */
	MILITARY_ID_CARD("MilitaryIDCard", "军官证", "中国人民解放军现役军官的身份证件，用于证明军人身份及相关权益"),

	/** 士兵证 - 中国人民解放军现役士兵的身份证件 */
	SOLDIER_ID_CARD("SoldierIDCard", "士兵证", "中国人民解放军现役士兵的身份证件，有效期内等同于居民身份证效力"),

	/** 户口簿 - 用于证明户籍身份，含户主页、个人页 */
	HOUSEHOLD_REGISTER("HouseholdRegister", "户口簿", "用于证明户籍身份，含户主页、个人页，常用于未成年人或无身份证场景");

	private final String code;
	private final String label;
	private final String desc;

	PersonalIdType(String code, String label, String desc) {
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
	public static PersonalIdType fromCode(String code) {
		for (PersonalIdType type : PersonalIdType.values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		throw new IllegalArgumentException("未知的身份证件类型code: " + code);
	}
}
