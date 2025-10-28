package com.auth.center.auth.custom;

import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.infrastructure.config.ThirdPartyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 企业微信第三方登录提供者示例
 * 展示如何自定义第三方登录提供者
 */
@Slf4j
public class WeComThirdPartyProvider implements ThirdPartyProvider {
    
    private final ThirdPartyConfig.ProviderConfig config;
    
    public WeComThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
        this.config = config;
        log.info("初始化企业微信第三方登录提供者: {}", config.getName());
    }
    
    @Override
    public String getProviderType() {
        return "WECOM";
    }
    
    @Override
    public String getProviderName() {
        return config.getName() != null ? config.getName() : "企业微信登录";
    }
    
    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }
    
    @Override
    public boolean validateConfig() {
        return config.isConfigComplete();
    }
    
    @Override
    public String getAuthorizeUrl(String redirectUri, String state, Map<String, String> additionalParams) {
        // 生成企业微信授权地址
        String appId = config.getAppId();
        String scope = config.getScope() != null ? config.getScope() : "snsapi_privateinfo";
        
        // 如果有自定义授权地址，使用自定义地址
        if (config.getAuthorizeUrl() != null && !config.getAuthorizeUrl().trim().isEmpty()) {
            return String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s",
                    config.getAuthorizeUrl(), appId, redirectUri, scope, state);
        }
        
        // 使用默认的企业微信授权地址
        return String.format("https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=%s&agentid=%s&redirect_uri=%s&state=%s",
                appId, getAgentId(), redirectUri, state);
    }
    
    @Override
    public ThirdPartyUserInfo handleCallback(String code, String state) {
        log.info("处理企业微信登录回调，授权码: {}, state: {}", code, state);
        
        try {
            // 1. 获取访问令牌
            String accessToken = getAccessToken(code);
            
            // 2. 获取用户信息
            ThirdPartyUserInfo userInfo = getUserInfo(accessToken);
            
            // 3. 验证用户信息
            validateUserInfo(userInfo);
            
            return userInfo;
        } catch (Exception e) {
            log.error("处理企业微信登录回调失败: {}", e.getMessage(), e);
            throw new RuntimeException("企业微信登录失败: " + e.getMessage());
        }
    }
    
    @Override
    public ThirdPartyUserInfo getUserInfo(String accessToken, String openId) {
        log.info("获取企业微信用户信息，accessToken: {}, openId: {}", accessToken, openId);
        
        // 实现获取企业微信用户信息的逻辑
        // 调用企业微信API获取用户详细信息
        
        ThirdPartyUserInfo userInfo = new ThirdPartyUserInfo();
        userInfo.setOpenId(openId);
        // 设置其他用户信息
        
        return userInfo;
    }
    
    @Override
    public TokenInfo refreshToken(String refreshToken) {
        log.info("刷新企业微信访问令牌，refreshToken: {}", refreshToken);
        
        // 实现刷新令牌的逻辑
        // 调用企业微信API刷新访问令牌
        
        TokenInfo tokenInfo = new TokenInfo();
        // 设置新的令牌信息
        
        return tokenInfo;
    }
    
    // 私有方法实现
    
    private String getAgentId() {
        // 从自定义参数中获取AgentId
        return config.getCustomParams().getOrDefault("agentId", "1000001");
    }
    
    private String getAccessToken(String code) {
        // 实现获取访问令牌的逻辑
        // 调用企业微信API获取访问令牌
        log.info("获取企业微信访问令牌，code: {}", code);
        return "wecom_access_token_" + System.currentTimeMillis();
    }
    
    private ThirdPartyUserInfo getUserInfo(String accessToken) {
        // 实现获取用户信息的逻辑
        ThirdPartyUserInfo userInfo = new ThirdPartyUserInfo();
        userInfo.setOpenId("wecom_user_" + System.currentTimeMillis());
        userInfo.setNickname("企业微信用户");
        userInfo.setAvatar("https://example.com/avatar.png");
        return userInfo;
    }
    
    private void validateUserInfo(ThirdPartyUserInfo userInfo) {
        // 验证用户信息是否完整
        if (userInfo.getOpenId() == null || userInfo.getOpenId().trim().isEmpty()) {
            throw new RuntimeException("企业微信用户信息不完整");
        }
    }
}