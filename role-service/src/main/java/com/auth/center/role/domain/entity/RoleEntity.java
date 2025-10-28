package com.auth.center.role.domain.entity;

import com.auth.center.role.domain.enums.RoleStatus;
import com.auth.center.role.domain.enums.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
public class RoleEntity {
    
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
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}