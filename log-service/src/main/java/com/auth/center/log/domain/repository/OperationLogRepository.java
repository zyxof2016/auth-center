package com.auth.center.log.domain.repository;

import com.auth.center.log.domain.entity.OperationLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * 操作日志仓库接口
 */
public interface OperationLogRepository {
    
    /**
     * 保存操作日志
     */
    OperationLogEntity save(OperationLogEntity operationLogEntity);
    
    /**
     * 根据条件分页查询操作日志
     */
    Page<OperationLogEntity> findByConditions(Long tenantId, String username, String operationType, 
                                            Boolean status, LocalDateTime startTime, LocalDateTime endTime, 
                                            Pageable pageable);
    
    /**
     * 统计时间范围内的操作日志数量
     */
    Long countByTimeRange(Long tenantId, LocalDateTime startTime, LocalDateTime endTime);
}