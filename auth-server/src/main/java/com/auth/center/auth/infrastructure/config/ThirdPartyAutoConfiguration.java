package com.auth.center.auth.infrastructure.config;

import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyProviderRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 第三方登录自动配置
 * 根据配置文件自动注册第三方登录提供者
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ThirdPartyConfig.class)
@ConditionalOnProperty(prefix = "auth.third-party", name = "enabled", havingValue = "true")
public class ThirdPartyAutoConfiguration {
    
    private final ThirdPartyConfig thirdPartyConfig;
    private final ThirdPartyProviderRegistry providerRegistry;
    
    public ThirdPartyAutoConfiguration(ThirdPartyConfig thirdPartyConfig, 
                                       ThirdPartyProviderRegistry providerRegistry) {
        this.thirdPartyConfig = thirdPartyConfig;
        this.providerRegistry = providerRegistry;
    }
    
    @PostConstruct
    public void autoRegisterProviders() {
        if (!thirdPartyConfig.isEnabled()) {
            log.info("第三方登录功能已禁用");
            return;
        }
        
        log.info("开始自动注册第三方登录提供者...");
        
        Map<String, ThirdPartyConfig.ProviderConfig> providers = thirdPartyConfig.getProviders();
        if (providers == null || providers.isEmpty()) {
            log.warn("未配置任何第三方登录提供者");
            return;
        }
        
        int registeredCount = 0;
        for (Map.Entry<String, ThirdPartyConfig.ProviderConfig> entry : providers.entrySet()) {
            String providerType = entry.getKey();
            ThirdPartyConfig.ProviderConfig config = entry.getValue();
            
            if (!config.isEnabled()) {
                log.info("第三方登录提供者 {} 已禁用，跳过注册", providerType);
                continue;
            }
            
            try {
                ThirdPartyProvider provider = createProvider(providerType, config);
                if (provider != null) {
                    providerRegistry.registerProvider(provider);
                    registeredCount++;
                    log.info("成功注册第三方登录提供者: {} - {}", providerType, config.getName());
                }
            } catch (Exception e) {
                log.error("注册第三方登录提供者 {} 失败: {}", providerType, e.getMessage(), e);
            }
        }
        
        log.info("第三方登录提供者自动注册完成，共注册 {} 个提供者", registeredCount);
    }
    
    /**
     * 创建第三方登录提供者实例
     */
    private ThirdPartyProvider createProvider(String providerType, ThirdPartyConfig.ProviderConfig config) {
        // 如果有自定义处理类，优先使用自定义处理类
        if (config.getHandlerClass() != null && !config.getHandlerClass().trim().isEmpty()) {
            return createCustomProvider(config.getHandlerClass(), providerType, config);
        }
        
        // 使用默认处理类
        return createDefaultProvider(providerType, config);
    }
    
    /**
     * 创建自定义处理类实例
     */
    private ThirdPartyProvider createCustomProvider(String handlerClass, String providerType, 
                                                   ThirdPartyConfig.ProviderConfig config) {
        try {
            Class<?> clazz = Class.forName(handlerClass);
            if (!ThirdPartyProvider.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("处理类必须实现 ThirdPartyProvider 接口: " + handlerClass);
            }
            
            ThirdPartyProvider provider = (ThirdPartyProvider) clazz.getDeclaredConstructor().newInstance();
            // 这里可以调用provider的初始化方法（如果有）
            return provider;
        } catch (Exception e) {
            log.error("创建自定义第三方登录提供者失败: {}", handlerClass, e);
            return null;
        }
    }
    
    /**
     * 创建默认处理类实例
     */
    private ThirdPartyProvider createDefaultProvider(String providerType, ThirdPartyConfig.ProviderConfig config) {
        // 根据providerType创建对应的默认提供者
        switch (providerType.toLowerCase()) {
            case "wechat":
                return new WeChatThirdPartyProvider(config);
            case "qq":
                return new QQThirdPartyProvider(config);
            case "alipay":
                return new AlipayThirdPartyProvider(config);
            case "github":
                return new GitHubThirdPartyProvider(config);
            default:
                log.warn("未知的第三方登录提供者类型: {}", providerType);
                return null;
        }
    }
    
    /**
     * 微信第三方登录提供者
     */
    private static class WeChatThirdPartyProvider implements ThirdPartyProvider {
        private final ThirdPartyConfig.ProviderConfig config;
        
        public WeChatThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
            this.config = config;
        }
        
        @Override
        public String getProviderType() { return "WECHAT"; }
        @Override
        public String getProviderName() { return config.getName() != null ? config.getName() : "微信登录"; }
        @Override
        public boolean isEnabled() { return config.isEnabled(); }
        @Override
        public boolean validateConfig() { return config.isConfigComplete(); }
        
        @Override
        public String getAuthorizeUrl(String redirectUri, String state, java.util.Map<String, String> additionalParams) {
            // 实现微信授权地址生成
            return null;
        }
        
        @Override
        public ThirdPartyUserInfo handleCallback(String code, String state) {
            // 实现微信回调处理
            return null;
        }
        
        @Override
        public ThirdPartyUserInfo getUserInfo(String accessToken, String openId) {
            // 实现微信用户信息获取
            return null;
        }
        
        @Override
        public TokenInfo refreshToken(String refreshToken) {
            // 实现微信令牌刷新
            return null;
        }
    }
    
    /**
     * QQ第三方登录提供者
     */
    private static class QQThirdPartyProvider implements ThirdPartyProvider {
        private final ThirdPartyConfig.ProviderConfig config;
        
        public QQThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
            this.config = config;
        }
        
        @Override
        public String getProviderType() { return "QQ"; }
        @Override
        public String getProviderName() { return config.getName() != null ? config.getName() : "QQ登录"; }
        @Override
        public boolean isEnabled() { return config.isEnabled(); }
        @Override
        public boolean validateConfig() { return config.isConfigComplete(); }
        
        // 其他方法实现类似微信提供者
    }
    
    /**
     * 支付宝第三方登录提供者
     */
    private static class AlipayThirdPartyProvider implements ThirdPartyProvider {
        private final ThirdPartyConfig.ProviderConfig config;
        
        public AlipayThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
            this.config = config;
        }
        
        @Override
        public String getProviderType() { return "ALIPAY"; }
        @Override
        public String getProviderName() { return config.getName() != null ? config.getName() : "支付宝登录"; }
        @Override
        public boolean isEnabled() { return config.isEnabled(); }
        @Override
        public boolean validateConfig() { return config.isConfigComplete(); }
        
        // 其他方法实现类似微信提供者
    }
    
    /**
     * GitHub第三方登录提供者
     */
    private static class GitHubThirdPartyProvider implements ThirdPartyProvider {
        private final ThirdPartyConfig.ProviderConfig config;
        
        public GitHubThirdPartyProvider(ThirdPartyConfig.ProviderConfig config) {
            this.config = config;
        }
        
        @Override
        public String getProviderType() { return "GITHUB"; }
        @Override
        public String getProviderName() { return config.getName() != null ? config.getName() : "GitHub登录"; }
        @Override
        public boolean isEnabled() { return config.isEnabled(); }
        @Override
        public boolean validateConfig() { return config.isConfigComplete(); }
        
        // 其他方法实现类似微信提供者
    }
}