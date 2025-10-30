package com.auth.center.monitor.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 服务健康状态实体
 */
@Data
@Document(indexName = "service_health")
public class ServiceHealthEntity {
    
    /**
     * 健康状态ID
     */
    @Id
    private Long id;
    
    /**
     * 租户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    
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
     * 健康状态
     */
    @Field(type = FieldType.Keyword)
    private String status;
    
    /**
     * 健康检查URL
     */
    @Field(type = FieldType.Keyword)
    private String healthUrl;
    
    /**
     * 响应时间(毫秒)
     */
    @Field(type = FieldType.Long)
    private Long responseTime;
    
    /**
     * 错误信息
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String errorMessage;
    
    /**
     * 最后检查时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime lastCheckTime;
    
    /**
     * 创建服务健康状态
     */
    public static ServiceHealthEntity create(Long tenantId, String serviceName, String instanceId,
                                           String status, String healthUrl, Long responseTime,
                                           String errorMessage) {
        ServiceHealthEntity health = new ServiceHealthEntity();
        health.tenantId = tenantId;
        health.serviceName = serviceName;
        health.instanceId = instanceId;
        health.status = status;
        health.healthUrl = healthUrl;
        health.responseTime = responseTime;
        health.errorMessage = errorMessage;
        health.lastCheckTime = LocalDateTime.now();
        return health;
    }
    
    /**
     * 更新服务健康状态
     */
    public void update(String status, Long responseTime, String errorMessage) {
        this.status = status;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
        this.lastCheckTime = LocalDateTime.now();
    }
}