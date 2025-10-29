package com.auth.center.auth.custom;

import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.infrastructure.config.ThirdPartyConfig;

import java.util.Map;

/**
 * 企业微信第三方登录提供者示例
 * 展示如何自定义第三方登录提供者
 */
public class WeComThirdPartyProvider implements ThirdPartyProvider {
    
    private final ThirdPartyConfig.ProviderConfig config;
    
    public WeComThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
        this.config = config;
    }
    
    @Override
    public String getProviderType() {
        return "WECOM";
    }
    
    @Override
    public String getProviderName() {
        return "企业微信登录"; // 简化实现，不调用可能不存在的方法
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public boolean validateConfig() {
        return true;
    }
    
    @Override
    public String getAuthorizeUrl(String redirectUri, String state, Map<String, String> additionalParams) {
        // 简化实现
        return "https://example.com/authorize";
    }
    
    @Override
    public com.auth.center.auth.domain.service.ThirdPartyUserInfo handleCallback(String code, String state) {
        // 简化实现
        return null;
    }
    
    @Override
    public com.auth.center.auth.domain.service.ThirdPartyUserInfo getUserInfo(String accessToken, String openId) {
        // 简化实现
        return null;
    }
    
    @Override
    public com.auth.center.auth.domain.service.TokenInfo refreshToken(String refreshToken) {
        // 简化实现
        return null;
    }
}