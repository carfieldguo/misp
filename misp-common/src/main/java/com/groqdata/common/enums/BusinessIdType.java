package com.groqdata.common.enums;

/**
 * 企业认证证件类型枚举类
 * 对应数据库字典类型: business_id_type
 */
public enum BusinessIdType {
    
    /** 营业执照 - 企业合法经营的法定凭证 */
    BUSINESS_LICENSE("BusinessLicense", "营业执照", "企业合法经营的法定凭证（含统一社会信用代码），记载企业名称、类型、经营范围等信息，分 “正本” 和 “副本”"),
    
    /** 事业单位法人证书 - 事业单位的合法身份证明 */
    PUBLIC_INSTITUTION_CERTIFICATE("PublicInstitutionCertificate", "事业单位法人证书", "事业单位的合法身份证明，由事业单位登记管理机关核发，用于非营利性组织业务"),
    
    /** 社会团体法人登记证书 - 社会团体的合法登记凭证 */
    SOCIAL_ORGANIZATION_CERTIFICATE("SocialOrganizationCertificate", "社会团体法人登记证书", "社会团体（如协会、基金会）的合法登记凭证，需经民政部门批准");
    
    private final String code;
    private final String label;
    private final String desc;
    
    BusinessIdType(String code, String label, String desc) {
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
    public static BusinessIdType fromCode(String code) {
        for (BusinessIdType type : BusinessIdType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的企业证件类型code: " + code);
    }
}
