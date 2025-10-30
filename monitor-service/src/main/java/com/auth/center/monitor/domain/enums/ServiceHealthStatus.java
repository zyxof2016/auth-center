package com.auth.center.monitor.domain.enums;

/**
 * 服务健康状态枚举
 */
public enum ServiceHealthStatus {
    
    /**
     * 健康
     */
    HEALTHY("HEALTHY", "健康"),
    
    /**
     * 警告
     */
    WARNING("WARNING", "警告"),
    
    /**
     * 错误
     */
    ERROR("ERROR", "错误"),
    
    /**
     * 未知
     */
    UNKNOWN("UNKNOWN", "未知");
    
    private final String code;
    private final String description;
    
    ServiceHealthStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ServiceHealthStatus fromCode(String code) {
        for (ServiceHealthStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid service health status code: " + code);
    }
}