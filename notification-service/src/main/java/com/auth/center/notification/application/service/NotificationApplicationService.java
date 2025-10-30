package com.auth.center.notification.application.service;

import com.auth.center.notification.domain.entity.NotificationEntity;
import com.auth.center.notification.domain.enums.NotificationType;
import com.auth.center.notification.domain.repository.NotificationRepository;
import com.auth.center.notification.infrastructure.service.NotificationServiceFactory;
import com.auth.center.notification.infrastructure.service.NotificationSendService;
import com.auth.center.notification.infrastructure.service.WebSocketNotificationService;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知应用服务
 */
@Service
@RequiredArgsConstructor
public class NotificationApplicationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationServiceFactory notificationServiceFactory;
    private final WebSocketNotificationService webSocketNotificationService;
    
    /**
     * 发送通知
     */
    public SingleResponse<NotificationEntity> sendNotification(NotificationEntity notification) {
        notification.setCreatedTime(LocalDateTime.now());
        notification.setUpdatedTime(LocalDateTime.now());
        
        // 保存通知到数据库
        NotificationEntity savedNotification = notificationRepository.save(notification);
        
        // 异步发送通知
        sendNotificationAsync(savedNotification);
        
        return SingleResponse.of(savedNotification);
    }
    
    /**
     * 异步发送通知
     */
    private void sendNotificationAsync(NotificationEntity notification) {
        new Thread(() -> {
            try {
                // 获取对应的通知发送服务
                NotificationSendService sendService = notificationServiceFactory.getNotificationService(notification.getChannel());
                
                // 发送通知
                boolean success = sendService.sendNotification(notification);
                
                // 更新通知状态
                if (success) {
                    notification.setSendSuccess();
                } else {
                    notification.setSendFailed("发送失败");
                }
                
                notificationRepository.save(notification);
                
                // 发送实时通知（WebSocket）
                if (success) {
                    webSocketNotificationService.sendRealTimeNotification(notification);
                }
            } catch (Exception e) {
                notification.setSendFailed("发送异常: " + e.getMessage());
                notificationRepository.save(notification);
            }
        }).start();
    }
    
    /**
     * 更新通知状态
     */
    public Response updateNotificationStatus(Long notificationId, String status, String errorMessage) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        notification.setStatus(status);
        notification.setErrorMessage(errorMessage);
        notification.setUpdatedTime(LocalDateTime.now());
        
        notificationRepository.save(notification);
        return Response.buildSuccess();
    }
    
    /**
     * 分页查询通知列表
     */
    public PageResponse<NotificationEntity> getNotificationPage(NotificationType notificationType, 
                                                              String receiver, String status,
                                                              LocalDateTime startTime, LocalDateTime endTime,
                                                              int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<NotificationEntity> notificationPage = notificationRepository.findByConditions(
                notificationType, receiver, status, startTime, endTime, pageRequest);
        
        return PageResponse.of(notificationPage.getContent(), page, size, notificationPage.getTotalElements());
    }
    
    /**
     * 获取用户通知列表
     */
    public List<NotificationEntity> getUserNotifications(String receiver, int maxCount) {
        return notificationRepository.findByReceiver(receiver, maxCount);
    }
    
    /**
     * 重试失败通知
     */
    public Response retryFailedNotifications() {
        List<NotificationEntity> failedNotifications = notificationRepository.findFailedNotifications();
        
        for (NotificationEntity notification : failedNotifications) {
            if (notification.canRetry()) {
                // 获取对应的通知发送服务
                NotificationSendService sendService = notificationServiceFactory.getNotificationService(notification.getChannel());
                
                // 重试发送通知
                boolean success = sendService.retrySendNotification(notification);
                
                // 更新通知状态
                if (success) {
                    notification.setSendSuccess();
                } else {
                    notification.incrementRetryCount();
                    if (!notification.canRetry()) {
                        notification.setSendFailed("达到最大重试次数");
                    }
                }
                
                notificationRepository.save(notification);
            }
        }
        
        return Response.buildSuccess();
    }
    
    /**
     * 标记通知为已读
     */
    public Response markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        notification.markAsRead();
        notification.setUpdatedTime(LocalDateTime.now());
        notificationRepository.save(notification);
        
        return Response.buildSuccess();
    }
    
    /**
     * 获取未读通知数量
     */
    public SingleResponse<Long> getUnreadNotificationCount(String receiver) {
        Long count = notificationRepository.countUnreadNotifications(receiver);
        return SingleResponse.of(count);
    }
}