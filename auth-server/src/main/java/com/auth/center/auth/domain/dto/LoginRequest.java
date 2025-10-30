package com.auth.center.auth.domain.dto;

import com.auth.center.auth.domain.enums.LoginType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    
    /**
     * 登录类型
     */
    @NotNull(message = "登录类型不能为空")
    private LoginType loginType;
    
    /**
     * 用户名（用于用户名密码登录）
     */
    private String username;
    
    /**
     * 手机号（用于手机号密码登录和验证码登录）
     */
    private String phone;
    
    /**
     * 邮箱（用于邮箱密码登录）
     */
    private String email;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 验证码（用于验证码登录）
     */
    private String code;
    
    /**
     * 业务ID（短信验证码返回的bizId）
     */
    private String bizId;
    
    /**
     * 第三方登录类型
     */
    private String thirdType;
    
    /**
     * 图形验证码
     */
    private String captcha;
    
    /**
     * 图形验证码ID
     */
    private String captchaId;
    
    /**
     * 第三方授权码（用于第三方登录）
     */
    private String authCode;
    
    /**
     * 第三方状态参数（用于第三方登录）
     */
    private String state;
    
    /**
     * 是否记住我
     */
    private Boolean rememberMe = false;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 验证登录参数
     */
    public void validate() {
        switch (loginType) {
            case USERNAME_PASSWORD:
                validateUsernamePassword();
                break;
            case PHONE_PASSWORD:
                validatePhonePassword();
                break;
            case EMAIL_PASSWORD:
                validateEmailPassword();
                break;
            case PHONE_CODE:
                validatePhoneCode();
                break;
            case WECHAT:
            case QQ:
            case ALIPAY:
            case GITHUB:
                validateThirdParty();
                break;
            default:
                throw new IllegalArgumentException("不支持的登录类型");
        }
    }
    
    private void validateUsernamePassword() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }
    
    private void validatePhonePassword() {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }
    
    private void validateEmailPassword() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }
    
    private void validatePhoneCode() {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("验证码不能为空");
        }
    }
    
    private void validateThirdParty() {
        if (authCode == null || authCode.trim().isEmpty()) {
            throw new IllegalArgumentException("授权码不能为空");
        }
    }
}