package com.auth.center.monitor.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 告警规则实体
 */
@Data
@Document(indexName = "alert_rule")
public class AlertRuleEntity {
    
    /**
     * 规则ID
     */
    @Id
    private Long id;
    
    /**
     * 租户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    
    /**
     * 规则名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String ruleName;
    
    /**
     * 规则类型
     */
    @Field(type = FieldType.Keyword)
    private String ruleType;
    
    /**
     * 指标名称
     */
    @Field(type = FieldType.Keyword)
    private String metricName;
    
    /**
     * 比较运算符
     */
    @Field(type = FieldType.Keyword)
    private String operator;
    
    /**
     * 阈值
     */
    @Field(type = FieldType.Double)
    private Double threshold;
    
    /**
     * 持续时间(秒)
     */
    @Field(type = FieldType.Integer)
    private Integer duration;
    
    /**
     * 告警级别
     */
    @Field(type = FieldType.Keyword)
    private String severity;
    
    /**
     * 告警接收人
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String receivers;
    
    /**
     * 告警方式
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String alertMethods;
    
    /**
     * 是否启用
     */
    @Field(type = FieldType.Boolean)
    private Boolean enabled;
    
    /**
     * 描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;
    
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updatedTime;
    
    /**
     * 创建告警规则
     */
    public static AlertRuleEntity create(Long tenantId, String ruleName, String ruleType,
                                       String metricName, String operator, Double threshold,
                                       Integer duration, String severity, String receivers,
                                       String alertMethods, String description) {
        AlertRuleEntity rule = new AlertRuleEntity();
        rule.tenantId = tenantId;
        rule.ruleName = ruleName;
        rule.ruleType = ruleType;
        rule.metricName = metricName;
        rule.operator = operator;
        rule.threshold = threshold;
        rule.duration = duration;
        rule.severity = severity;
        rule.receivers = receivers;
        rule.alertMethods = alertMethods;
        rule.description = description;
        rule.enabled = true;
        rule.createdTime = LocalDateTime.now();
        rule.updatedTime = LocalDateTime.now();
        return rule;
    }
    
    /**
     * 更新告警规则
     */
    public void update(String ruleName, String ruleType, String metricName, String operator,
                      Double threshold, Integer duration, String severity, String receivers,
                      String alertMethods, String description) {
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.metricName = metricName;
        this.operator = operator;
        this.threshold = threshold;
        this.duration = duration;
        this.severity = severity;
        this.receivers = receivers;
        this.alertMethods = alertMethods;
        this.description = description;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 启用规则
     */
    public void enable() {
        this.enabled = true;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 禁用规则
     */
    public void disable() {
        this.enabled = false;
        this.updatedTime = LocalDateTime.now();
    }
}