package com.auth.center.monitor.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务健康状态实体
 */
@Data
public class ServiceHealthEntity {
    
    /**
     * 健康状态ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 实例ID
     */
    private String instanceId;
    
    /**
     * 健康状态
     */
    private String status;
    
    /**
     * 健康检查URL
     */
    private String healthUrl;
    
    /**
     * 响应时间(毫秒)
     */
    private Long responseTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 最后检查时间
     */
    private LocalDateTime lastCheckTime;
}