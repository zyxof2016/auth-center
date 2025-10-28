package com.auth.center.role.interfaces.controller;

import com.auth.center.role.application.dto.RoleDTO;
import com.auth.center.role.application.service.RoleApplicationService;
import com.auth.center.role.domain.enums.RoleStatus;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleApplicationService roleApplicationService;
    
    /**
     * 创建角色
     */
    @PostMapping
    public SingleResponse<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        return roleApplicationService.createRole(roleDTO);
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public SingleResponse<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        return roleApplicationService.updateRole(id, roleDTO);
    }
    
    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public SingleResponse<RoleDTO> getRoleById(@PathVariable Long id) {
        return roleApplicationService.getRoleById(id);
    }
    
    /**
     * 分页查询角色列表
     */
    @GetMapping
    public PageResponse<RoleDTO> getRolePage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) RoleStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return roleApplicationService.getRolePage(tenantId, roleName, status, page, size);
    }
    
    /**
     * 启用角色
     */
    @PutMapping("/{id}/enable")
    public Response enableRole(@PathVariable Long id) {
        return roleApplicationService.enableRole(id);
    }
    
    /**
     * 禁用角色
     */
    @PutMapping("/{id}/disable")
    public Response disableRole(@PathVariable Long id) {
        return roleApplicationService.disableRole(id);
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Response deleteRole(@PathVariable Long id) {
        return roleApplicationService.deleteRole(id);
    }
    
    /**
     * 根据角色编码查询角色
     */
    @GetMapping("/role-code/{roleCode}")
    public SingleResponse<RoleDTO> getRoleByRoleCode(@RequestParam Long tenantId, @PathVariable String roleCode) {
        return roleApplicationService.getRoleByRoleCode(tenantId, roleCode);
    }
    
    /**
     * 获取用户拥有的角色列表
     */
    @GetMapping("/user/{userId}")
    public List<RoleDTO> getUserRoles(@RequestParam Long tenantId, @PathVariable Long userId) {
        return roleApplicationService.getUserRoles(tenantId, userId);
    }
}