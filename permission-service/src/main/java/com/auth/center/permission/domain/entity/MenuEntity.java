package com.auth.center.role.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单实体
 */
@Data
public class MenuEntity {
    
    /**
     * 菜单ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 菜单编码
     */
    private String menuCode;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单类型（目录/菜单/按钮）
     */
    private Integer menuType;
    
    /**
     * 路由地址
     */
    private String path;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 权限标识
     */
    private String permission;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否可见（0隐藏 1显示）
     */
    private Integer visible;
    
    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;
    
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
     * 启用菜单
     */
    public void enable() {
        this.status = 1; // 启用状态
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 禁用菜单
     */
    public void disable() {
        this.status = 0; // 禁用状态
        this.updatedTime = LocalDateTime.now();
    }
}