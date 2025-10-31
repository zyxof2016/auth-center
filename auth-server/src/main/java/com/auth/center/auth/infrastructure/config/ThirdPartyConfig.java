package com.auth.center.auth.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.third-party")
public class ThirdPartyConfig {
    
    /**
     * 是否启用第三方登录功能
     */
    private boolean enabled = true;
    
    /**
     * 默认回调地址
     */
    private String defaultRedirectUri;
    
    /**
     * 第三方登录提供者配置
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();
    
    /**
     * 提供者配置
     */
    @Data
    public static class ProviderConfig {
        
        /**
         * 是否启用
         */
        private boolean enabled = true;
        
        /**
         * 名称
         */
        private String name;
        
        /**
         * 应用ID
         */
        private String appId;
        
        /**
         * 应用密钥
         */
        private String appSecret;
        
        /**
         * 授权范围
         */
        private String scope;
        
        /**
         * 图标URL
         */
        private String iconUrl;
        
        /**
         * 权重（排序用）
         */
        private int weight = 0;
        
        /**
         * 授权URL
         */
        private String authorizeUrl;
        
        /**
         * 访问令牌URL
         */
        private String accessTokenUrl;
        
        /**
         * 用户信息URL
         */
        private String userInfoUrl;
        
        /**
         * 自定义处理器类
         */
        private String handlerClass;
    }
}