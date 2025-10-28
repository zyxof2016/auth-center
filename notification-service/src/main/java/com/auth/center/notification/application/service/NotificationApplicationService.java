package com.auth.center.notification.application.service;

import com.auth.center.notification.domain.entity.NotificationEntity;
import com.auth.center.notification.domain.enums.NotificationType;
import com.auth.center.notification.domain.repository.NotificationRepository;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知应用服务
 */
@Service
@RequiredArgsConstructor
public class NotificationApplicationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * 发送通知
     */
    public SingleResponse<NotificationEntity> sendNotification(NotificationEntity notification) {
        notification.setStatus("SENDING");
        notification.setSendTime(LocalDateTime.now());
        notification.setCreatedTime(LocalDateTime.now());
        notification.setUpdatedTime(LocalDateTime.now());
        
        NotificationEntity savedNotification = notificationRepository.save(notification);
        return SingleResponse.of(savedNotification);
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
        
        return PageResponse.of(notificationPage.getContent(), notificationPage.getTotalElements(), page, size);
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
            if (notification.getRetryCount() < notification.getMaxRetryCount()) {
                notification.setStatus("RETRYING");
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setUpdatedTime(LocalDateTime.now());
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
        
        notification.setStatus("READ");
        notification.setUpdatedTime(LocalDateTime.now());
        notificationRepository.save(notification);
        
        return Response.buildSuccess();
    }
}