package com.auth.center.log.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志数据传输对象
 */
@Data
public class OperationLogDTO {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 操作类型
     */
    private String operationType;
    
    /**
     * 操作模块
     */
    private String operationModule;
    
    /**
     * 操作描述
     */
    private String operationDesc;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 请求URL
     */
    private String requestUrl;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 执行时间(毫秒)
     */
    private Long executeTime;
    
    /**
     * 状态
     */
    private Boolean status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}