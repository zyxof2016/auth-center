package com.auth.center.auth.domain.enums;

/**
 * 登录类型枚举
 */
public enum LoginType {
    
    /**
     * 用户名密码登录
     */
    USERNAME_PASSWORD("USERNAME", "用户名密码登录"),
    
    /**
     * 手机号密码登录
     */
    PHONE_PASSWORD("PHONE", "手机号密码登录"),
    
    /**
     * 邮箱密码登录
     */
    EMAIL_PASSWORD("EMAIL", "邮箱密码登录"),
    
    /**
     * 手机号验证码登录
     */
    PHONE_CODE("PHONE_CODE", "手机验证码登录"),
    
    /**
     * 微信登录
     */
    WECHAT("WECHAT", "微信登录"),
    
    /**
     * QQ登录
     */
    QQ("QQ", "QQ登录"),
    
    /**
     * 支付宝登录
     */
    ALIPAY("ALIPAY", "支付宝登录"),
    
    /**
     * GitHub登录
     */
    GITHUB("GITHUB", "GitHub登录"),
    
    /**
     * 第三方登录
     */
    THIRD_PARTY("THIRD_PARTY", "第三方登录");
    
    private final String code;
    private final String desc;
    
    LoginType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static LoginType getByCode(String code) {
        for (LoginType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 是否为密码登录类型
     */
    public boolean isPasswordType() {
        return this == USERNAME_PASSWORD || this == PHONE_PASSWORD || this == EMAIL_PASSWORD;
    }
    
    /**
     * 是否为第三方登录类型
     */
    public boolean isThirdPartyType() {
        return this == WECHAT || this == QQ || this == ALIPAY || this == GITHUB;
    }
}