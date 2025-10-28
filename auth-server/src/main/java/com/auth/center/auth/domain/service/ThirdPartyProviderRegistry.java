package com.auth.center.auth.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 第三方登录提供者注册器
 * 支持动态注册和管理第三方登录提供者
 */
@Slf4j
@Component
public class ThirdPartyProviderRegistry {
    
    private final Map<String, ThirdPartyProvider> providers = new ConcurrentHashMap<>();
    
    /**
     * 注册第三方登录提供者
     * @param provider 提供者实例
     */
    public void registerProvider(ThirdPartyProvider provider) {
        if (provider == null) {
            log.warn("尝试注册空的第三方登录提供者");
            return;
        }
        
        String providerType = provider.getProviderType().toUpperCase();
        if (providers.containsKey(providerType)) {
            log.warn("第三方登录提供者 {} 已存在，将被覆盖", providerType);
        }
        
        providers.put(providerType, provider);
        log.info("注册第三方登录提供者: {} - {}", providerType, provider.getProviderName());
    }
    
    /**
     * 注销第三方登录提供者
     * @param providerType 提供者类型
     */
    public void unregisterProvider(String providerType) {
        ThirdPartyProvider provider = providers.remove(providerType.toUpperCase());
        if (provider != null) {
            log.info("注销第三方登录提供者: {}", providerType);
        }
    }
    
    /**
     * 获取第三方登录提供者
     * @param providerType 提供者类型
     * @return 提供者实例，如果不存在返回null
     */
    public ThirdPartyProvider getProvider(String providerType) {
        return providers.get(providerType.toUpperCase());
    }
    
    /**
     * 获取所有已注册的提供者
     * @return 提供者列表
     */
    public List<ThirdPartyProvider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }
    
    /**
     * 获取所有启用的提供者
     * @return 启用的提供者列表
     */
    public List<ThirdPartyProvider> getEnabledProviders() {
        List<ThirdPartyProvider> enabledProviders = new ArrayList<>();
        for (ThirdPartyProvider provider : providers.values()) {
            if (provider.isEnabled() && provider.validateConfig()) {
                enabledProviders.add(provider);
            }
        }
        return enabledProviders;
    }
    
    /**
     * 检查提供者是否存在
     * @param providerType 提供者类型
     * @return true-存在，false-不存在
     */
    public boolean containsProvider(String providerType) {
        return providers.containsKey(providerType.toUpperCase());
    }
    
    /**
     * 检查提供者是否可用（存在且启用且配置完整）
     * @param providerType 提供者类型
     * @return true-可用，false-不可用
     */
    public boolean isProviderAvailable(String providerType) {
        ThirdPartyProvider provider = getProvider(providerType);
        return provider != null && provider.isEnabled() && provider.validateConfig();
    }
    
    /**
     * 获取可用的提供者类型列表
     * @return 可用的提供者类型列表
     */
    public List<String> getAvailableProviderTypes() {
        List<String> availableTypes = new ArrayList<>();
        for (ThirdPartyProvider provider : providers.values()) {
            if (provider.isEnabled() && provider.validateConfig()) {
                availableTypes.add(provider.getProviderType());
            }
        }
        return availableTypes;
    }
    
    /**
     * 获取提供者统计信息
     * @return 统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProviders", providers.size());
        
        int enabledCount = 0;
        int validConfigCount = 0;
        for (ThirdPartyProvider provider : providers.values()) {
            if (provider.isEnabled()) {
                enabledCount++;
            }
            if (provider.validateConfig()) {
                validConfigCount++;
            }
        }
        
        stats.put("enabledProviders", enabledCount);
        stats.put("validConfigProviders", validConfigCount);
        stats.put("availableProviders", getEnabledProviders().size());
        
        return stats;
    }
}