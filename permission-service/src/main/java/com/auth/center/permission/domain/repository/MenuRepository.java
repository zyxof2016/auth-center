package com.auth.center.role.domain.repository;

import com.auth.center.role.domain.entity.MenuEntity;

import java.util.List;
import java.util.Optional;

/**
 * 菜单仓储接口
 */
public interface MenuRepository {
    
    /**
     * 保存菜单
     */
    MenuEntity save(MenuEntity menuEntity);
    
    /**
     * 根据ID查找菜单
     */
    Optional<MenuEntity> findById(Long id);
    
    /**
     * 根据菜单编码查找菜单
     */
    Optional<MenuEntity> findByMenuCode(Long tenantId, String menuCode);
    
    /**
     * 检查菜单编码是否存在
     */
    boolean existsByMenuCode(Long tenantId, String menuCode);
    
    /**
     * 根据父菜单ID查找子菜单列表
     */
    List<MenuEntity> findByParentId(Long tenantId, Long parentId);
    
    /**
     * 根据租户ID查找所有菜单
     */
    List<MenuEntity> findByTenantId(Long tenantId);
    
    /**
     * 根据租户ID和角色ID查找菜单列表
     */
    List<MenuEntity> findByTenantIdAndRoleId(Long tenantId, Long roleId);
    
    /**
     * 删除菜单
     */
    void deleteById(Long id);
    
    /**
     * 根据父菜单ID查找菜单数量
     */
    long countByParentId(Long parentId);
}