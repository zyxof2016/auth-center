package com.auth.center.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录配置属性
 */
@Component
@ConfigurationProperties(prefix = "auth.third-party")
public class ThirdPartyConfig {
    
    /**
     * 是否启用第三方登录
     */
    private boolean enabled = false;
    
    /**
     * 提供者配置映射
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Map<String, ProviderConfig> getProviders() {
        return providers;
    }
    
    public void setProviders(Map<String, ProviderConfig> providers) {
        this.providers = providers;
    }
    
    /**
     * 提供者配置
     */
    public static class ProviderConfig {
        
        /**
         * 应用ID
         */
        private String appId;
        
        /**
         * 应用密钥
         */
        private String appSecret;
        
        /**
         * 回调地址
         */
        private String redirectUri;
        
        /**
         * 是否启用
         */
        private boolean enabled = false;
        
        /**
         * 自定义处理类
         */
        private String handlerClass;
        
        public String getAppId() {
            return appId;
        }
        
        public void setAppId(String appId) {
            this.appId = appId;
        }
        
        public String getAppSecret() {
            return appSecret;
        }
        
        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
        
        public String getRedirectUri() {
            return redirectUri;
        }
        
        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getHandlerClass() {
            return handlerClass;
        }
        
        public void setHandlerClass(String handlerClass) {
            this.handlerClass = handlerClass;
        }
    }
}