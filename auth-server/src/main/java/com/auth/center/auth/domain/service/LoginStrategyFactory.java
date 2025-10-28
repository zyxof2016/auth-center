package com.auth.center.auth.domain.service;

import com.auth.center.auth.domain.enums.LoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录策略工厂
 */
@Component
public class LoginStrategyFactory {
    
    private final Map<LoginType, LoginStrategy> strategyMap = new ConcurrentHashMap<>();
    
    @Autowired
    public LoginStrategyFactory(Map<String, LoginStrategy> strategies) {
        strategies.forEach((beanName, strategy) -> {
            strategyMap.put(strategy.getLoginType(), strategy);
        });
    }
    
    /**
     * 根据登录类型获取登录策略
     */
    public LoginStrategy getStrategy(LoginType loginType) {
        LoginStrategy strategy = strategyMap.get(loginType);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的登录类型: " + loginType);
        }
        return strategy;
    }
    
    /**
     * 检查是否支持指定的登录类型
     */
    public boolean supports(LoginType loginType) {
        return strategyMap.containsKey(loginType);
    }
}