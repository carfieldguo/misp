package com.groqdata.common.enums;

/**
 * 应用状态枚举类
 * 对应数据库字典类型: application_status
 */
public enum ApplicationStatus {
    
    /** 已创建 - 应用基本信息已填写并保存，但未提交审核 */
    CREATED("Created", "已创建", "应用基本信息已填写并保存，但未提交审核（仅开发者可见，不可调用接口）"),
    
    /** 审核通过 - 应用通过平台审核，可正式调用开放接口 */
    NORMAL("Normal", "正常可用", "应用通过平台审核，可正式调用开放接口"),

    /** 已注销 - 开发者主动删除应用（不可恢复） */
    CLOSED("Closed", "已注销", "开发者主动删除应用（不可恢复）"),
    
    /** 禁用 - 暂停应用功能（如接口调用权限冻结，通常用于安全事件） */
    DISABLED("Disabled", "禁用", "暂停应用功能（如接口调用权限冻结，通常用于安全事件）"),
    
    /** 冻结 - 因未支付接口调用费用被冻结，充值后可恢复 */
    FROZEN("Frozen", "冻结", "因未支付接口调用费用（如超出免费额度）被冻结，充值后可恢复");
    
    private final String code;
    private final String label;
    private final String desc;
    
    ApplicationStatus(String code, String label, String desc) {
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
    public static ApplicationStatus fromCode(String code) {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的应用状态code: " + code);
    }
}
