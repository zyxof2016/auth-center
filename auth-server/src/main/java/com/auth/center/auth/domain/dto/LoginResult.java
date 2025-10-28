package com.auth.center.auth.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录结果DTO
 */
@Data
public class LoginResult {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "bearer";
    
    /**
     * 过期时间（秒）
     */
    private Integer expiresIn;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    
    /**
     * 用户信息DTO
     */
    @Data
    public static class UserInfo {
        
        /**
         * 用户ID
         */
        private Long id;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 真实姓名
         */
        private String realName;
        
        /**
         * 昵称
         */
        private String nickname;
        
        /**
         * 邮箱
         */
        private String email;
        
        /**
         * 手机号
         */
        private String phone;
        
        /**
         * 头像
         */
        private String avatar;
        
        /**
         * 用户类型
         */
        private Integer userType;
        
        /**
         * 角色列表
         */
        private String[] roles;
        
        /**
         * 权限列表
         */
        private String[] permissions;
    }
}