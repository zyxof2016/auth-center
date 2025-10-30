package com.auth.center.message.infrastructure.service;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.message.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 消息发送服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {
    
    private final RocketMQTemplate rocketMQTemplate;
    private final MessageRepository messageRepository;
    
    /**
     * 发送消息到RocketMQ
     */
    public boolean sendMessage(MessageEntity messageEntity) {
        try {
            // 构建RocketMQ消息
            Message<String> rocketMessage = MessageBuilder.withPayload(messageEntity.getBody())
                    .setHeader("KEYS", messageEntity.getKeys())
                    .setHeader("TAGS", messageEntity.getTags())
                    .build();
            
            // 发送消息
            if (messageEntity.getDelayLevel() != null && messageEntity.getDelayLevel() > 0) {
                // 发送延迟消息
                rocketMQTemplate.syncSendDelayTimeSeconds(
                        messageEntity.getTopic(), 
                        rocketMessage, 
                        messageEntity.getDelayLevel());
            } else {
                // 发送普通消息
                rocketMQTemplate.syncSend(messageEntity.getTopic(), rocketMessage);
            }
            
            // 更新消息状态为发送成功
            messageEntity.setSendSuccess();
            messageRepository.save(messageEntity);
            
            log.info("消息发送成功: messageId={}, topic={}", messageEntity.getId(), messageEntity.getTopic());
            return true;
        } catch (Exception e) {
            // 更新消息状态为发送失败
            messageEntity.setSendFailed(e.getMessage());
            messageRepository.save(messageEntity);
            
            log.error("消息发送失败: messageId={}, topic={}, error={}", 
                    messageEntity.getId(), messageEntity.getTopic(), e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 重试发送失败的消息
     */
    public boolean retrySendMessage(MessageEntity messageEntity) {
        try {
            // 增加重试次数
            messageEntity.incrementRetryCount();
            messageRepository.save(messageEntity);
            
            // 发送消息
            Message<String> rocketMessage = MessageBuilder.withPayload(messageEntity.getBody())
                    .setHeader("KEYS", messageEntity.getKeys())
                    .setHeader("TAGS", messageEntity.getTags())
                    .build();
            
            rocketMQTemplate.syncSend(messageEntity.getTopic(), rocketMessage);
            
            // 更新消息状态为发送成功
            messageEntity.setSendSuccess();
            messageRepository.save(messageEntity);
            
            log.info("消息重试发送成功: messageId={}, topic={}", messageEntity.getId(), messageEntity.getTopic());
            return true;
        } catch (Exception e) {
            // 更新消息状态为发送失败
            messageEntity.setSendFailed(e.getMessage());
            messageRepository.save(messageEntity);
            
            log.error("消息重试发送失败: messageId={}, topic={}, retryCount={}, error={}", 
                    messageEntity.getId(), messageEntity.getTopic(), messageEntity.getRetryCount(), 
                    e.getMessage(), e);
            return false;
        }
    }
}