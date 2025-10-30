package com.auth.center.message.domain.repository;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息仓库接口
 */
public interface MessageRepository {
    
    /**
     * 保存消息
     */
    MessageEntity save(MessageEntity message);
    
    /**
     * 根据ID查找消息
     */
    Optional<MessageEntity> findById(Long id);
    
    /**
     * 根据条件分页查询消息
     */
    Page<MessageEntity> findByConditions(String topic, MessageStatus status, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       Pageable pageable);
    
    /**
     * 查找待处理消息
     */
    List<MessageEntity> findPendingMessages(String topic, int maxCount);
    
    /**
     * 查找失败消息
     */
    List<MessageEntity> findFailedMessages(String topic);
    
    /**
     * 删除过期消息
     */
    void deleteExpiredMessages(LocalDateTime expireTime);
    
    /**
     * 根据键查找消息
     */
    Optional<MessageEntity> findByKeys(String keys);
}