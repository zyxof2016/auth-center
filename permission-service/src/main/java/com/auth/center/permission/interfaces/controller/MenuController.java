package com.auth.center.role.interfaces.controller;

import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.role.application.dto.MenuDTO;
import com.auth.center.role.application.service.MenuApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuApplicationService menuApplicationService;
    
    /**
     * 创建菜单
     */
    @PostMapping
    public SingleResponse<MenuDTO> createMenu(@RequestBody MenuDTO menuDTO) {
        return menuApplicationService.createMenu(menuDTO);
    }
    
    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    public SingleResponse<MenuDTO> updateMenu(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        return menuApplicationService.updateMenu(id, menuDTO);
    }
    
    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    public SingleResponse<MenuDTO> getMenuById(@PathVariable Long id) {
        return menuApplicationService.getMenuById(id);
    }
    
    /**
     * 根据租户ID获取菜单列表
     */
    @GetMapping("/tenant/{tenantId}")
    public SingleResponse<List<MenuDTO>> getMenuListByTenantId(@PathVariable Long tenantId) {
        return menuApplicationService.getMenuListByTenantId(tenantId);
    }
    
    /**
     * 根据租户ID和角色ID获取菜单列表
     */
    @GetMapping("/tenant/{tenantId}/role/{roleId}")
    public SingleResponse<List<MenuDTO>> getMenuListByTenantIdAndRoleId(@PathVariable Long tenantId, 
                                                                        @PathVariable Long roleId) {
        return menuApplicationService.getMenuListByTenantIdAndRoleId(tenantId, roleId);
    }
    
    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Response deleteMenu(@PathVariable Long id) {
        return menuApplicationService.deleteMenu(id);
    }
    
    /**
     * 启用菜单
     */
    @PutMapping("/{id}/enable")
    public Response enableMenu(@PathVariable Long id) {
        return menuApplicationService.enableMenu(id);
    }
    
    /**
     * 禁用菜单
     */
    @PutMapping("/{id}/disable")
    public Response disableMenu(@PathVariable Long id) {
        return menuApplicationService.disableMenu(id);
    }
}