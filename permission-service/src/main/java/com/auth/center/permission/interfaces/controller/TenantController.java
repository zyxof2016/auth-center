package com.auth.center.role.interfaces.controller;

import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.role.application.dto.TenantDTO;
import com.auth.center.role.application.service.TenantApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理控制器
 */
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {
    
    private final TenantApplicationService tenantApplicationService;
    
    /**
     * 创建租户
     */
    @PostMapping
    public SingleResponse<TenantDTO> createTenant(@RequestBody TenantDTO tenantDTO) {
        return tenantApplicationService.createTenant(tenantDTO);
    }
    
    /**
     * 更新租户
     */
    @PutMapping("/{id}")
    public SingleResponse<TenantDTO> updateTenant(@PathVariable Long id, @RequestBody TenantDTO tenantDTO) {
        return tenantApplicationService.updateTenant(id, tenantDTO);
    }
    
    /**
     * 获取租户详情
     */
    @GetMapping("/{id}")
    public SingleResponse<TenantDTO> getTenantById(@PathVariable Long id) {
        return tenantApplicationService.getTenantById(id);
    }
    
    /**
     * 分页查询租户列表
     */
    @GetMapping
    public Response getTenantList(@RequestParam(required = false) String tenantName,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        // 实际实现需要完善
        return Response.buildSuccess();
    }
    
    /**
     * 删除租户
     */
    @DeleteMapping("/{id}")
    public Response deleteTenant(@PathVariable Long id) {
        return tenantApplicationService.deleteTenant(id);
    }
    
    /**
     * 启用租户
     */
    @PutMapping("/{id}/enable")
    public Response enableTenant(@PathVariable Long id) {
        return tenantApplicationService.enableTenant(id);
    }
    
    /**
     * 禁用租户
     */
    @PutMapping("/{id}/disable")
    public Response disableTenant(@PathVariable Long id) {
        return tenantApplicationService.disableTenant(id);
    }
}