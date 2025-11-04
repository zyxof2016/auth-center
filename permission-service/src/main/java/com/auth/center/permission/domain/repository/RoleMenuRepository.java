package com.auth.center.role.domain.repository;

import com.auth.center.role.domain.entity.RoleMenuEntity;

import java.util.List;

/**
 * 角色菜单关联仓储接口
 */
public interface RoleMenuRepository {
    
    /**
     * 保存角色菜单关联
     */
    RoleMenuEntity save(RoleMenuEntity roleMenuEntity);
    
    /**
     * 根据角色ID删除关联
     */
    void deleteByRoleId(Long roleId);
    
    /**
     * 根据角色ID查找菜单ID列表
     */
    List<Long> findMenuIdsByRoleId(Long roleId);
    
    /**
     * 根据角色ID查找关联列表
     */
    List<RoleMenuEntity> findByRoleId(Long roleId);
    
    /**
     * 批量保存角色菜单关联
     */
    void batchSave(List<RoleMenuEntity> roleMenuEntities);
    
    /**
     * 根据角色ID和菜单ID列表删除关联
     */
    void deleteByRoleIdAndMenuIds(Long roleId, List<Long> menuIds);
}