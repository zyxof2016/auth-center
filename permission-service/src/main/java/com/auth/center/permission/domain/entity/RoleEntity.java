package com.auth.center.role.domain.entity;

import com.auth.center.role.domain.enums.RoleStatus;
import com.auth.center.role.domain.enums.RoleType;
import com.auth.center.role.domain.exception.RoleErrorCode;
import com.auth.center.common.exception.BusinessException;
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
    
    /**
     * 启用角色
     */
    public void enable() {
        this.status = RoleStatus.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 禁用角色
     */
    public void disable() {
        this.status = RoleStatus.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 更新角色信息
     */
    public void update(String roleName, RoleType roleType, Integer dataScope, String description) {
        this.roleName = roleName;
        this.roleType = roleType;
        this.dataScope = dataScope;
        this.description = description;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 验证是否可以修改系统角色
     */
    public void validateSystemRoleModification(String newRoleCode, RoleType newRoleType) {
        if (this.roleType == RoleType.SYSTEM) {
            if (!this.roleCode.equals(newRoleCode)) {
                throw new BusinessException(RoleErrorCode.SYSTEM_ROLE_CANNOT_MODIFY);
            }
            if (this.roleType != newRoleType) {
                throw new BusinessException(RoleErrorCode.SYSTEM_ROLE_CANNOT_MODIFY);
            }
        }
    }
    
    /**
     * 验证是否可以删除系统角色
     */
    public void validateSystemRoleDeletion() {
        if (this.roleType == RoleType.SYSTEM) {
            throw new BusinessException(RoleErrorCode.SYSTEM_ROLE_CANNOT_DELETE);
        }
    }
}