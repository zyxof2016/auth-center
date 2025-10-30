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
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;
    
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
    
    /**
     * 创建通知
     */
    public static NotificationEntity create(Long tenantId, String notificationType, String title, 
                                          String content, String receiver, String channel,
                                          String templateId, String templateParams) {
        NotificationEntity notification = new NotificationEntity();
        notification.tenantId = tenantId;
        notification.notificationType = notificationType;
        notification.title = title;
        notification.content = content;
        notification.receiver = receiver;
        notification.channel = channel;
        notification.templateId = templateId;
        notification.templateParams = templateParams;
        notification.status = "PENDING";
        notification.retryCount = 0;
        notification.maxRetryCount = 3;
        notification.createdTime = LocalDateTime.now();
        notification.updatedTime = LocalDateTime.now();
        return notification;
    }
    
    /**
     * 设置发送成功状态
     */
    public void setSendSuccess() {
        this.status = "SUCCESS";
        this.sendTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 设置发送失败状态
     */
    public void setSendFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.status = "READ";
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 标记为未读
     */
    public void markAsUnread() {
        this.status = "UNREAD";
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount = this.retryCount + 1;
        this.status = "RETRYING";
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount;
    }
    
    /**
     * 获取是否已读
     */
    public boolean isRead() {
        return "READ".equals(this.status);
    }
}