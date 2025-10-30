package com.auth.center.common.web.exception;

import lombok.Getter;

/**
 * 认证异常 - 公共异常类
 */
@Getter
public class AuthException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final String errorCode;
    
    /**
     * 错误消息
     */
    private final String errorMessage;
    
    public AuthException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public AuthException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 登录失败异常
     */
    public static AuthException loginFailed(String message) {
        return new AuthException("AUTH_LOGIN_FAILED", message);
    }
    
    /**
     * 验证码错误异常
     */
    public static AuthException verificationCodeError() {
        return new AuthException("AUTH_VERIFICATION_CODE_ERROR", "验证码错误或已过期");
    }
    
    /**
     * 验证码发送频繁异常
     */
    public static AuthException verificationCodeFrequent() {
        return new AuthException("AUTH_VERIFICATION_CODE_FREQUENT", "验证码发送过于频繁，请稍后再试");
    }
    
    /**
     * 第三方登录失败异常
     */
    public static AuthException thirdPartyLoginFailed(String message) {
        return new AuthException("AUTH_THIRD_PARTY_LOGIN_FAILED", message);
    }
    
    /**
     * 账号被禁用异常
     */
    public static AuthException accountDisabled() {
        return new AuthException("AUTH_ACCOUNT_DISABLED", "账号已被禁用，请联系管理员");
    }
    
    /**
     * 密码错误异常
     */
    public static AuthException passwordError() {
        return new AuthException("AUTH_PASSWORD_ERROR", "用户名或密码错误");
    }
    
    /**
     * 账号不存在异常
     */
    public static AuthException accountNotFound() {
        return new AuthException("AUTH_ACCOUNT_NOT_FOUND", "账号不存在");
    }
    
    /**
     * 第三方账号已绑定异常
     */
    public static AuthException thirdPartyAccountAlreadyBound() {
        return new AuthException("AUTH_THIRD_PARTY_ACCOUNT_ALREADY_BOUND", "该第三方账号已被其他用户绑定");
    }
    
    /**
     * 第三方账号未绑定异常
     */
    public static AuthException thirdPartyAccountNotBound() {
        return new AuthException("AUTH_THIRD_PARTY_ACCOUNT_NOT_BOUND", "该第三方账号未绑定");
    }
}