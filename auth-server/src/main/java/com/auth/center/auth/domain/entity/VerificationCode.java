package com.auth.center.auth.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 验证码实体
 */
@Data
public class VerificationCode {

    /**
     * ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 接收者（手机号/邮箱）
     */
    private String receiver;

    /**
     * 验证码类型
     */
    private String codeType;

    /**
     * 验证码
     */
    private String code;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已使用
     */
    private Boolean used;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 发送状态
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 检查验证码是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 检查验证码是否有效（未过期且未使用）
     */
    public boolean isValid() {
        return !isExpired() && (used == null || !used);
    }

    /**
     * 标记为已使用
     */
    public void markAsUsed() {
        this.used = true;
        this.usedTime = LocalDateTime.now();
    }
}