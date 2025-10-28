package com.auth.center.user.domain.entity;

import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class UserEntity {
    
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
    private LocalDateTime birthday;
    
    /**
     * 状态
     */
    private UserStatus status;
    
    /**
     * 用户类型
     */
    private UserType userType;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
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
    private LocalDateTime pwdUpdateTime;
    
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
}