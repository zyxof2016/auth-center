package com.auth.center.auth.infrastructure.thirdparty;

import com.auth.center.auth.infrastructure.config.ThirdPartyConfig;
import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * QQ登录提供者
 */
@Component
public class QQProvider implements ThirdPartyProvider {
    
    private final ThirdPartyConfig thirdPartyConfig;
    
    @Autowired
    public QQProvider(ThirdPartyConfig thirdPartyConfig) {
        this.thirdPartyConfig = thirdPartyConfig;
    }
    
    @Override
    public String getProviderType() {
        return "QQ";
    }
    
    @Override
    public String getProviderName() {
        ThirdPartyConfig.ProviderConfig config = thirdPartyConfig.getProviders().get("qq");
        return config != null ? config.getName() : "QQ登录";
    }
    
    @Override
    public boolean isEnabled() {
        ThirdPartyConfig.ProviderConfig config = thirdPartyConfig.getProviders().get("qq");
        return config != null && config.isEnabled();
    }
    
    private ThirdPartyConfig.ProviderConfig getConfig() {
        return thirdPartyConfig.getProviders().get("qq");
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
            throw new IllegalStateException("QQ登录配置不存在");
        }
        
        String authorizeUrl = config.getAuthorizeUrl() != null && !config.getAuthorizeUrl().isEmpty() ? 
                              config.getAuthorizeUrl() : "https://graph.qq.com/oauth2.0/authorize";
        
        String scope = config.getScope() != null && !config.getScope().isEmpty() ? 
                       config.getScope() : "get_user_info";
        
        return authorizeUrl + 
               "?response_type=code" + 
               "&client_id=" + config.getAppId() + 
               "&redirect_uri=" + redirectUri + 
               "&state=" + state;
    }
    
    @Override
    public String getAuthorizeUrl(String redirectUri, String state, java.util.Map<String, String> additionalParams) {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        if (config == null) {
            throw new IllegalStateException("QQ登录配置不存在");
        }
        
        String authorizeUrl = config.getAuthorizeUrl() != null && !config.getAuthorizeUrl().isEmpty() ? 
                              config.getAuthorizeUrl() : "https://graph.qq.com/oauth2.0/authorize";
        
        String scope = config.getScope() != null && !config.getScope().isEmpty() ? 
                       config.getScope() : "get_user_info";
        
        StringBuilder url = new StringBuilder();
        url.append(authorizeUrl);
        url.append("?response_type=code");
        url.append("&client_id=").append(config.getAppId());
        url.append("&redirect_uri=").append(redirectUri);
        url.append("&state=").append(state);
        
        // 添加额外参数
        if (additionalParams != null && !additionalParams.isEmpty()) {
            for (java.util.Map.Entry<String, String> entry : additionalParams.entrySet()) {
                url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        
        return url.toString();
    }
    
    @Override
    public ThirdPartyUser handleCallback(String code, String state) {
        ThirdPartyConfig.ProviderConfig config = getConfig();
        if (config == null) {
            throw new IllegalStateException("QQ登录配置不存在");
        }
        
        // 这里应该实现实际的QQ回调处理逻辑
        // 1. 通过code获取access_token
        // 2. 通过access_token获取openid
        // 3. 通过access_token和openid获取用户信息
        // 4. 将用户信息转换为ThirdPartyUser对象
        
        // 暂时返回模拟数据
        ThirdPartyUser user = new ThirdPartyUser();
        user.setThirdId("qq_user_id");
        user.setThirdNickname("QQ用户");
        user.setThirdAvatar("https://example.com/avatar.jpg");
        user.setAccessToken("access_token");
        user.setRefreshToken("refresh_token");
        user.setExpireTime(System.currentTimeMillis() + 7200000L); // 2小时后过期
        user.setThirdType("QQ");
        return user;
    }
    
    @Override
    public ThirdPartyUser getUserInfo(String accessToken) {
        // 这里应该实现实际的获取用户信息逻辑
        // 暂时返回模拟数据
        ThirdPartyUser user = new ThirdPartyUser();
        user.setThirdId("qq_user_id");
        user.setThirdNickname("QQ用户");
        user.setThirdAvatar("https://example.com/avatar.jpg");
        user.setThirdType("QQ");
        return user;
    }
}