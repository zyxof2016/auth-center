package com.auth.center.role.interfaces.controller;

import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.role.application.service.RoleMenuApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单关联控制器
 */
@RestController
@RequestMapping("/api/role-menu")
@RequiredArgsConstructor
public class RoleMenuController {
    
    private final RoleMenuApplicationService roleMenuApplicationService;
    
    /**
     * 为角色分配菜单权限
     */
    @PostMapping("/role/{roleId}/menus")
    public Response assignMenusToRole(@PathVariable Long roleId, 
                                      @RequestBody List<Long> menuIds,
                                      @RequestParam Long createdBy) {
        return roleMenuApplicationService.assignMenusToRole(roleId, menuIds, createdBy);
    }
    
    /**
     * 获取角色拥有的菜单ID列表
     */
    @GetMapping("/role/{roleId}/menus")
    public SingleResponse<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        return roleMenuApplicationService.getMenuIdsByRoleId(roleId);
    }
    
    /**
     * 为角色追加菜单权限（不删除原有权限）
     */
    @PostMapping("/role/{roleId}/menus/append")
    public Response appendMenusToRole(@PathVariable Long roleId, 
                                      @RequestBody List<Long> menuIds,
                                      @RequestParam Long createdBy) {
        return roleMenuApplicationService.appendMenusToRole(roleId, menuIds, createdBy);
    }
    
    /**
     * 从角色移除指定菜单权限
     */
    @DeleteMapping("/role/{roleId}/menus")
    public Response removeMenusFromRole(@PathVariable Long roleId, 
                                        @RequestBody List<Long> menuIds) {
        return roleMenuApplicationService.removeMenusFromRole(roleId, menuIds);
    }
}