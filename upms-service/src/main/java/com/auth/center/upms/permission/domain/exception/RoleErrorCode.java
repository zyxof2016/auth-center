package com.auth.center.role.domain.exception;

import com.auth.center.common.exception.ErrorCode;

/**
 * 角色服务错误码
 */
public enum RoleErrorCode implements ErrorCode {
    
    /**
     * 角色编码已存在
     */
    ROLE_CODE_EXISTS("ROLE_001", "角色编码已存在"),
    
    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND("ROLE_002", "角色不存在"),
    
    /**
     * 角色已被禁用
     */
    ROLE_DISABLED("ROLE_003", "角色已被禁用"),
    
    /**
     * 系统角色不允许修改
     */
    SYSTEM_ROLE_CANNOT_MODIFY("ROLE_004", "系统角色不允许修改"),
    
    /**
     * 系统角色不允许删除
     */
    SYSTEM_ROLE_CANNOT_DELETE("ROLE_005", "系统角色不允许删除"),
    
    /**
     * 角色已被用户使用
     */
    ROLE_USED_BY_USER("ROLE_006", "角色已被用户使用");
    
    private final String code;
    private final String message;
    
    RoleErrorCode(String code, String message) {
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