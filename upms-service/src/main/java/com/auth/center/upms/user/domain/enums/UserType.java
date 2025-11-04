package com.auth.center.user.domain.enums;

/**
 * 用户类型枚举
 */
public enum UserType {
    
    /**
     * 普通用户
     */
    NORMAL(1, "普通用户"),

    /**
     * 第三方用户
     */
    THIRD_PARTY(2, "第三方用户");

    private final int code;
    private final String description;
    
    UserType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static UserType fromCode(int code) {
        for (UserType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid user type code: " + code);
    }
}