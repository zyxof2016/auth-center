package com.auth.center.notification.infrastructure.service;

import com.auth.center.notification.domain.entity.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket通知服务
 */
@Slf4j
@Service
public class WebSocketNotificationService {
    
    // 存储用户WebSocket会话
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    /**
     * 添加用户会话
     */
    public void addUserSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("用户WebSocket会话已添加: userId={}", userId);
    }
    
    /**
     * 移除用户会话
     */
    public void removeUserSession(String userId) {
        userSessions.remove(userId);
        log.info("用户WebSocket会话已移除: userId={}", userId);
    }
    
    /**
     * 发送实时通知
     */
    public boolean sendRealTimeNotification(NotificationEntity notification) {
        WebSocketSession session = userSessions.get(notification.getReceiver());
        if (session != null && session.isOpen()) {
            try {
                // 构造通知消息
                String message = String.format("{\"type\":\"notification\",\"title\":\"%s\",\"content\":\"%s\"}", 
                        notification.getTitle(), notification.getContent());
                
                session.sendMessage(new TextMessage(message));
                log.info("实时通知发送成功: notificationId={}, receiver={}", notification.getId(), notification.getReceiver());
                return true;
            } catch (IOException e) {
                log.error("实时通知发送失败: notificationId={}, receiver={}, error={}", 
                        notification.getId(), notification.getReceiver(), e.getMessage(), e);
                return false;
            }
        } else {
            log.warn("用户WebSocket会话不存在或已关闭: receiver={}", notification.getReceiver());
            return false;
        }
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }
}