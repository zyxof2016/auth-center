package com.auth.center.message.infrastructure.service;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息调度服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageScheduleService {
    
    private final MessageRepository messageRepository;
    private final MessageSendService messageSendService;
    
    /**
     * 定时重试发送失败的消息
     */
    @Scheduled(fixedDelay = 60000) // 每分钟执行一次
    public void retryFailedMessages() {
        try {
            // 获取所有失败的消息
            List<MessageEntity> failedMessages = messageRepository.findFailedMessages(null);
            
            for (MessageEntity message : failedMessages) {
                // 检查是否可以重试
                if (message.canRetry()) {
                    log.info("开始重试发送消息: messageId={}", message.getId());
                    messageSendService.retrySendMessage(message);
                } else {
                    log.warn("消息已达到最大重试次数，不再重试: messageId={}, retryCount={}", 
                            message.getId(), message.getRetryCount());
                }
            }
        } catch (Exception e) {
            log.error("定时重试失败消息时发生错误", e);
        }
    }
    
    /**
     * 定时清理过期消息
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanExpiredMessages() {
        try {
            // 删除30天前的消息
            java.time.LocalDateTime expireTime = java.time.LocalDateTime.now().minusDays(30);
            messageRepository.deleteExpiredMessages(expireTime);
            log.info("清理过期消息完成，清理时间: {}", expireTime);
        } catch (Exception e) {
            log.error("定时清理过期消息时发生错误", e);
        }
    }
}