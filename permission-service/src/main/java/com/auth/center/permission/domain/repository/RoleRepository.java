package com.auth.center.role.domain.repository;

import com.auth.center.role.domain.entity.RoleEntity;
import com.auth.center.role.domain.enums.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓库接口
 */
public interface RoleRepository {
    
    /**
     * 保存角色
     */
    RoleEntity save(RoleEntity roleEntity);
    
    /**
     * 根据ID查找角色
     */
    Optional<RoleEntity> findById(Long id);
    
    /**
     * 根据角色编码查找角色
     */
    Optional<RoleEntity> findByRoleCode(Long tenantId, String roleCode);
    
    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCode(Long tenantId, String roleCode);
    
    /**
     * 根据条件分页查询角色
     */
    Page<RoleEntity> findByConditions(Long tenantId, String roleName, RoleStatus status, Pageable pageable);
    
    /**
     * 根据用户ID查询用户拥有的角色
     */
    List<RoleEntity> findByUserId(Long tenantId, Long userId);
    
    /**
     * 删除角色
     */
    void delete(RoleEntity roleEntity);
}