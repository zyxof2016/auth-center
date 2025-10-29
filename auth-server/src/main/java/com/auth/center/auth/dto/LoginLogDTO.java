package com.auth.center.auth.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志DTO类
 */
@Data
public class LoginLogDTO implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String ip;
    private String userAgent;
    private Integer loginType;
    private Integer status;
    private String message;
    private LocalDateTime createTime;
}