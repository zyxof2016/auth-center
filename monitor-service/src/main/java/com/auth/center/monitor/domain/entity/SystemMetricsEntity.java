package com.auth.center.monitor.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 系统指标实体
 */
@Data
@Document(indexName = "system_metrics")
public class SystemMetricsEntity {
    
    /**
     * 指标ID
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
     * CPU使用率
     */
    @Field(type = FieldType.Double)
    private Double cpuUsage;
    
    /**
     * 内存使用率
     */
    @Field(type = FieldType.Double)
    private Double memoryUsage;
    
    /**
     * 磁盘使用率
     */
    @Field(type = FieldType.Double)
    private Double diskUsage;
    
    /**
     * 网络流入速率
     */
    @Field(type = FieldType.Double)
    private Double networkInRate;
    
    /**
     * 网络流出速率
     */
    @Field(type = FieldType.Double)
    private Double networkOutRate;
    
    /**
     * 线程数
     */
    @Field(type = FieldType.Integer)
    private Integer threadCount;
    
    /**
     * 堆内存使用
     */
    @Field(type = FieldType.Long)
    private Long heapMemoryUsed;
    
    /**
     * 非堆内存使用
     */
    @Field(type = FieldType.Long)
    private Long nonHeapMemoryUsed;
    
    /**
     * GC次数
     */
    @Field(type = FieldType.Long)
    private Long gcCount;
    
    /**
     * GC时间
     */
    @Field(type = FieldType.Long)
    private Long gcTime;
    
    /**
     * 采集时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime collectTime;
    
    /**
     * 创建系统指标
     */
    public static SystemMetricsEntity create(Long tenantId, String serviceName, String instanceId,
                                           Double cpuUsage, Double memoryUsage, Double diskUsage,
                                           Double networkInRate, Double networkOutRate,
                                           Integer threadCount, Long heapMemoryUsed, Long nonHeapMemoryUsed,
                                           Long gcCount, Long gcTime) {
        SystemMetricsEntity metrics = new SystemMetricsEntity();
        metrics.tenantId = tenantId;
        metrics.serviceName = serviceName;
        metrics.instanceId = instanceId;
        metrics.cpuUsage = cpuUsage;
        metrics.memoryUsage = memoryUsage;
        metrics.diskUsage = diskUsage;
        metrics.networkInRate = networkInRate;
        metrics.networkOutRate = networkOutRate;
        metrics.threadCount = threadCount;
        metrics.heapMemoryUsed = heapMemoryUsed;
        metrics.nonHeapMemoryUsed = nonHeapMemoryUsed;
        metrics.gcCount = gcCount;
        metrics.gcTime = gcTime;
        metrics.collectTime = LocalDateTime.now();
        return metrics;
    }
}