package com.auth.center.role.application.service;

import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.role.domain.entity.RoleMenuEntity;
import com.auth.center.role.domain.exception.TenantMenuErrorCode;
import com.auth.center.role.domain.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单关联应用服务
 */
@Service
@RequiredArgsConstructor
public class RoleMenuApplicationService {
    
    private final RoleMenuRepository roleMenuRepository;
    
    /**
     * 为角色分配菜单权限
     */
    public Response assignMenusToRole(Long roleId, List<Long> menuIds, Long createdBy) {
        // 先删除角色原有的菜单关联
        roleMenuRepository.deleteByRoleId(roleId);
        
        // 批量创建新的角色菜单关联
        List<RoleMenuEntity> roleMenuEntities = menuIds.stream()
                .map(menuId -> {
                    RoleMenuEntity entity = new RoleMenuEntity();
                    entity.setRoleId(roleId);
                    entity.setMenuId(menuId);
                    entity.setCreatedBy(createdBy);
                    entity.setCreatedTime(LocalDateTime.now());
                    return entity;
                })
                .collect(Collectors.toList());
        
        roleMenuRepository.batchSave(roleMenuEntities);
        return Response.buildSuccess();
    }
    
    /**
     * 获取角色拥有的菜单ID列表
     */
    public SingleResponse<List<Long>> getMenuIdsByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuRepository.findMenuIdsByRoleId(roleId);
        return SingleResponse.of(menuIds);
    }
    
    /**
     * 为角色追加菜单权限（不删除原有权限）
     */
    public Response appendMenusToRole(Long roleId, List<Long> menuIds, Long createdBy) {
        // 获取角色已有的菜单ID列表
        List<Long> existingMenuIds = roleMenuRepository.findMenuIdsByRoleId(roleId);
        
        // 过滤掉已存在的菜单ID，避免重复
        List<RoleMenuEntity> roleMenuEntities = menuIds.stream()
                .filter(menuId -> !existingMenuIds.contains(menuId))
                .map(menuId -> {
                    RoleMenuEntity entity = new RoleMenuEntity();
                    entity.setRoleId(roleId);
                    entity.setMenuId(menuId);
                    entity.setCreatedBy(createdBy);
                    entity.setCreatedTime(LocalDateTime.now());
                    return entity;
                })
                .collect(Collectors.toList());
        
        if (!roleMenuEntities.isEmpty()) {
            roleMenuRepository.batchSave(roleMenuEntities);
        }
        
        return Response.buildSuccess();
    }
    
    /**
     * 从角色移除指定菜单权限
     */
    public Response removeMenusFromRole(Long roleId, List<Long> menuIds) {
        roleMenuRepository.deleteByRoleIdAndMenuIds(roleId, menuIds);
        return Response.buildSuccess();
    }
}