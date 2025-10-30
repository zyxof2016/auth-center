package com.auth.center.monitor.domain.repository;

import com.auth.center.monitor.domain.entity.SystemMetricsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 系统指标仓库接口
 */
public interface SystemMetricsRepository {
    
    /**
     * 保存系统指标
     */
    SystemMetricsEntity save(SystemMetricsEntity metrics);
    
    /**
     * 根据ID查找系统指标
     */
    Optional<SystemMetricsEntity> findById(Long id);
    
    /**
     * 根据条件分页查询系统指标
     */
    Page<SystemMetricsEntity> findByConditions(String serviceName, String instanceId, 
                                             LocalDateTime startTime, LocalDateTime endTime, 
                                             Pageable pageable);
    
    /**
     * 获取最新的系统指标
     */
    List<SystemMetricsEntity> findLatestMetrics(String serviceName, String instanceId, int limit);
}