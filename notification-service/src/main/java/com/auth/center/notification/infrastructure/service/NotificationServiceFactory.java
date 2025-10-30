package com.auth.center.notification.infrastructure.service;

import com.auth.center.notification.domain.entity.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知发送服务工厂
 */
@Component
public class NotificationServiceFactory {
    
    private final Map<String, NotificationSendService> notificationServices;
    
    @Autowired
    public NotificationServiceFactory(List<NotificationSendService> services) {
        this.notificationServices = services.stream()
                .collect(Collectors.toMap(
                        service -> service.getClass().getSimpleName().replace("NotificationService", "").toUpperCase(),
                        service -> service
                ));
    }
    
    /**
     * 根据通知类型获取对应的通知发送服务
     */
    public NotificationSendService getNotificationService(String channel) {
        String serviceKey = channel.toUpperCase();
        NotificationSendService service = notificationServices.get(serviceKey);
        if (service == null) {
            // 默认使用站内通知
            service = notificationServices.get("IN_APP");
        }
        return service;
    }
}