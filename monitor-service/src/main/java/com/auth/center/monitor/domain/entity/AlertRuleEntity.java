package com.auth.center.monitor.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警规则实体
 */
@Data
public class AlertRuleEntity {
    
    /**
     * 规则ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 规则类型
     */
    private String ruleType;
    
    /**
     * 指标名称
     */
    private String metricName;
    
    /**
     * 比较运算符
     */
    private String operator;
    
    /**
     * 阈值
     */
    private Double threshold;
    
    /**
     * 持续时间(秒)
     */
    private Integer duration;
    
    /**
     * 告警级别
     */
    private String severity;
    
    /**
     * 告警接收人
     */
    private String receivers;
    
    /**
     * 告警方式
     */
    private String alertMethods;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}