package com.auth.center.monitor.domain.repository;

import com.auth.center.monitor.domain.entity.AlertRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 告警规则仓库接口
 */
public interface AlertRuleRepository {
    
    /**
     * 保存告警规则
     */
    AlertRuleEntity save(AlertRuleEntity rule);
    
    /**
     * 根据ID查找告警规则
     */
    Optional<AlertRuleEntity> findById(Long id);
    
    /**
     * 查找所有告警规则
     */
    List<AlertRuleEntity> findAll();
    
    /**
     * 根据规则名称查找告警规则
     */
    Optional<AlertRuleEntity> findByRuleName(String ruleName);
    
    /**
     * 查找启用的告警规则
     */
    List<AlertRuleEntity> findEnabledRules();
    
    /**
     * 分页查询告警规则
     */
    Page<AlertRuleEntity> findByConditions(String ruleName, String ruleType, Boolean enabled, Pageable pageable);
}