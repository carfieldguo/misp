package com.groqdata.common.enums;

/**
 * 数据记录状态枚举类
 * 对应数据库字典类型: record_status
 */
public enum RecordStatus {
    
    /** 正常可用 - 正常状态（默认值） */
    NORMAL("NORMAL", "正常可用", "正常状态（默认值）"),
    
    /** 已删除 - 已删除（逻辑删除标记） */
    DELETED("DELETED", "已删除", "已删除（逻辑删除标记）"),
    
    /** 已禁用 - 已禁用（暂时不可用） */
    DISABLED("DISABLED", "已禁用", "已禁用（暂时不可用）");
    
    private final String code;
    private final String label;
    private final String desc;
    
    RecordStatus(String code, String label, String desc) {
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
    public static RecordStatus fromCode(String code) {
        for (RecordStatus status : RecordStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的数据记录状态code: " + code);
    }
}
