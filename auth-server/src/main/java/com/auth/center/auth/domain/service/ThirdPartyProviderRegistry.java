package com.auth.center.auth.domain.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 第三方登录提供者注册表
 */
@Component
public class ThirdPartyProviderRegistry {
    
    private final Map<String, ThirdPartyProvider> providers = new ConcurrentHashMap<>();
    
    /**
     * 注册第三方登录提供者
     *
     * @param providerType 提供者类型
     * @param provider 提供者实例
     */
    public void registerProvider(String providerType, ThirdPartyProvider provider) {
        providers.put(providerType, provider);
    }
    
    /**
     * 获取第三方登录提供者
     *
     * @param providerType 提供者类型
     * @return 提供者实例
     */
    public ThirdPartyProvider getProvider(String providerType) {
        return providers.get(providerType);
    }
    
    /**
     * 检查提供者是否存在
     *
     * @param providerType 提供者类型
     * @return 是否存在
     */
    public boolean containsProvider(String providerType) {
        return providers.containsKey(providerType);
    }
    
    /**
     * 移除第三方登录提供者
     *
     * @param providerType 提供者类型
     */
    public void removeProvider(String providerType) {
        providers.remove(providerType);
    }
    
    /**
     * 获取所有提供者
     *
     * @return 提供者映射
     */
    public Map<String, ThirdPartyProvider> getAllProviders() {
        return new ConcurrentHashMap<>(providers);
    }
}