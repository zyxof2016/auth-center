package com.auth.center.auth.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录配置 - 插拔式设计
 * 支持动态配置多个第三方登录方式
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.third-party")
public class ThirdPartyConfig {
    
    /**
     * 是否启用第三方登录功能
     */
    private boolean enabled = true;
    
    /**
     * 默认回调地址
     */
    private String defaultRedirectUri = "http://localhost:8080/auth/callback";
    
    /**
     * 第三方登录提供者配置
     * key: 提供者类型（如：wechat, qq, alipay, github等）
     * value: 提供者配置
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();
    
    /**
     * 第三方登录提供者配置类
     */
    @Data
    public static class ProviderConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        
        /**
         * 提供者名称（用于显示）
         */
        private String name;
        
        /**
         * 应用ID/客户端ID
         */
        private String appId;
        
        /**
         * 应用密钥/客户端密钥
         */
        private String appSecret;
        
        /**
         * 授权范围
         */
        private String scope;
        
        /**
         * 授权地址（可选，不配置则使用默认地址）
         */
        private String authorizeUrl;
        
        /**
         * 令牌地址（可选，不配置则使用默认地址）
         */
        private String tokenUrl;
        
        /**
         * 用户信息地址（可选，不配置则使用默认地址）
         */
        private String userInfoUrl;
        
        /**
         * 自定义参数
         */
        private Map<String, String> customParams = new HashMap<>();
        
        /**
         * 处理类（可选，不配置则使用默认处理类）
         */
        private String handlerClass;
        
        /**
         * 图标URL（用于前端显示）
         */
        private String iconUrl;
        
        /**
         * 排序权重（数字越小越靠前）
         */
        private Integer weight = 100;
        
        /**
         * 检查配置是否完整
         */
        public boolean isConfigComplete() {
            return appId != null && !appId.trim().isEmpty() 
                && appSecret != null && !appSecret.trim().isEmpty()
                && scope != null && !scope.trim().isEmpty();
        }
    }
}