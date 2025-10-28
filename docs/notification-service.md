# 通知服务设计文档

## 1. 通知服务概述

### 1.1 设计目标
提供统一的通知发送平台，支持邮件、短信、站内通知等多种通知方式，实现通知模板管理、发送状态跟踪、失败重试等功能。

### 1.2 技术选型
- **邮件发送**: Spring Mail + JavaMail + 邮件模板引擎
- **短信发送**: 阿里云短信/腾讯云短信 + 多服务商适配
- **站内通知**: WebSocket + Redis Pub/Sub
- **模板引擎**: Thymeleaf + FreeMarker
- **异步处理**: RocketMQ + Spring Async

## 2. 通知服务架构设计

### 2.1 整体架构
```
┌─────────────────────────────────────────────────────────────┐
│                   通知服务 (Notification Service)              │
├─────────────────────────────────────────────────────────────┤
│ 邮件发送 │ 短信发送 │ 站内通知 │ 模板管理 │ 发送记录 │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                   消息队列 (RocketMQ)                        │
├─────────────────────────────────────────────────────────────┤
│ 异步通知 │ 批量发送 │ 失败重试 │ 发送限流 │ 状态跟踪 │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────┬─────────┬─────────┬─────────┬─────────┬─────────┐
│邮件服务商│短信服务商│推送服务商│模板存储 │配置中心 │监控告警 │
│SMTP/API │阿里/腾讯│WebSocket│数据库/OSS│Nacos    │Prometheus│
└─────────┴─────────┴─────────┴─────────┴─────────┴─────────┘
```

### 2.2 数据库设计

#### 2.2.1 通知模板表 (sys_notification_template)
```sql
CREATE TABLE sys_notification_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(32) NOT NULL COMMENT '模板类型: EMAIL/SMS/PUSH',
    template_content TEXT NOT NULL COMMENT '模板内容',
    template_variables JSON COMMENT '模板变量定义',
    channel_type VARCHAR(32) COMMENT '渠道类型',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    tenant_id BIGINT COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code_tenant (template_code, tenant_id)
) COMMENT='通知模板表';
```

#### 2.2.2 通知发送记录表 (sys_notification_record)
```sql
CREATE TABLE sys_notification_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notification_id VARCHAR(64) NOT NULL COMMENT '通知ID',
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
    notification_type VARCHAR(32) NOT NULL COMMENT '通知类型: EMAIL/SMS/PUSH',
    receiver VARCHAR(512) NOT NULL COMMENT '接收者',
    title VARCHAR(256) COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    template_variables JSON COMMENT '模板变量值',
    send_status VARCHAR(32) NOT NULL COMMENT '发送状态: PENDING/SENDING/SUCCESS/FAILED',
    send_result TEXT COMMENT '发送结果',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    max_retry_count INT DEFAULT 3 COMMENT '最大重试次数',
    next_retry_time DATETIME COMMENT '下次重试时间',
    send_time DATETIME COMMENT '发送时间',
    complete_time DATETIME COMMENT '完成时间',
    tenant_id BIGINT COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_receiver (receiver(64)),
    INDEX idx_send_status (send_status),
    INDEX idx_created_time (created_time)
) COMMENT='通知发送记录表';
```

#### 2.2.3 通知渠道配置表 (sys_notification_channel)
```sql
CREATE TABLE sys_notification_channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    channel_code VARCHAR(64) NOT NULL COMMENT '渠道编码',
    channel_name VARCHAR(128) NOT NULL COMMENT '渠道名称',
    channel_type VARCHAR(32) NOT NULL COMMENT '渠道类型: EMAIL/SMS/PUSH',
    provider VARCHAR(64) NOT NULL COMMENT '服务提供商',
    config JSON NOT NULL COMMENT '渠道配置',
    priority INT DEFAULT 0 COMMENT '优先级',
    daily_limit INT DEFAULT 0 COMMENT '日发送限制',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    tenant_id BIGINT COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_channel_code_tenant (channel_code, tenant_id)
) COMMENT='通知渠道配置表';
```

## 3. 邮件发送功能设计

### 3.1 邮件发送流程

#### 3.1.1 同步邮件发送
```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    /**
     * 同步发送邮件
     */
    public SendResult sendEmailSync(EmailRequest request) {
        try {
            // 1. 模板渲染
            String content = renderTemplate(request.getTemplateCode(), 
                                          request.getTemplateVariables());
            
            // 2. 构建邮件
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(content, true); // HTML格式
            
            if (request.getCc() != null) {
                helper.setCc(request.getCc());
            }
            
            // 3. 发送邮件
            mailSender.send(message);
            
            return SendResult.success(request.getNotificationId());
            
        } catch (Exception e) {
            log.error("邮件发送失败: {}", request.getNotificationId(), e);
            return SendResult.failed(request.getNotificationId(), e.getMessage());
        }
    }
}
```

