package com.auth.center.auth.infrastructure.thirdparty;

import com.auth.center.auth.infrastructure.config.ThirdPartyConfig;
import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微信登录提供者
 */
@Component
public class WechatProvider implements ThirdPartyProvider {
    
    private final ThirdPartyConfig thirdPartyConfig;
    
    @Autowired
    public WechatProvider(ThirdPartyConfig thirdPartyConfig) {
        this.thirdPartyConfig = thirdPartyConfig;
    }
    
    @Override
    public String getProviderType() {
        return "WECHAT";
    }
    
    @Override
    public String getProviderName() {
        ThirdPartyConfig.ProviderConfig config = thirdPartyConfig.getProviders().get("wechat");
        return config != null ? config.getName() : "微信登录";
    }
    
    @Override
    public boolean isEnabled() {
        ThirdPartyConfig.ProviderConfig config = thirdPartyConfig.getProviders().get("wechat");
        return config != null && config.isEnabled();
    }
    
    private ThirdPartyConfig.ProviderConfig getConfig() {
        return thirdPartyConfig.getProviders().get("wechat");
    }
    
    @Override
    public boolean validateConfig() {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        return config != null && 
               config.getAppId() != null && !config.getAppId().isEmpty() && 
               config.getAppSecret() != null && !config.getAppSecret().isEmpty();
    }
    
    @Override
    public String getAuthorizeUrl(String redirectUri, String state) {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        if (config == null) {
            throw new IllegalStateException("微信登录配置不存在");
        }
        
        String authorizeUrl = config.getAuthorizeUrl() != null && !config.getAuthorizeUrl().isEmpty() ? 
                              config.getAuthorizeUrl() : "https://open.weixin.qq.com/connect/qrconnect";
        
        String scope = config.getScope() != null && !config.getScope().isEmpty() ? 
                       config.getScope() : "snsapi_login";
        
        return authorizeUrl + 
               "?appid=" + config.getAppId() + 
               "&redirect_uri=" + redirectUri + 
               "&response_type=code" + 
               "&scope=" + scope + 
               "&state=" + state + 
               "#wechat_redirect";
    }
    
    @Override
    public String getAuthorizeUrl(String redirectUri, String state, java.util.Map<String, String> additionalParams) {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        if (config == null) {
            throw new IllegalStateException("微信登录配置不存在");
        }
        
        String authorizeUrl = config.getAuthorizeUrl() != null && !config.getAuthorizeUrl().isEmpty() ? 
                              config.getAuthorizeUrl() : "https://open.weixin.qq.com/connect/qrconnect";
        
        String scope = config.getScope() != null && !config.getScope().isEmpty() ? 
                       config.getScope() : "snsapi_login";
        
        StringBuilder url = new StringBuilder();
        url.append(authorizeUrl);
        url.append("?appid=").append(config.getAppId());
        url.append("&redirect_uri=").append(redirectUri);
        url.append("&response_type=code");
        url.append("&scope=").append(scope);
        url.append("&state=").append(state);
        
        // 添加额外参数
        if (additionalParams != null && !additionalParams.isEmpty()) {
            for (java.util.Map.Entry<String, String> entry : additionalParams.entrySet()) {
                url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        
        url.append("#wechat_redirect");
        
        return url.toString();
    }
    
    @Override
    public ThirdPartyUser handleCallback(String code, String state) {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        if (config == null) {
            throw new IllegalStateException("微信登录配置不存在");
        }
        
        // 这里应该实现实际的微信回调处理逻辑
        // 1. 通过code获取access_token
        // 2. 通过access_token获取用户信息
        // 3. 将用户信息转换为ThirdPartyUser对象
        
        // 暂时返回模拟数据
        ThirdPartyUser user = new ThirdPartyUser();
        user.setThirdId("wechat_user_id");
        user.setThirdNickname("微信用户");
        user.setThirdAvatar("https://example.com/avatar.jpg");
        user.setAccessToken("access_token");
        user.setRefreshToken("refresh_token");
        user.setExpireTime(System.currentTimeMillis() + 7200000L); // 2小时后过期
        user.setThirdType("WECHAT");
        return user;
    }
    
    @Override
    public ThirdPartyUser getUserInfo(String accessToken) {
        // 这里应该实现实际的获取用户信息逻辑
        // 暂时返回模拟数据
        ThirdPartyUser user = new ThirdPartyUser();
        user.setThirdId("wechat_user_id");
        user.setThirdNickname("微信用户");
        user.setThirdAvatar("https://example.com/avatar.jpg");
        user.setThirdType("WECHAT");
        return user;
    }
}