package com.auth.center.message.domain.entity;

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
    private Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
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
}