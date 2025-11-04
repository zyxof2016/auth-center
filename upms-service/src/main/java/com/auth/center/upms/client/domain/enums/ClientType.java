package com.auth.center.client.domain.enums;

/**
 * 客户端类型枚举
 */
public enum ClientType {
    
    /**
     * Web应用
     */
    WEB(1, "Web应用"),
    
    /**
     * 移动应用
     */
    MOBILE(2, "移动应用"),
    
    /**
     * 第三方应用
     */
    THIRD_PARTY(3, "第三方应用");
    
    private final int code;
    private final String description;
    
    ClientType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ClientType fromCode(int code) {
        for (ClientType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid client type code: " + code);
    }
}