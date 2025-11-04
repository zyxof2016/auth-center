package com.auth.center.role.domain.repository;

import com.auth.center.role.domain.entity.TenantEntity;

import java.util.Optional;

/**
 * 租户仓储接口
 */
public interface TenantRepository {
    
    /**
     * 保存租户
     */
    TenantEntity save(TenantEntity tenantEntity);
    
    /**
     * 根据ID查找租户
     */
    Optional<TenantEntity> findById(Long id);
    
    /**
     * 根据租户编码查找租户
     */
    Optional<TenantEntity> findByTenantCode(String tenantCode);
    
    /**
     * 检查租户编码是否存在
     */
    boolean existsByTenantCode(String tenantCode);
    
    /**
     * 删除租户
     */
    void deleteById(Long id);
}