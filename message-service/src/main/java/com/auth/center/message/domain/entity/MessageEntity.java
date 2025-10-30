package com.auth.center.message.domain.entity;

import com.auth.center.message.domain.enums.MessageStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
public class MessageEntity {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 消息主题
     */
    private String topic;
    
    /**
     * 消息标签
     */
    private String tags;
    
    /**
     * 消息键
     */
    private String keys;
    
    /**
     * 消息体
     */
    private String body;
    
    /**
     * 消息状态
     */
    private String status;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 消费时间
     */
    private LocalDateTime consumeTime;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;
    
    /**
     * 延迟级别
     */
    private Integer delayLevel;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 创建消息
     */
    public static MessageEntity create(Long tenantId, String topic, String tags, String keys, 
                                     String body, Integer delayLevel, Integer maxRetryCount) {
        MessageEntity message = new MessageEntity();
        message.tenantId = tenantId;
        message.topic = topic;
        message.tags = tags;
        message.keys = keys;
        message.body = body;
        message.delayLevel = delayLevel;
        message.maxRetryCount = maxRetryCount != null ? maxRetryCount : 3;
        message.retryCount = 0;
        message.status = MessageStatus.SENDING.getCode();
        message.createdTime = LocalDateTime.now();
        return message;
    }
    
    /**
     * 设置发送成功状态
     */
    public void setSendSuccess() {
        this.status = MessageStatus.SEND_OK.getCode();
        this.sendTime = LocalDateTime.now();
    }
    
    /**
     * 设置发送失败状态
     */
    public void setSendFailed(String errorMessage) {
        this.status = MessageStatus.SEND_FAILED.getCode();
        this.errorMessage = errorMessage;
        this.sendTime = LocalDateTime.now();
    }
    
    /**
     * 设置消费成功状态
     */
    public void setConsumeSuccess() {
        this.status = MessageStatus.CONSUME_SUCCESS.getCode();
        this.consumeTime = LocalDateTime.now();
    }
    
    /**
     * 设置消费失败状态
     */
    public void setConsumeFailed(String errorMessage) {
        this.status = MessageStatus.CONSUME_FAILED.getCode();
        this.errorMessage = errorMessage;
        this.consumeTime = LocalDateTime.now();
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount = this.retryCount + 1;
        this.status = MessageStatus.RETRYING.getCode();
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount;
    }
    
    /**
     * 获取消息状态枚举
     */
    public MessageStatus getMessageStatus() {
        return MessageStatus.fromCode(this.status);
    }
}