#### 3.1.2 异步邮件发送
```java
@Service
public class AsyncEmailService {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    /**
     * 异步发送邮件（通过消息队列）
     */
    public void sendEmailAsync(EmailRequest request) {
        // 1. 保存发送记录（状态为PENDING）
        NotificationRecord record = saveNotificationRecord(request);
        
        // 2. 发送消息到队列
        Message<EmailRequest> message = MessageBuilder
            .withPayload(request)
            .setHeader("notificationId", record.getNotificationId())
            .build();
            
        rocketMQTemplate.send("NOTIFICATION_EMAIL_TOPIC", message);
    }
    
    /**
     * 邮件消息消费者
     */
    @RocketMQMessageListener(
        topic = "NOTIFICATION_EMAIL_TOPIC",
        consumerGroup = "email-notification-consumer-group"
    )
    public class EmailConsumer implements RocketMQListener<EmailRequest> {
        
        @Override
        public void onMessage(EmailRequest request) {
            // 异步处理邮件发送
            emailService.sendEmailSync(request);
        }
    }
}
```

### 3.2 邮件模板管理

#### 3.2.1 邮件模板示例
```java
// 用户注册欢迎邮件模板
{
  "templateCode": "USER_REGISTER_WELCOME",
  "templateName": "用户注册欢迎邮件",
  "templateType": "EMAIL",
  "templateContent": """
  <!DOCTYPE html>
  <html>
  <head>
      <meta charset="UTF-8">
      <title>欢迎加入我们</title>
  </head>
  <body>
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #1890ff;">欢迎加入认证中心</h2>
          <p>亲爱的 ${username}，</p>
          <p>感谢您注册认证中心账号！</p>
          <p>您的账号信息：</p>
          <ul>
              <li>用户名：${username}</li>
              <li>注册时间：${registerTime}</li>
          </ul>
          <p>请妥善保管您的账号信息。</p>
          <div style="margin-top: 30px; padding: 20px; background: #f5f5f5;">
              <p>如有任何问题，请联系我们：</p>
              <p>邮箱：support@auth-center.com</p>
          </div>
      </div>
  </body>
  </html>
  """,
  "templateVariables": [
    {"name": "username", "description": "用户名"},
    {"name": "registerTime", "description": "注册时间"}
  ]
}
```

#### 3.2.2 模板渲染服务
```java
@Service
public class TemplateRenderService {
    
    @Autowired
    private TemplateEngine templateEngine;
    
    /**
     * 渲染模板
     */
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        // 1. 获取模板
        NotificationTemplate template = getTemplateByCode(templateCode);
        
        // 2. 创建上下文
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        
        // 3. 渲染模板
        return templateEngine.process(template.getTemplateContent(), context);
    }
}
```

## 4. 短信发送功能设计

### 4.1 短信发送流程

#### 4.1.1 短信服务接口
```java
public interface SmsService {
    
    /**
     * 发送短信
     */
    SendResult sendSms(SmsRequest request);
    
    /**
     * 批量发送短信
     */
    BatchSendResult sendBatchSms(List<SmsRequest> requests);
    
    /**
     * 查询发送状态
     */
    QueryResult querySendStatus(String notificationId);
}
```

#### 4.1.2 阿里云短信实现
```java
@Service("aliyunSmsService")
public class AliyunSmsService implements SmsService {
    
    @Autowired
    private IAcsClient acsClient;
    
    @Override
    public SendResult sendSms(SmsRequest request) {
        try {
            SendSmsRequest smsRequest = new SendSmsRequest();
            smsRequest.setPhoneNumbers(request.getPhoneNumber());
            smsRequest.setSignName(request.getSignName());
            smsRequest.setTemplateCode(request.getTemplateCode());
            smsRequest.setTemplateParam(JSON.toJSONString(request.getTemplateParam()));
            
            SendSmsResponse response = acsClient.getAcsResponse(smsRequest);
            
            if ("OK".equals(response.getCode())) {
                return SendResult.success(request.getNotificationId())
                    .setExternalId(response.getBizId());
            } else {
                return SendResult.failed(request.getNotificationId(), response.getMessage());
            }
            
        } catch (Exception e) {
            log.error("阿里云短信发送失败: {}", request.getNotificationId(), e);
            return SendResult.failed(request.getNotificationId(), e.getMessage());
        }
    }
}
```

