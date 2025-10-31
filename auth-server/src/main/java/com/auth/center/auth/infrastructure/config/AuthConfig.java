package com.auth.center.auth.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 认证服务配置类
 */
@Configuration
@EnableFeignClients(basePackages = "com.auth.center.auth.infrastructure.client")
@EnableConfigurationProperties(ThirdPartyConfig.class)
public class AuthConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}