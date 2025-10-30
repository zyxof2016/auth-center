package com.auth.center.notification.infrastructure.service;

import com.auth.center.notification.domain.entity.NotificationEntity;

/**
 * 通知发送服务接口
 */
public interface NotificationSendService {
    
    /**
     * 发送通知
     */
    boolean sendNotification(NotificationEntity notification);
    
    /**
     * 重试发送失败的通知
     */
    boolean retrySendNotification(NotificationEntity notification);
}