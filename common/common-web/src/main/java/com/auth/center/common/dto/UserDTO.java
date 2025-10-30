package com.auth.center.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户DTO类 - 公共版本
 */
@Data
public class UserDTO implements Serializable {
    private Long id;
    private Long tenantId;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
}