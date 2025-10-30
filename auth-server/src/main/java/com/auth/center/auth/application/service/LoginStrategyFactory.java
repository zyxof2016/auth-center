package com.auth.center.auth.application.service;

import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 登录策略工厂类
 */
@Component
public class LoginStrategyFactory {
    
    private final Map<LoginType, LoginStrategy> strategies = new EnumMap<>(LoginType.class);
    
    public LoginStrategyFactory(List<LoginStrategy> strategyList) {
        for (LoginStrategy strategy : strategyList) {
            strategies.put(LoginType.getByCode(strategy.getLoginType()), strategy);
        }
    }
    
    /**
     * 根据登录类型获取对应的策略
     *
     * @param loginType 登录类型
     * @return 登录策略
     */
    public LoginStrategy getStrategy(LoginType loginType) {
        LoginStrategy strategy = strategies.get(loginType);
        if (strategy == null || !strategy.isAvailable()) {
            throw new IllegalArgumentException("不支持的登录类型或策略不可用: " + loginType);
        }
        return strategy;
    }
    
    /**
     * 检查策略是否可用
     *
     * @param loginType 登录类型
     * @return 是否可用
     */
    public boolean isStrategyAvailable(LoginType loginType) {
        LoginStrategy strategy = strategies.get(loginType);
        return strategy != null && strategy.isAvailable();
    }
}