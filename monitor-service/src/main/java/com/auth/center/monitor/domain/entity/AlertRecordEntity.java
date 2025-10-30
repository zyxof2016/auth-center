package com.auth.center.monitor.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 告警记录实体
 */
@Data
@Document(indexName = "alert_record")
public class AlertRecordEntity {
    
    /**
     * 记录ID
     */
    @Id
    private Long id;
    
    /**
     * 租户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    
    /**
     * 规则ID
     */
    @Field(type = FieldType.Long)
    private Long ruleId;
    
    /**
     * 告警标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String alertTitle;
    
    /**
     * 告警内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String alertContent;
    
    /**
     * 告警级别
     */
    @Field(type = FieldType.Keyword)
    private String severity;
    
    /**
     * 服务名称
     */
    @Field(type = FieldType.Keyword)
    private String serviceName;
    
    /**
     * 实例ID
     */
    @Field(type = FieldType.Keyword)
    private String instanceId;
    
    /**
     * 指标值
     */
    @Field(type = FieldType.Double)
    private Double metricValue;
    
    /**
     * 告警状态
     */
    @Field(type = FieldType.Keyword)
    private String status;
    
    /**
     * 告警时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime alertTime;
    
    /**
     * 恢复时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime recoverTime;
    
    /**
     * 处理人
     */
    @Field(type = FieldType.Keyword)
    private String handler;
    
    /**
     * 处理时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime handleTime;
    
    /**
     * 处理备注
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String handleRemark;
    
    /**
     * 创建告警记录
     */
    public static AlertRecordEntity create(Long tenantId, Long ruleId, String alertTitle,
                                         String alertContent, String severity, String serviceName,
                                         String instanceId, Double metricValue) {
        AlertRecordEntity alert = new AlertRecordEntity();
        alert.tenantId = tenantId;
        alert.ruleId = ruleId;
        alert.alertTitle = alertTitle;
        alert.alertContent = alertContent;
        alert.severity = severity;
        alert.serviceName = serviceName;
        alert.instanceId = instanceId;
        alert.metricValue = metricValue;
        alert.status = "ACTIVE";
        alert.alertTime = LocalDateTime.now();
        return alert;
    }
    
    /**
     * 处理告警
     */
    public void handle(String handler, String handleRemark) {
        this.status = "HANDLED";
        this.handler = handler;
        this.handleTime = LocalDateTime.now();
        this.handleRemark = handleRemark;
    }
    
    /**
     * 恢复告警
     */
    public void recover() {
        this.status = "RECOVERED";
        this.recoverTime = LocalDateTime.now();
    }
}