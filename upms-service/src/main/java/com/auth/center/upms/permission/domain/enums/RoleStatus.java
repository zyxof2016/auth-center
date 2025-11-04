package com.auth.center.role.domain.enums;

/**
 * 角色状态枚举
 */
public enum RoleStatus {
    
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
    
    RoleStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RoleStatus fromCode(int code) {
        for (RoleStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid role status code: " + code);
    }
}