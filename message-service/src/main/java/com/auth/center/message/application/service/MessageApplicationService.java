package com.auth.center.message.application.service;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.message.domain.repository.MessageRepository;
import com.auth.center.message.infrastructure.service.MessageSendService;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息应用服务
 */
@Service
@RequiredArgsConstructor
public class MessageApplicationService {
    
    private final MessageRepository messageRepository;
    private final MessageSendService messageSendService;
    
    /**
     * 发送消息
     */
    public SingleResponse<MessageEntity> sendMessage(MessageEntity message) {
        message.setCreatedTime(LocalDateTime.now());
        
        // 保存消息到数据库
        MessageEntity savedMessage = messageRepository.save(message);
        
        // 异步发送消息到RocketMQ
        messageSendService.sendMessage(savedMessage);
        
        return SingleResponse.of(savedMessage);
    }
    
    /**
     * 更新消息状态
     */
    public Response updateMessageStatus(Long messageId, MessageStatus status, String errorMessage) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        switch (status) {
            case SEND_OK:
                message.setSendSuccess();
                break;
            case SEND_FAILED:
                message.setSendFailed(errorMessage);
                break;
            case CONSUME_SUCCESS:
                message.setConsumeSuccess();
                break;
            case CONSUME_FAILED:
                message.setConsumeFailed(errorMessage);
                break;
            default:
                message.setStatus(status.getCode());
                break;
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
        
        return PageResponse.of(messagePage.getContent(), page, size, messagePage.getTotalElements());
    }
    
    /**
     * 重试失败消息
     */
    public Response retryFailedMessages(String topic) {
        List<MessageEntity> failedMessages = messageRepository.findFailedMessages(topic);
        
        for (MessageEntity message : failedMessages) {
            if (message.canRetry()) {
                messageSendService.retrySendMessage(message);
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
    
    /**
     * 根据键查找消息
     */
    public SingleResponse<MessageEntity> getMessageByKey(String keys) {
        MessageEntity message = messageRepository.findByKeys(keys)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        return SingleResponse.of(message);
    }
}