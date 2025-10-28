package com.auth.center.log.domain.repository;

import com.auth.center.log.domain.entity.LoginLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * 登录日志仓库接口
 */
public interface LoginLogRepository {
    
    /**
     * 保存登录日志
     */
    LoginLogEntity save(LoginLogEntity loginLogEntity);
    
    /**
     * 根据条件分页查询登录日志
     */
    Page<LoginLogEntity> findByConditions(Long tenantId, String username, String loginType, 
                                        Boolean status, LocalDateTime startTime, LocalDateTime endTime, 
                                        Pageable pageable);
    
    /**
     * 统计时间范围内的登录日志数量
     */
    Long countByTimeRange(Long tenantId, LocalDateTime startTime, LocalDateTime endTime);
}