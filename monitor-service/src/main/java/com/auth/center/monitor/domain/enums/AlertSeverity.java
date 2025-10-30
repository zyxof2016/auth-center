package com.auth.center.monitor.domain.enums;

/**
 * 告警级别枚举
 */
public enum AlertSeverity {
    
    /**
     * 信息
     */
    INFO("INFO", "信息"),
    
    /**
     * 警告
     */
    WARNING("WARNING", "警告"),
    
    /**
     * 错误
     */
    ERROR("ERROR", "错误"),
    
    /**
     * 严重
     */
    CRITICAL("CRITICAL", "严重");
    
    private final String code;
    private final String description;
    
    AlertSeverity(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AlertSeverity fromCode(String code) {
        for (AlertSeverity severity : values()) {
            if (severity.code.equals(code)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Invalid alert severity code: " + code);
    }
}