package com.auth.center.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志DTO类 - 公共版本
 */
@Data
public class LoginLogDTO implements Serializable {
    private Long id;
    private Long tenantId;
    private Long userId;
    private String username;
    private String loginType;
    private String loginIp;
    private String userAgent;
    private Boolean success;
    private String errorMessage;
    private LocalDateTime loginTime;
}