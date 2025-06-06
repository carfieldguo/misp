package com.groqdata.common.enums;
/**
 * 账号状态枚举类
 * 对应数据库字典类型: account_status
 */
public enum AccountStatus {
    
    /** 未激活 - 账号已创建但未完成激活流程 */
    UNACTIVATED("Unactivated", "未激活", "账号已创建但未完成激活流程（如邮箱验证、手机短信验证）"),
    
    /** 已激活 - 完成所有注册验证，账号正常可用 */
    ACTIVATED("Activated", "已激活", "完成所有注册验证，账号正常可用"),
    
    /** 已删除 - 账号被动注销，永久失效 */
    DELETED("Deleted", "已删除", "账号被动注销，永久失效（不可恢复）"),
    
    /** 已注销 - 账号主动注销，永久失效 */
    CLOSED("Closed", "已注销", "账号主动注销，永久失效（不可恢复）"),
    
    /** 冻结 - 账号因违规、安全风险等原因被临时锁定 */
    FROZEN("Frozen", "冻结", "账号因违规、安全风险等原因被临时锁定，暂停登录和操作"),
    
    /** 锁定 - 通常因多次输错密码触发，需重置密码后解锁 */
    LOCKED("Locked", "锁定", "通常因多次输错密码触发，需重置密码后解锁");
    
    private final String code;
    private final String label;
    private final String desc;
    
    AccountStatus(String code, String label, String desc) {
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
    public static AccountStatus fromCode(String code) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的账号状态code: " + code);
    }
    
}

