package com.auth.center.role.infrastructure.repository;

import com.auth.center.role.domain.entity.RoleEntity;
import com.auth.center.role.domain.enums.RoleStatus;
import com.auth.center.role.domain.repository.RoleRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓库实现类
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    
    private final RoleMapper roleMapper;
    
    public RoleRepositoryImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }
    
    @Override
    public RoleEntity save(RoleEntity roleEntity) {
        if (roleEntity.getId() == null) {
            roleMapper.insert(roleEntity);
        } else {
            roleMapper.updateById(roleEntity);
        }
        return roleEntity;
    }
    
    @Override
    public java.util.Optional<RoleEntity> findById(Long id) {
        RoleEntity entity = roleMapper.selectById(id);
        return java.util.Optional.ofNullable(entity);
    }
    
    @Override
    public java.util.Optional<RoleEntity> findByRoleCode(Long tenantId, String roleCode) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getTenantId, tenantId)
               .eq(RoleEntity::getRoleCode, roleCode);
        RoleEntity entity = roleMapper.selectOne(wrapper);
        return java.util.Optional.ofNullable(entity);
    }
    
    @Override
    public boolean existsByRoleCode(Long tenantId, String roleCode) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getTenantId, tenantId)
               .eq(RoleEntity::getRoleCode, roleCode);
        return roleMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public org.springframework.data.domain.Page<RoleEntity> findByConditions(Long tenantId, String roleName, RoleStatus status, Pageable pageable) {
        // 转换Spring Data Pageable到MyBatis Plus Page
        IPage<RoleEntity> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleEntity::getTenantId, tenantId);
        
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(RoleEntity::getRoleName, roleName);
        }
        
        if (status != null) {
            wrapper.eq(RoleEntity::getStatus, status);
        }
        
        // 执行分页查询
        IPage<RoleEntity> result = roleMapper.selectPage(page, wrapper);
        
        // 转换为Spring Data Page
        return new org.springframework.data.domain.PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
    
    @Override
    public List<RoleEntity> findByUserId(Long tenantId, Long userId) {
        return roleMapper.selectByUserId(tenantId, userId);
    }
    
    @Override
    public void delete(RoleEntity roleEntity) {
        roleMapper.deleteById(roleEntity.getId());
    }
}