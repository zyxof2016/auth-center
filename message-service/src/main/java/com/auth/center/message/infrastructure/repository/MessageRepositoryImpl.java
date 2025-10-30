package com.auth.center.message.infrastructure.repository;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.message.domain.repository.MessageRepository;
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
 * 消息仓库实现类
 * 注意：这是一个简化的内存实现，实际项目中应该使用数据库存储
 */
@Repository
public class MessageRepositoryImpl implements MessageRepository {
    
    // 使用内存存储模拟数据库（实际项目中应该使用真实的数据库）
    private final ConcurrentHashMap<Long, MessageEntity> messageStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MessageEntity> messageKeyIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public MessageEntity save(MessageEntity message) {
        if (message.getId() == null) {
            message.setId(idGenerator.getAndIncrement());
        }
        messageStorage.put(message.getId(), message);
        if (message.getKeys() != null) {
            messageKeyIndex.put(message.getKeys(), message);
        }
        return message;
    }
    
    @Override
    public Optional<MessageEntity> findById(Long id) {
        return Optional.ofNullable(messageStorage.get(id));
    }
    
    @Override
    public Page<MessageEntity> findByConditions(String topic, MessageStatus status, 
                                               LocalDateTime startTime, LocalDateTime endTime, 
                                               Pageable pageable) {
        List<MessageEntity> filteredMessages = messageStorage.values().stream()
                .filter(message -> topic == null || message.getTopic().equals(topic))
                .filter(message -> status == null || message.getStatus().equals(status.getCode()))
                .filter(message -> startTime == null || message.getCreatedTime().isAfter(startTime) || message.getCreatedTime().equals(startTime))
                .filter(message -> endTime == null || message.getCreatedTime().isBefore(endTime) || message.getCreatedTime().equals(endTime))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        long total = messageStorage.values().stream()
                .filter(message -> topic == null || message.getTopic().equals(topic))
                .filter(message -> status == null || message.getStatus().equals(status.getCode()))
                .filter(message -> startTime == null || message.getCreatedTime().isAfter(startTime) || message.getCreatedTime().equals(startTime))
                .filter(message -> endTime == null || message.getCreatedTime().isBefore(endTime) || message.getCreatedTime().equals(endTime))
                .count();
        
        return new PageImpl<>(filteredMessages, pageable, total);
    }
    
    @Override
    public List<MessageEntity> findPendingMessages(String topic, int maxCount) {
        return messageStorage.values().stream()
                .filter(message -> message.getTopic().equals(topic))
                .filter(message -> message.getStatus().equals(MessageStatus.SENDING.getCode()) || 
                                   message.getStatus().equals(MessageStatus.RETRYING.getCode()))
                .limit(maxCount)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageEntity> findFailedMessages(String topic) {
        return messageStorage.values().stream()
                .filter(message -> message.getTopic().equals(topic))
                .filter(message -> message.getStatus().equals(MessageStatus.SEND_FAILED.getCode()) || 
                                   message.getStatus().equals(MessageStatus.CONSUME_FAILED.getCode()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteExpiredMessages(LocalDateTime expireTime) {
        messageStorage.entrySet().removeIf(entry -> entry.getValue().getCreatedTime().isBefore(expireTime));
        messageKeyIndex.entrySet().removeIf(entry -> entry.getValue().getCreatedTime().isBefore(expireTime));
    }
    
    @Override
    public Optional<MessageEntity> findByKeys(String keys) {
        return Optional.ofNullable(messageKeyIndex.get(keys));
    }
}