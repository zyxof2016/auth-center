package com.auth.center.log.domain.enums;

/**
 * 登录类型枚举
 */
public enum LoginType {
    
    /**
     * 密码登录
     */
    PASSWORD("PASSWORD", "密码登录"),
    
    /**
     * 邮箱密码登录
     */
    EMAIL_PASSWORD("EMAIL_PASSWORD", "邮箱密码登录"),
    
    /**
     * 手机号密码登录
     */
    PHONE_PASSWORD("PHONE_PASSWORD", "手机号密码登录"),
    
    /**
     * 手机验证码登录
     */
    PHONE_CODE("PHONE_CODE", "手机验证码登录"),
    
    /**
     * 微信登录
     */
    THIRD_WECHAT("THIRD_WECHAT", "微信登录"),
    
    /**
     * QQ登录
     */
    THIRD_QQ("THIRD_QQ", "QQ登录");
    
    private final String code;
    private final String description;
    
    LoginType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static LoginType fromCode(String code) {
        for (LoginType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid login type code: " + code);
    }
}