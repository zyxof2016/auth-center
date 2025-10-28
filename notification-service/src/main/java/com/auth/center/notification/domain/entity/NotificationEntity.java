package com.auth.center.notification.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
public class NotificationEntity {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 通知类型
     */
    private String notificationType;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 接收人
     */
    private String receiver;
    
    /**
     * 发送渠道
     */
    private String channel;
    
    /**
     * 模板ID
     */
    private String templateId;
    
    /**
     * 模板参数
     */
    private String templateParams;
    
    /**
     * 发送状态
     */
    private String status;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}