package com.auth.center.role.infrastructure.repository;

import com.auth.center.role.domain.entity.TenantEntity;
import com.auth.center.role.domain.repository.TenantRepository;
import com.auth.center.role.infrastructure.repository.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 租户仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {
    
    private final TenantMapper tenantMapper;
    
    @Override
    public TenantEntity save(TenantEntity tenantEntity) {
        if (tenantEntity.getId() == null) {
            // 新增
            tenantMapper.insert(tenantEntity);
        } else {
            // 更新
            tenantMapper.updateById(tenantEntity);
        }
        return tenantEntity;
    }
    
    @Override
    public Optional<TenantEntity> findById(Long id) {
        return Optional.ofNullable(tenantMapper.selectById(id));
    }
    
    @Override
    public Optional<TenantEntity> findByTenantCode(String tenantCode) {
        return Optional.ofNullable(tenantMapper.selectByTenantCode(tenantCode));
    }
    
    @Override
    public boolean existsByTenantCode(String tenantCode) {
        return tenantMapper.selectByTenantCode(tenantCode) != null;
    }
    
    @Override
    public void deleteById(Long id) {
        tenantMapper.deleteById(id);
    }
}