package com.auth.center.client.domain.enums;

/**
 * 客户端状态枚举
 */
public enum ClientStatus {
    
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
    
    ClientStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ClientStatus fromCode(int code) {
        for (ClientStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid client status code: " + code);
    }
}