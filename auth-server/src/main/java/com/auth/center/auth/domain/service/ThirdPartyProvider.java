package com.auth.center.auth.domain.service;

import java.util.Map;

/**
 * 第三方登录提供者接口
 * 支持插拔式设计，每个第三方登录方式实现此接口
 */
public interface ThirdPartyProvider {
    
    /**
     * 获取提供者类型
     * @return 提供者类型（如：WECHAT, QQ, ALIPAY, GITHUB等）
     */
    String getProviderType();
    
    /**
     * 获取提供者名称
     * @return 提供者名称（如：微信登录、QQ登录等）
     */
    String getProviderName();
    
    /**
     * 是否启用
     * @return true-启用，false-禁用
     */
    boolean isEnabled();
    
    /**
     * 获取授权地址
     * @param redirectUri 回调地址
     * @param state 状态参数
     * @param additionalParams 额外参数
     * @return 授权地址
     */
    String getAuthorizeUrl(String redirectUri, String state, Map<String, String> additionalParams);
    
    /**
     * 处理授权回调
     * @param code 授权码
     * @param state 状态参数
     * @return 第三方用户信息
     */
    ThirdPartyUserInfo handleCallback(String code, String state);
    
    /**
     * 获取用户信息
     * @param accessToken 访问令牌
     * @param openId 用户唯一标识
     * @return 第三方用户信息
     */
    ThirdPartyUserInfo getUserInfo(String accessToken, String openId);
    
    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌信息
     */
    TokenInfo refreshToken(String refreshToken);
    
    /**
     * 验证配置是否完整
     * @return true-配置完整，false-配置不完整
     */
    boolean validateConfig();
}

/**
 * 第三方用户信息
 */
class ThirdPartyUserInfo {
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer gender;
    private String location;
    private Map<String, Object> rawData;
    
    // getter/setter省略
}

/**
 * 令牌信息
 */
class TokenInfo {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String scope;
    
    // getter/setter省略
}