package com.auth.center.message.application.service;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.message.domain.repository.MessageRepository;
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
 * 消息应用服务
 */
@Service
@RequiredArgsConstructor
public class MessageApplicationService {
    
    private final MessageRepository messageRepository;
    
    /**
     * 发送消息
     */
    public SingleResponse<MessageEntity> sendMessage(MessageEntity message) {
        message.setStatus(MessageStatus.SENDING.getCode());
        message.setSendTime(LocalDateTime.now());
        message.setCreatedTime(LocalDateTime.now());
        
        MessageEntity savedMessage = messageRepository.save(message);
        return SingleResponse.of(savedMessage);
    }
    
    /**
     * 更新消息状态
     */
    public Response updateMessageStatus(Long messageId, MessageStatus status, String errorMessage) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        message.setStatus(status.getCode());
        if (MessageStatus.CONSUME_SUCCESS.equals(status)) {
            message.setConsumeTime(LocalDateTime.now());
        }
        if (errorMessage != null) {
            message.setErrorMessage(errorMessage);
        }
        
        messageRepository.save(message);
        return Response.buildSuccess();
    }
    
    /**
     * 获取待处理消息
     */
    public List<MessageEntity> getPendingMessages(String topic, int maxCount) {
        return messageRepository.findPendingMessages(topic, maxCount);
    }
    
    /**
     * 分页查询消息列表
     */
    public PageResponse<MessageEntity> getMessagePage(String topic, MessageStatus status, 
                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                    int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MessageEntity> messagePage = messageRepository.findByConditions(
                topic, status, startTime, endTime, pageRequest);
        
        return PageResponse.of(messagePage.getContent(), messagePage.getTotalElements(), page, size);
    }
    
    /**
     * 重试失败消息
     */
    public Response retryFailedMessages(String topic) {
        List<MessageEntity> failedMessages = messageRepository.findFailedMessages(topic);
        
        for (MessageEntity message : failedMessages) {
            if (message.getRetryCount() < message.getMaxRetryCount()) {
                message.setStatus(MessageStatus.RETRYING.getCode());
                message.setRetryCount(message.getRetryCount() + 1);
                messageRepository.save(message);
            }
        }
        
        return Response.buildSuccess();
    }
    
    /**
     * 删除过期消息
     */
    public Response deleteExpiredMessages(LocalDateTime expireTime) {
        messageRepository.deleteExpiredMessages(expireTime);
        return Response.buildSuccess();
    }
}