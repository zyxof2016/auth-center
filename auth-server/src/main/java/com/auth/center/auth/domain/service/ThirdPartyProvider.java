package com.auth.center.auth.domain.service;

import java.util.Map;

/**
 * 第三方登录提供者接口
 * 根据OAuth2.0最佳实践设计，支持安全的授权流程
 */
public interface ThirdPartyProvider {
    
    /**
     * 获取提供者类型
     *
     * @return 提供者类型
     */
    String getProviderType();
    
    /**
     * 获取提供者名称
     *
     * @return 提供者名称
     */
    String getProviderName();
    
    /**
     * 是否启用
     *
     * @return 是否启用
     */
    boolean isEnabled();
    
    /**
     * 验证配置是否有效
     *
     * @return 配置是否有效
     */
    boolean validateConfig();
    
    /**
     * 获取授权URL
     *
     * @param redirectUri 回调地址
     * @param state 状态参数，用于防止CSRF攻击
     * @param additionalParams 额外参数，如scope等
     * @return 授权URL
     */
    String getAuthorizeUrl(String redirectUri, String state, Map<String, String> additionalParams);
    
    /**
     * 处理授权回调
     *
     * @param authCode 授权码
     * @param state 状态参数
     * @return 第三方用户信息
     */
    ThirdPartyUserInfo handleCallback(String authCode, String state);
    
    /**
     * 获取用户信息
     *
     * @param accessToken 访问令牌
     * @param openId 用户唯一标识
     * @return 第三方用户信息
     */
    ThirdPartyUserInfo getUserInfo(String accessToken, String openId);
    
    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenInfo refreshToken(String refreshToken);
}