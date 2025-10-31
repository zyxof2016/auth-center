package com.auth.center.auth.domain.service;

/**
 * 第三方登录提供者接口
 */
public interface ThirdPartyProvider {
    
    /**
     * 获取提供者类型
     */
    String getProviderType();
    
    /**
     * 获取提供者名称
     */
    String getProviderName();
    
    /**
     * 是否启用
     */
    boolean isEnabled();
    
    /**
     * 验证配置是否有效
     */
    boolean validateConfig();
    
    /**
     * 获取授权URL
     */
    String getAuthorizeUrl(String redirectUri, String state);
    
    /**
     * 获取授权URL（带额外参数）
     */
    default String getAuthorizeUrl(String redirectUri, String state, java.util.Map<String, String> additionalParams) {
        return getAuthorizeUrl(redirectUri, state);
    }
    
    /**
     * 处理回调
     */
    ThirdPartyUser handleCallback(String code, String state);
    
    /**
     * 获取用户信息
     */
    ThirdPartyUser getUserInfo(String accessToken);
}