package com.auth.center.user.domain.exception;

import com.auth.center.common.exception.ErrorCode;

/**
 * 用户模块错误码
 */
public enum UserErrorCode implements ErrorCode {
    
    USER_NOT_FOUND("USER_001", "用户不存在"),
    USERNAME_EXISTS("USER_002", "用户名已存在"),
    EMAIL_EXISTS("USER_003", "邮箱已存在"),
    PHONE_EXISTS("USER_004", "手机号已存在"),
    INVALID_USER_INFO("USER_005", "用户信息无效"),
    USER_LOCKED("USER_006", "用户已被锁定"),
    USER_LOGIN_FAILED("USER_007", "用户登录失败"),
    USER_DISABLED("USER_008", "用户已被禁用"),
    INSUFFICIENT_PRIVILEGES("USER_009", "权限不足"),
    USER_ALREADY_EXISTS("USER_010", "用户已存在");
    
    private final String code;
    private final String message;
    
    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}