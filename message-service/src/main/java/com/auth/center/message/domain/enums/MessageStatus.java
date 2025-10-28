package com.auth.center.message.domain.enums;

/**
 * 消息状态枚举
 */
public enum MessageStatus {
    
    /**
     * 发送中
     */
    SENDING("SENDING", "发送中"),
    
    /**
     * 发送成功
     */
    SEND_OK("SEND_OK", "发送成功"),
    
    /**
     * 发送失败
     */
    SEND_FAILED("SEND_FAILED", "发送失败"),
    
    /**
     * 消费成功
     */
    CONSUME_SUCCESS("CONSUME_SUCCESS", "消费成功"),
    
    /**
     * 消费失败
     */
    CONSUME_FAILED("CONSUME_FAILED", "消费失败"),
    
    /**
     * 重试中
     */
    RETRYING("RETRYING", "重试中");
    
    private final String code;
    private final String description;
    
    MessageStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MessageStatus fromCode(String code) {
        for (MessageStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid message status code: " + code);
    }
}