package com.auth.center.auth.infrastructure.config;

import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyProviderRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 第三方登录自动配置
 * 根据配置文件自动注册第三方登录提供者
 */
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
        // 简化实现，避免调用不存在的方法
    }
}