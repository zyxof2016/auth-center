package com.auth.center.notification.infrastructure.service;

import com.auth.center.notification.domain.entity.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件通知发送服务
 */
@Slf4j
@Service
public class EmailNotificationService implements NotificationSendService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public boolean sendNotification(NotificationEntity notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getReceiver());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            log.info("邮件通知发送成功: notificationId={}, receiver={}", notification.getId(), notification.getReceiver());
            return true;
        } catch (Exception e) {
            log.error("邮件通知发送失败: notificationId={}, receiver={}, error={}", 
                    notification.getId(), notification.getReceiver(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean retrySendNotification(NotificationEntity notification) {
        return sendNotification(notification);
    }
}