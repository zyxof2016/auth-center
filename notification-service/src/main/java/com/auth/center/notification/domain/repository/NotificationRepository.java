package com.auth.center.notification.domain.repository;

import com.auth.center.notification.domain.entity.NotificationEntity;
import com.auth.center.notification.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 通知仓库接口
 */
public interface NotificationRepository {
    
    /**
     * 保存通知
     */
    NotificationEntity save(NotificationEntity notification);
    
    /**
     * 根据ID查找通知
     */
    Optional<NotificationEntity> findById(Long id);
    
    /**
     * 根据条件分页查询通知
     */
    Page<NotificationEntity> findByConditions(NotificationType notificationType, String receiver, 
                                            String status, LocalDateTime startTime, LocalDateTime endTime, 
                                            Pageable pageable);
    
    /**
     * 根据接收人查找通知
     */
    List<NotificationEntity> findByReceiver(String receiver, int maxCount);
    
    /**
     * 查找失败通知
     */
    List<NotificationEntity> findFailedNotifications();
    
    /**
     * 删除过期通知
     */
    void deleteExpiredNotifications(LocalDateTime expireTime);
    
    /**
     * 统计未读通知数量
     */
    Long countUnreadNotifications(String receiver);
}