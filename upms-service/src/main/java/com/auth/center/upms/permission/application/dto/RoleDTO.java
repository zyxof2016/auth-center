package com.auth.center.role.application.dto;

import com.auth.center.role.domain.enums.RoleStatus;
import com.auth.center.role.domain.enums.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色数据传输对象
 */
@Data
public class RoleDTO {
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色类型
     */
    private RoleType roleType;
    
    /**
     * 数据权限范围
     */
    private Integer dataScope;
    
    /**
     * 状态
     */
    private RoleStatus status;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}