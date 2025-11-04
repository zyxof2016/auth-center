package com.auth.center.role.domain.exception;

import com.auth.center.common.exception.ErrorCode;

/**
 * 租户和菜单服务错误码
 */
public enum TenantMenuErrorCode implements ErrorCode {
    
    /**
     * 租户编码已存在
     */
    TENANT_CODE_EXISTS("TENANT_001", "租户编码已存在"),
    
    /**
     * 租户不存在
     */
    TENANT_NOT_FOUND("TENANT_002", "租户不存在"),
    
    /**
     * 租户已被禁用
     */
    TENANT_DISABLED("TENANT_003", "租户已被禁用"),
    
    /**
     * 租户已过期
     */
    TENANT_EXPIRED("TENANT_004", "租户已过期"),
    
    /**
     * 菜单编码已存在
     */
    MENU_CODE_EXISTS("MENU_001", "菜单编码已存在"),
    
    /**
     * 菜单不存在
     */
    MENU_NOT_FOUND("MENU_002", "菜单不存在"),
    
    /**
     * 菜单已被禁用
     */
    MENU_DISABLED("MENU_003", "菜单已被禁用"),
    
    /**
     * 父菜单不存在
     */
    PARENT_MENU_NOT_FOUND("MENU_004", "父菜单不存在"),
    
    /**
     * 不能将菜单设置为自己的子菜单
     */
    MENU_CANNOT_BE_CHILD_OF_ITSELF("MENU_005", "不能将菜单设置为自己的子菜单");
    
    private final String code;
    private final String message;
    
    TenantMenuErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}