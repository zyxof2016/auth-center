package com.auth.center.notification.interfaces.controller;

import com.auth.center.notification.application.service.NotificationApplicationService;
import com.auth.center.notification.domain.entity.NotificationEntity;
import com.auth.center.notification.domain.enums.NotificationType;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知管理控制器
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationApplicationService notificationApplicationService;
    
    /**
     * 发送通知
     */
    @PostMapping("/send")
    public SingleResponse<NotificationEntity> sendNotification(@RequestBody NotificationEntity notification) {
        return notificationApplicationService.sendNotification(notification);
    }
    
    /**
     * 更新通知状态
     */
    @PutMapping("/{notificationId}/status")
    public Response updateNotificationStatus(@PathVariable Long notificationId,
                                            @RequestParam String status,
                                            @RequestParam(required = false) String errorMessage) {
        return notificationApplicationService.updateNotificationStatus(notificationId, status, errorMessage);
    }
    
    /**
     * 分页查询通知列表
     */
    @GetMapping
    public PageResponse<NotificationEntity> getNotificationPage(
            @RequestParam(required = false) NotificationType notificationType,
            @RequestParam(required = false) String receiver,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificationApplicationService.getNotificationPage(notificationType, receiver, status, startTime, endTime, page, size);
    }
    
    /**
     * 获取用户通知列表
     */
    @GetMapping("/user/{receiver}")
    public List<NotificationEntity> getUserNotifications(@PathVariable String receiver,
                                                       @RequestParam(defaultValue = "10") int maxCount) {
        return notificationApplicationService.getUserNotifications(receiver, maxCount);
    }
    
    /**
     * 重试失败通知
     */
    @PostMapping("/retry")
    public Response retryFailedNotifications() {
        return notificationApplicationService.retryFailedNotifications();
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    public Response markAsRead(@PathVariable Long notificationId) {
        return notificationApplicationService.markAsRead(notificationId);
    }
}