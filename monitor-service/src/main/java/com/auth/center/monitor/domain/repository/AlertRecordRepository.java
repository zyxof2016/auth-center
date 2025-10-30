package com.auth.center.monitor.domain.repository;

import com.auth.center.monitor.domain.entity.AlertRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 告警记录仓库接口
 */
public interface AlertRecordRepository {
    
    /**
     * 保存告警记录
     */
    AlertRecordEntity save(AlertRecordEntity alert);
    
    /**
     * 根据ID查找告警记录
     */
    Optional<AlertRecordEntity> findById(Long id);
    
    /**
     * 查找活跃告警
     */
    List<AlertRecordEntity> findActiveAlerts();
    
    /**
     * 根据服务名称查找告警记录
     */
    List<AlertRecordEntity> findByServiceName(String serviceName);
    
    /**
     * 根据时间范围查找告警记录
     */
    List<AlertRecordEntity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 分页查询告警记录
     */
    Page<AlertRecordEntity> findByConditions(String serviceName, String severity, String status, 
                                          LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}