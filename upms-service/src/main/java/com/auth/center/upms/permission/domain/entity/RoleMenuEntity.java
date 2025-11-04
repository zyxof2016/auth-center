package com.auth.center.upms.permission.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色菜单关联实体
 */
@Data
public class RoleMenuEntity {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 菜单ID
     */
    private Long menuId;
    
    /**
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}