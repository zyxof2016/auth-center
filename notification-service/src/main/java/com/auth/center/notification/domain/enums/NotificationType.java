package com.auth.center.notification.domain.enums;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    
    /**
     * 邮件通知
     */
    EMAIL("EMAIL", "邮件通知"),
    
    /**
     * 短信通知
     */
    SMS("SMS", "短信通知"),
    
    /**
     * 站内通知
     */
    IN_APP("IN_APP", "站内通知"),
    
    /**
     * 微信通知
     */
    WECHAT("WECHAT", "微信通知"),
    
    /**
     * 钉钉通知
     */
    DINGTALK("DINGTALK", "钉钉通知");
    
    private final String code;
    private final String description;
    
    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid notification type code: " + code);
    }
}