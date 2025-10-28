package com.auth.center.auth.domain.service;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.enums.LoginType;

/**
 * 登录策略接口
 */
public interface LoginStrategy {
    
    /**
     * 支持的登录类型
     */
    LoginType getSupportedType();
    
    /**
     * 验证登录参数
     */
    void validate(LoginRequest request);
    
    /**
     * 执行登录
     */
    LoginResult login(LoginRequest request);
    
    /**
     * 是否支持该登录类型
     */
    default boolean supports(LoginType loginType) {
        return getSupportedType() == loginType;
    }
}