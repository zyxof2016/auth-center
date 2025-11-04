package com.auth.center.role.domain.enums;

/**
 * 租户状态枚举
 */
public enum TenantStatus {
    
    /**
     * 禁用
     */
    DISABLED(0, "禁用"),
    
    /**
     * 启用
     */
    ENABLED(1, "启用");
    
    private final int code;
    private final String description;
    
    TenantStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static TenantStatus fromCode(int code) {
        for (TenantStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid tenant status code: " + code);
    }
}