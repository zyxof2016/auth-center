package com.auth.center.notification.infrastructure.repository;

import com.auth.center.notification.domain.entity.NotificationEntity;
import com.auth.center.notification.domain.enums.NotificationType;
import com.auth.center.notification.domain.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 通知仓库实现类
 * 注意：这是一个简化的内存实现，实际项目中应该使用数据库存储
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    
    // 使用内存存储模拟数据库（实际项目中应该使用真实的数据库）
    private final ConcurrentHashMap<Long, NotificationEntity> notificationStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<NotificationEntity>> receiverIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public NotificationEntity save(NotificationEntity notification) {
        if (notification.getId() == null) {
            notification.setId(idGenerator.getAndIncrement());
        }
        notificationStorage.put(notification.getId(), notification);
        
        // 更新接收人索引
        if (notification.getReceiver() != null) {
            receiverIndex.computeIfAbsent(notification.getReceiver(), k -> new java.util.ArrayList<>());
            // 简化处理，实际应用中需要更复杂的索引管理
        }
        
        return notification;
    }
    
    @Override
    public Optional<NotificationEntity> findById(Long id) {
        return Optional.ofNullable(notificationStorage.get(id));
    }
    
    @Override
    public Page<NotificationEntity> findByConditions(NotificationType notificationType, String receiver, 
                                                   String status, LocalDateTime startTime, LocalDateTime endTime, 
                                                   Pageable pageable) {
        List<NotificationEntity> filteredNotifications = notificationStorage.values().stream()
                .filter(notification -> notificationType == null || notification.getNotificationType().equals(notificationType.getCode()))
                .filter(notification -> receiver == null || notification.getReceiver().equals(receiver))
                .filter(notification -> status == null || notification.getStatus().equals(status))
                .filter(notification -> startTime == null || notification.getCreatedTime().isAfter(startTime) || notification.getCreatedTime().equals(startTime))
                .filter(notification -> endTime == null || notification.getCreatedTime().isBefore(endTime) || notification.getCreatedTime().equals(endTime))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        long total = notificationStorage.values().stream()
                .filter(notification -> notificationType == null || notification.getNotificationType().equals(notificationType.getCode()))
                .filter(notification -> receiver == null || notification.getReceiver().equals(receiver))
                .filter(notification -> status == null || notification.getStatus().equals(status))
                .filter(notification -> startTime == null || notification.getCreatedTime().isAfter(startTime) || notification.getCreatedTime().equals(startTime))
                .filter(notification -> endTime == null || notification.getCreatedTime().isBefore(endTime) || notification.getCreatedTime().equals(endTime))
                .count();
        
        return new PageImpl<>(filteredNotifications, pageable, total);
    }
    
    @Override
    public List<NotificationEntity> findByReceiver(String receiver, int maxCount) {
        return notificationStorage.values().stream()
                .filter(notification -> notification.getReceiver().equals(receiver))
                .sorted((n1, n2) -> n2.getCreatedTime().compareTo(n1.getCreatedTime())) // 按创建时间倒序
                .limit(maxCount)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationEntity> findFailedNotifications() {
        return notificationStorage.values().stream()
                .filter(notification -> "FAILED".equals(notification.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteExpiredNotifications(LocalDateTime expireTime) {
        notificationStorage.entrySet().removeIf(entry -> entry.getValue().getCreatedTime().isBefore(expireTime));
    }
    
    @Override
    public Long countUnreadNotifications(String receiver) {
        return notificationStorage.values().stream()
                .filter(notification -> notification.getReceiver().equals(receiver))
                .filter(notification -> "UNREAD".equals(notification.getStatus()) || "SENDING".equals(notification.getStatus()))
                .count();
    }
}