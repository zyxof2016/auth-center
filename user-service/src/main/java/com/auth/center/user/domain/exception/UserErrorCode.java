package com.auth.center.user.domain.exception;

/**
 * 用户服务错误码
 */
public enum UserErrorCode {
    
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS("USER_001", "用户名已存在"),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND("USER_002", "用户不存在"),
    
    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS("USER_003", "邮箱已存在"),
    
    /**
     * 手机号已存在
     */
    PHONE_EXISTS("USER_004", "手机号已存在"),
    
    /**
     * 用户已被禁用
     */
    USER_DISABLED("USER_005", "用户已被禁用"),
    
    /**
     * 密码错误
     */
    PASSWORD_ERROR("USER_006", "密码错误"),
    
    /**
     * 登录失败次数过多
     */
    LOGIN_FAIL_TOO_MANY("USER_007", "登录失败次数过多");
    
    private final String code;
    private final String message;
    
    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}