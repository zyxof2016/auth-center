package com.auth.center.auth.domain.service.impl;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyProviderRegistry;
import com.auth.center.auth.application.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 第三方登录策略 - 插拔式设计
 * 支持动态配置的第三方登录提供者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyLoginStrategy implements LoginStrategy {
    
    private final ThirdPartyProviderRegistry providerRegistry;
    
    @Override
    public LoginType getLoginType() {
        return LoginType.THIRD_PARTY;
    }
    
    @Override
    public LoginResult login(LoginRequest request) {
        log.info("执行第三方登录: {}", request.getThirdType());
        
        // 1. 验证第三方授权码
        validateThirdPartyCode(request);
        
        // 2. 获取第三方登录提供者
        ThirdPartyProvider provider = getThirdPartyProvider(request.getThirdType());
        
        // 3. 处理授权回调，获取第三方用户信息
        var thirdUserInfo = provider.handleCallback(request.getCode(), request.getState());
        
        // 4. 查询或创建用户
        var user = findOrCreateUserByThirdParty(thirdUserInfo, request.getThirdType());
        
        // 5. 验证用户状态
        validateUserStatus(user);
        
        // 6. 生成令牌
        var tokens = generateTokens(user);
        
        // 7. 记录登录日志
        recordLoginLog(user, LoginType.THIRD_PARTY);
        
        return buildLoginResult(tokens, user);
    }
    
    private void validateThirdPartyCode(LoginRequest request) {
        // 验证第三方类型
        if (request.getThirdType() == null || request.getThirdType().trim().isEmpty()) {
            throw AuthException.thirdPartyLoginFailed("第三方类型不能为空");
        }
        
        // 验证授权码
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw AuthException.thirdPartyLoginFailed("第三方授权码不能为空");
        }
        
        // 验证state参数（防止CSRF攻击）
        if (request.getState() == null || request.getState().trim().isEmpty()) {
            throw AuthException.thirdPartyLoginFailed("state参数不能为空");
        }
    }
    
    private ThirdPartyProvider getThirdPartyProvider(String providerType) {
        // 检查提供者是否可用
        if (!providerRegistry.isProviderAvailable(providerType)) {
            throw AuthException.thirdPartyLoginFailed("第三方登录提供者不可用: " + providerType);
        }
        
        ThirdPartyProvider provider = providerRegistry.getProvider(providerType);
        if (provider == null) {
            throw AuthException.thirdPartyLoginFailed("不支持的第三方登录类型: " + providerType);
        }
        
        return provider;
    }
    
    private Object findOrCreateUserByThirdParty(Object thirdUserInfo, String thirdType) {
        // 根据第三方用户信息查询或创建用户
        var user = findUserByThirdParty(thirdUserInfo, thirdType);
        if (user == null) {
            user = createUserByThirdParty(thirdUserInfo, thirdType);
        }
        return user;
    }
    
    private Object findUserByThirdParty(Object thirdUserInfo, String thirdType) {
        // 实现通过第三方信息查询用户逻辑
        // 返回用户实体对象
        return new Object();
    }
    
    private Object createUserByThirdParty(Object thirdUserInfo, String thirdType) {
        // 实现通过第三方信息创建用户逻辑
        // 返回新创建的用户实体对象
        return new Object();
    }
    
    private void validateUserStatus(Object user) {
        // 实现用户状态验证逻辑
        // 检查用户是否被禁用等
    }
    
    private Object generateTokens(Object user) {
        // 实现令牌生成逻辑
        // 返回包含accessToken和refreshToken的对象
        return new Object();
    }
    
    private void recordLoginLog(Object user, LoginType loginType) {
        // 实现登录日志记录逻辑
    }
    
    private LoginResult buildLoginResult(Object tokens, Object user) {
        // 构建登录结果
        var result = new LoginResult();
        // 设置令牌和用户信息
        return result;
    }
}