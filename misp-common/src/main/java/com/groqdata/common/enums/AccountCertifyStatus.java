package com.groqdata.common.enums;

/**
 * 账号认证状态枚举类
 * 对应数据库字典类型: account_certify_status
 */
public enum AccountCertifyStatus {
    
    /** 未认证 - 账号未提交任何认证信息或未开始认证流程 */
    UNCERTIFIED("Uncertified", "未认证", "账号未提交任何认证信息或未开始认证流程"),
    
    /** 认证通过 - 所有认证信息审核通过，账号具备完整权限 */
    CERTIFIED("Certified", "认证通过", "所有认证信息审核通过，账号具备完整权限（如实名认证、企业认证）"),
    
    /** 认证驳回 - 因信息不符、材料缺失等原因未通过认证，需重新提交 */
    REJECTED("Rejected", "认证驳回", "因信息不符、材料缺失等原因未通过认证，需重新提交");
    
    private final String code;
    private final String label;
    private final String desc;
    
    AccountCertifyStatus(String code, String label, String desc) {
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
    public static AccountCertifyStatus fromCode(String code) {
        for (AccountCertifyStatus status : AccountCertifyStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的认证状态code: " + code);
    }
}


