package com.auth.center.auth.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.verification-code")
public class VerificationCodeConfig {
    
    /**
     * 验证码长度
     */
    private int length = 6;
    
    /**
     * 验证码过期时间（秒）
     */
    private int expireSeconds = 300;
    
    /**
     * 验证码发送频率限制
     */
    private FrequencyLimit frequencyLimit = new FrequencyLimit();
    
    /**
     * 短信服务配置
     */
    private SmsConfig sms = new SmsConfig();
    
    /**
     * 邮件服务配置
     */
    private EmailConfig email = new EmailConfig();
    
    /**
     * 频率限制配置
     */
    @Data
    public static class FrequencyLimit {
        /**
         * 同一接收者1分钟内最多发送次数
         */
        private int perMinute = 1;
        
        /**
         * 同一IP地址1小时内最多发送次数
         */
        private int perHourByIp = 10;
        
        /**
         * 同一接收者24小时内最多发送次数
         */
        private int perDayByReceiver = 20;
    }
    
    /**
     * 短信服务配置
     */
    @Data
    public static class SmsConfig {
        /**
         * 短信服务商
         */
        private String provider = "aliyun";
        
        /**
         * 短信签名
         */
        private String signName;
        
        /**
         * 模板ID
         */
        private String templateId;
        
        /**
         * 是否启用
         */
        private boolean enabled = true;
    }
    
    /**
     * 邮件服务配置
     */
    @Data
    public static class EmailConfig {
        /**
         * 发件人邮箱
         */
        private String from;
        
        /**
         * 邮件主题
         */
        private String subject = "验证码";
        
        /**
         * 邮件模板
         */
        private String template;
        
        /**
         * 是否启用
         */
        private boolean enabled = true;
    }
}