package com.auth.center.auth.domain.service;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;

/**
 * 登录策略接口
 */
public interface LoginStrategy {
    
    /**
     * 执行登录逻辑
     *
     * @param request 登录请求
     * @return 登录结果
     */
    LoginResult login(LoginRequest request);
    
    /**
     * 获取支持的登录类型
     *
     * @return 登录类型
     */
    String getLoginType();
    
    /**
     * 验证策略是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();
}