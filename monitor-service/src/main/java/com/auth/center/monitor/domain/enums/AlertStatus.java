package com.auth.center.monitor.domain.enums;

/**
 * 告警状态枚举
 */
public enum AlertStatus {
    
    /**
     * 活跃
     */
    ACTIVE("ACTIVE", "活跃"),
    
    /**
     * 已处理
     */
    HANDLED("HANDLED", "已处理"),
    
    /**
     * 已恢复
     */
    RECOVERED("RECOVERED", "已恢复");
    
    private final String code;
    private final String description;
    
    AlertStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AlertStatus fromCode(String code) {
        for (AlertStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid alert status code: " + code);
    }
}