#### 4.1.3 短信服务工厂
```java
@Service
public class SmsServiceFactory {
    
    @Autowired
    private Map<String, SmsService> smsServiceMap;
    
    /**
     * 根据渠道获取短信服务
     */
    public SmsService getSmsService(String channelCode) {
        String beanName = channelCode.toLowerCase() + "SmsService";
        return smsServiceMap.get(beanName);
    }
    
    /**
     * 获取默认短信服务
     */
    public SmsService getDefaultSmsService() {
        return smsServiceMap.get("aliyunSmsService");
    }
}
```

### 4.2 短信模板管理

#### 4.2.1 短信模板示例
```java
// 验证码短信模板
{
  "templateCode": "SMS_VERIFICATION_CODE",
  "templateName": "验证码短信",
  "templateType": "SMS",
  "templateContent": "您的验证码是：${code}，${minutes}分钟内有效。如非本人操作，请忽略本短信。",
  "templateVariables": [
    {"name": "code", "description": "验证码"},
    {"name": "minutes", "description": "有效时间（分钟）"}
  ]
}

// 登录提醒短信模板
{
  "templateCode": "SMS_LOGIN_NOTICE", 
  "templateName": "登录提醒短信",
  "templateType": "SMS",
  "templateContent": "您的账号于${loginTime}在${device}登录，IP地址：${ipAddress}。如非本人操作，请及时修改密码。",
  "templateVariables": [
    {"name": "loginTime", "description": "登录时间"},
    {"name": "device", "description": "登录设备"},
    {"name": "ipAddress", "description": "IP地址"}
  ]
}
```

## 5. 站内通知功能设计

### 5.1 WebSocket实时通知

#### 5.1.1 WebSocket配置
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

#### 5.1.2 站内通知服务
```java
@Service
public class PushNotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 发送个人通知
     */
    public void sendToUser(String userId, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/notifications", 
            message
        );
    }
    
    /**
     * 发送广播通知
     */
    public void broadcast(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
    
    /**
     * 发送系统公告
     */
    public void sendAnnouncement(AnnouncementMessage announcement) {
        messagingTemplate.convertAndSend("/topic/announcements", announcement);
    }
}
```

### 5.2 站内通知消息格式

#### 5.2.1 通知消息结构
```java
@Data
public class NotificationMessage {
    private String id;
    private String type; // INFO/WARNING/ERROR/SUCCESS
    private String title;
    private String content;
    private Long timestamp;
    private String sender;
    private Map<String, Object> extra;
    private Boolean read = false;
}

// 示例：权限变更通知
{
  "id": "notify_123456",
  "type": "INFO",
  "title": "权限变更通知",
  "content": "您的角色权限已更新，请重新登录生效。",
  "timestamp": 1640995200000,
  "sender": "系统管理员",
  "extra": {
    "changeType": "ROLE_UPDATE",
    "effectiveTime": "2024-01-15 10:30:00"
  }
}
```

## 6. 统一通知发送接口

### 6.1 通知发送服务

#### 6.1.1 统一发送接口
```java
@Service
public class UnifiedNotificationService {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsServiceFactory smsServiceFactory;
    
    @Autowired
    private PushNotificationService pushService;
    
    /**
     * 统一发送通知
     */
    public UnifiedSendResult sendNotification(UnifiedNotificationRequest request) {
        UnifiedSendResult result = new UnifiedSendResult();
        
        // 发送邮件
        if (request.getEmailRequest() != null) {
            SendResult emailResult = emailService.sendEmailSync(request.getEmailRequest());
            result.setEmailResult(emailResult);
        }
        
        // 发送短信
        if (request.getSmsRequest() != null) {
            SmsService smsService = smsServiceFactory.getSmsService(request.getSmsChannel());
            SendResult smsResult = smsService.sendSms(request.getSmsRequest());
            result.setSmsResult(smsResult);
        }
        
        // 发送站内通知
        if (request.getPushRequest() != null) {
            pushService.sendToUser(request.getUserId(), request.getPushRequest());
            result.setPushResult(SendResult.success(request.getNotificationId()));
        }
        
        return result;
    }
}
```

#### 6.1.2 通知发送请求对象
```java
@Data
public class UnifiedNotificationRequest {
    private String notificationId;
    private String userId;
    
    // 邮件相关
    private EmailRequest emailRequest;
    
    // 短信相关
    private SmsRequest smsRequest;
    private String smsChannel = "aliyun";
    
    // 站内通知相关
    private NotificationMessage pushRequest;
}

@Data
public class EmailRequest {
    private String notificationId;
    private String[] to;
    private String[] cc;
    private String subject;
    private String templateCode;
    private Map<String, Object> templateVariables;
}

@Data
public class SmsRequest {
    private String notificationId;
    private String phoneNumber;
    private String templateCode;
    private Map<String, Object> templateParam;
    private String signName;
}
```

