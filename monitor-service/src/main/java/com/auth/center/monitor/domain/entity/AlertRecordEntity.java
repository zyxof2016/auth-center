package com.auth.center.monitor.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警记录实体
 */
@Data
public class AlertRecordEntity {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 规则ID
     */
    private Long ruleId;
    
    /**
     * 告警标题
     */
    private String alertTitle;
    
    /**
     * 告警内容
     */
    private String alertContent;
    
    /**
     * 告警级别
     */
    private String severity;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 实例ID
     */
    private String instanceId;
    
    /**
     * 指标值
     */
    private Double metricValue;
    
    /**
     * 告警状态
     */
    private String status;
    
    /**
     * 告警时间
     */
    private LocalDateTime alertTime;
    
    /**
     * 恢复时间
     */
    private LocalDateTime recoverTime;
    
    /**
     * 处理人
     */
    private String handler;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 处理备注
     */
    private String handleRemark;
}