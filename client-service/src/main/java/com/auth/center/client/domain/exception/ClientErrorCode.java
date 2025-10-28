package com.auth.center.client.domain.exception;

/**
 * 客户端服务错误码
 */
public enum ClientErrorCode {
    
    /**
     * 客户端ID已存在
     */
    CLIENT_ID_EXISTS("CLIENT_001", "客户端ID已存在"),
    
    /**
     * 客户端不存在
     */
    CLIENT_NOT_FOUND("CLIENT_002", "客户端不存在"),
    
    /**
     * 客户端已被禁用
     */
    CLIENT_DISABLED("CLIENT_003", "客户端已被禁用"),
    
    /**
     * 客户端配置错误
     */
    CLIENT_CONFIG_ERROR("CLIENT_004", "客户端配置错误");
    
    private final String code;
    private final String message;
    
    ClientErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}