## 7. 通知发送策略

### 7.1 发送限流策略

#### 7.1.1 基于Redis的限流
```java
@Service
public class NotificationRateLimitService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 检查发送频率限制
     */
    public boolean checkRateLimit(String key, int maxCount, Duration duration) {
        String redisKey = "rate_limit:" + key;
        
        Long current = redisTemplate.opsForValue().increment(redisKey);
        if (current == 1) {
            // 第一次设置过期时间
            redisTemplate.expire(redisKey, duration);
        }
        
        return current <= maxCount;
    }
    
    /**
     * 检查用户发送限制
     */
    public boolean checkUserLimit(String userId, NotificationType type) {
        String key = userId + ":" + type.name().toLowerCase();
        
        switch (type) {
            case EMAIL:
                return checkRateLimit(key, 10, Duration.ofHours(1)); // 1小时最多10封
            case SMS:
                return checkRateLimit(key, 5, Duration.ofMinutes(1)); // 1分钟最多5条
            default:
                return true;
        }
    }
}
```

### 7.2 失败重试策略

#### 7.2.1 重试策略配置
```java
@Configuration
public class RetryConfig {
    
    @Bean
    public RetryTemplate emailRetryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(1000, 2, 5000)
            .retryOn(NotificationException.class)
            .build();
    }
    
    @Bean
    public RetryTemplate smsRetryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(2)
            .fixedBackoff(2000)
            .retryOn(NotificationException.class)
            .build();
    }
}
```

#### 7.2.2 带重试的发送服务
```java
@Service
public class RetryableNotificationService {
    
    @Autowired
    @Qualifier("emailRetryTemplate")
    private RetryTemplate emailRetryTemplate;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 带重试的邮件发送
     */
    public SendResult sendEmailWithRetry(EmailRequest request) {
        return emailRetryTemplate.execute(context -> {
            log.info("第{}次尝试发送邮件: {}", context.getRetryCount() + 1, request.getNotificationId());
            
            SendResult result = emailService.sendEmailSync(request);
            
            if (!result.isSuccess()) {
                throw new NotificationException("邮件发送失败: " + result.getMessage());
            }
            
            return result;
        });
    }
}
```

## 8. 监控和运维

### 8.1 监控指标

#### 8.1.1 关键监控指标
- **发送成功率**: 各渠道发送成功率
- **发送延迟**: 从接收到发送完成的时间
- **队列积压**: 待发送消息数量
- **错误类型分布**: 各类错误的数量和比例
- **渠道使用率**: 各通知渠道的使用情况

#### 8.1.2 自定义监控
```java
@Component
public class NotificationMetrics {
    
    private final MeterRegistry meterRegistry;
    
    private final Counter emailSentCounter;
    private final Counter smsSentCounter;
    private final Timer notificationTimer;
    
    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.emailSentCounter = Counter.builder("notification.email.sent")
            .description("邮件发送数量")
            .register(meterRegistry);
            
        this.smsSentCounter = Counter.builder("notification.sms.sent")
            .description("短信发送数量")
            .register(meterRegistry);
            
        this.notificationTimer = Timer.builder("notification.process.duration")
            .description("通知处理耗时")
            .register(meterRegistry);
    }
    
    public void recordEmailSent(boolean success) {
        emailSentCounter.increment();
        if (!success) {
            Counter.builder("notification.email.failed")
                .register(meterRegistry)
                .increment();
        }
    }
}
```

### 8.2 运维管理

#### 8.2.1 管理接口
```java
@RestController
@RequestMapping("/api/notification")
public class NotificationAdminController {
    
    @Autowired
    private NotificationRecordService recordService;
    
    /**
     * 查询发送记录
     */
    @GetMapping("/records")
    public PageResponse<NotificationRecord> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sendStatus,
            @RequestParam(required = false) String notificationType) {
        
        return recordService.getRecords(page, size, sendStatus, notificationType);
    }
    
    /**
     * 手动重试发送
     */
    @PostMapping("/records/{id}/retry")
    public Response retrySend(@PathVariable Long id) {
        return recordService.retrySend(id);
    }
    
    /**
     * 获取发送统计
     */
    @GetMapping("/statistics")
    public NotificationStatistics getStatistics(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        return recordService.getStatistics(startTime, endTime);
    }
}
```

这套通知服务设计提供了完整的邮件、短信、站内通知功能，支持高并发场景下的可靠发送，具备完善的监控和管理能力。