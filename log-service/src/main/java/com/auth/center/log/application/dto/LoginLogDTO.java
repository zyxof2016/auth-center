package com.auth.center.log.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志数据传输对象
 */
@Data
public class LoginLogDTO {
    
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
     * 登录类型
     */
    private String loginType;
    
    /**
     * 登录IP
     */
    private String loginIp;
    
    /**
     * 登录地点
     */
    private String loginLocation;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 状态
     */
    private Boolean status;
    
    /**
     * 失败原因
     */
    private String failReason;
    
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
}