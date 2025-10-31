package com.auth.center.auth.domain.dto;

import lombok.Data;

/**
 * 用户信息DTO（用于auth-server内部使用）
 */
@Data
public class UserInfoDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 性别
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private java.time.LocalDateTime birthday;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 用户类型
     */
    private Integer userType;
    
    /**
     * 最后登录时间
     */
    private java.time.LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录失败次数
     */
    private Integer loginFailCount;
    
    /**
     * 密码修改时间
     */
    private java.time.LocalDateTime pwdUpdateTime;
    
    /**
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private java.time.LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    private java.time.LocalDateTime updatedTime;
}