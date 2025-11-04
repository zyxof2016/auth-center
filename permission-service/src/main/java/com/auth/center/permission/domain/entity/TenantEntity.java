package com.auth.center.role.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户实体
 */
@Data
public class TenantEntity {
    
    /**
     * 租户ID
     */
    private Long id;
    
    /**
     * 租户编码
     */
    private String tenantCode;
    
    /**
     * 租户名称
     */
    private String tenantName;
    
    /**
     * 租户类型
     */
    private Integer tenantType;
    
    /**
     * 联系人
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
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
     * 启用租户
     */
    public void enable() {
        this.status = 1; // 启用状态
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 禁用租户
     */
    public void disable() {
        this.status = 0; // 禁用状态
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 检查租户是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * 检查租户是否已启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}