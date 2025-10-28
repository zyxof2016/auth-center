package com.auth.center.monitor.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统指标实体
 */
@Data
public class SystemMetricsEntity {
    
    /**
     * 指标ID
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
     * CPU使用率
     */
    private Double cpuUsage;
    
    /**
     * 内存使用率
     */
    private Double memoryUsage;
    
    /**
     * 磁盘使用率
     */
    private Double diskUsage;
    
    /**
     * 网络流入速率
     */
    private Double networkInRate;
    
    /**
     * 网络流出速率
     */
    private Double networkOutRate;
    
    /**
     * 线程数
     */
    private Integer threadCount;
    
    /**
     * 堆内存使用
     */
    private Long heapMemoryUsed;
    
    /**
     * 非堆内存使用
     */
    private Long nonHeapMemoryUsed;
    
    /**
     * GC次数
     */
    private Long gcCount;
    
    /**
     * GC时间
     */
    private Long gcTime;
    
    /**
     * 采集时间
     */
    private LocalDateTime collectTime;
}