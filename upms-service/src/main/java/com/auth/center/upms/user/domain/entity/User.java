package com.auth.center.user.domain.entity;

import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户领域实体（充血模型）
 */
@Data
public class User {
    
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

    /**
     * 验证用户信息是否有效
     *
     * @throws IllegalStateException 当用户信息无效时抛出异常
     */
    public void validate() throws IllegalStateException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("用户名不能为空");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalStateException("密码长度不能少于6位");
        }

        // 可以添加更多验证规则
    }

    /**
     * 更新用户信息
     *
     * @param email 邮箱
     * @param phone 手机号
     * @param realName 真实姓名
     */
    public void updateInfo(String email, String phone, String realName) {
        this.email = email;
        this.phone = phone;
        this.realName = realName;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新密码
     *
     * @param newPassword 新密码
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.pwdUpdateTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 锁定用户
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 解锁用户
     */
    public void unlock() {
        this.status = UserStatus.NORMAL;
        this.loginFailCount = 0;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 增加登录失败次数
     */
    public void incrementLoginFailCount() {
        if (this.loginFailCount == null) {
            this.loginFailCount = 0;
        }
        this.loginFailCount++;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginFailCount() {
        this.loginFailCount = 0;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 记录登录信息
     *
     * @param ip 登录IP
     */
    public void recordLogin(String ip) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = ip;
        this.resetLoginFailCount();
        this.updatedTime = LocalDateTime.now();
    }
}