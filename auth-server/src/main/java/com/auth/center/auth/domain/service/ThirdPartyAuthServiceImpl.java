package com.auth.center.auth.domain.service;

import com.auth.center.auth.domain.dto.LoginResult;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 第三方登录服务实现
 */
@Service
public class ThirdPartyAuthServiceImpl implements ThirdPartyAuthService {
    
    private final ThirdPartyProviderRegistry providerRegistry;
    
    public ThirdPartyAuthServiceImpl(ThirdPartyProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }
    
    @Override
    public String getAuthorizeUrl(String thirdType, String redirectUri, String state) {
        ThirdPartyProvider provider = providerRegistry.getProvider(thirdType);
        if (provider == null || !provider.isEnabled()) {
            throw new IllegalArgumentException("不支持的第三方登录类型或提供者未启用: " + thirdType);
        }
        
        // 如果没有提供state参数，生成一个随机的state参数
        String finalState = (state != null && !state.isEmpty()) ? state : generateState();
        
        // 添加额外的参数
        java.util.Map<String, String> additionalParams = new java.util.HashMap<>();
        additionalParams.put("response_type", "code");
        additionalParams.put("scope", "snsapi_login");
        
        return provider.getAuthorizeUrl(redirectUri, finalState, additionalParams);
    }
    
    @Override
    public LoginResult handleCallback(String thirdType, String authCode, String state) {
        ThirdPartyProvider provider = providerRegistry.getProvider(thirdType);
        if (provider == null || !provider.isEnabled()) {
            throw new IllegalArgumentException("不支持的第三方登录类型或提供者未启用: " + thirdType);
        }
        
        // 处理回调，获取用户信息
        ThirdPartyUser userInfo = provider.handleCallback(authCode, state);
        
        // 创建登录结果
        LoginResult loginResult = new LoginResult();
        loginResult.setAccessToken("mock_access_token_" + System.currentTimeMillis());
        loginResult.setRefreshToken("mock_refresh_token_" + System.currentTimeMillis());
        loginResult.setExpiresIn(7200); // 2小时
        
        // 设置用户信息
        LoginResult.UserInfo resultUserInfo = new LoginResult.UserInfo();
        resultUserInfo.setId(1L);
        resultUserInfo.setUsername("third_party_user");
        resultUserInfo.setNickname(userInfo.getThirdNickname());
        resultUserInfo.setAvatar(userInfo.getThirdAvatar());
        resultUserInfo.setRoles(new String[]{"USER"});
        resultUserInfo.setPermissions(new String[]{"READ"});
        
        loginResult.setUserInfo(resultUserInfo);
        loginResult.setLoginTime(java.time.LocalDateTime.now());
        
        return loginResult;
    }
    
    @Override
    public void bindAccount(String thirdType, Long userId, String authCode) {
        ThirdPartyProvider provider = providerRegistry.getProvider(thirdType);
        if (provider == null || !provider.isEnabled()) {
            throw new IllegalArgumentException("不支持的第三方登录类型或提供者未启用: " + thirdType);
        }
        
        // 处理绑定逻辑
        // 这里只是一个示例实现，实际项目中需要保存绑定关系到数据库
        System.out.println("绑定第三方账号: " + thirdType + ", 用户ID: " + userId);
    }
    
    @Override
    public void unbindAccount(String thirdType, Long userId) {
        ThirdPartyProvider provider = providerRegistry.getProvider(thirdType);
        if (provider == null) {
            throw new IllegalArgumentException("不支持的第三方登录类型: " + thirdType);
        }
        
        // 处理解绑逻辑
        // 这里只是一个示例实现，实际项目中需要从数据库删除绑定关系
        System.out.println("解绑第三方账号: " + thirdType + ", 用户ID: " + userId);
    }
    
    @Override
    public List<Object> getBindings(Long userId) {
        // 获取用户绑定的第三方账号列表
        // 这里只是一个示例实现，实际项目中需要从数据库查询
        return new java.util.ArrayList<>();
    }
    
    @Override
    public Object getStatistics() {
        // 获取第三方登录统计信息
        // 这里只是一个示例实现，实际项目中需要从数据库查询统计信息
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalLogins", 100);
        statistics.put("todayLogins", 10);
        return statistics;
    }
    
    /**
     * 生成随机的state参数，防止CSRF攻击
     */
    private String generateState() {
        return "state_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}