# 分布式消息队列设计文档

## 1. 消息队列概述

### 1.1 设计目标
在高并发场景下，通过消息队列实现系统解耦、异步处理、流量削峰、最终一致性等目标，提升系统性能和可靠性。

### 1.2 技术选型
- **消息中间件**: RocketMQ 5.0（阿里云开源）
- **集成框架**: Spring Cloud Stream + RocketMQ Binder
- **监控工具**: RocketMQ Dashboard + Prometheus

## 2. 消息队列架构设计

### 2.1 整体架构
```
┌─────────────────────────────────────────────────────────────┐
│                   生产者 (Producer)                          │
├─────────────────────────────────────────────────────────────┤
│ 认证服务 │ 用户服务 │ 文件服务 │ 日志服务 │ 其他微服务 │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                   RocketMQ 集群                              │
├─────────────────────────────────────────────────────────────┤
│ NameServer集群 │ Broker集群 │ 高可用部署 │ 数据同步 │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                   消费者 (Consumer)                          │
├─────────────────────────────────────────────────────────────┤
│ 异步任务 │ 事件处理 │ 数据同步 │ 通知服务 │ 监控告警 │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 消息队列集群部署
```
RocketMQ集群:
├── NameServer集群 (3节点)
│   ├── ns1: 9876
│   ├── ns2: 9876  
│   └── ns3: 9876
├── Broker集群 (主从模式)
│   ├── Broker-A (Master) + Broker-A (Slave)
│   ├── Broker-B (Master) + Broker-B (Slave)
│   └── Broker-C (Master) + Broker-C (Slave)
└── RocketMQ Dashboard (监控界面)
```

## 3. 消息队列应用场景

### 3.1 异步任务处理

#### 3.1.1 用户注册异步处理
**场景**: 用户注册后需要执行多个耗时操作
```java
// 用户注册消息
{
  "topic": "USER_REGISTER_TOPIC",
  "tags": "register",
  "keys": "user_123456",
  "body": {
    "userId": 123456,
    "username": "testuser",
    "email": "test@example.com",
    "registerTime": "2024-01-15 10:30:00"
  }
}
```

**消费者处理**:
- 发送欢迎邮件
- 初始化用户权限
- 创建用户空间
- 记录注册统计

#### 3.1.2 文件处理异步化
**场景**: 大文件上传后的异步处理
```java
// 文件处理消息
{
  "topic": "FILE_PROCESS_TOPIC", 
  "tags": "video_process",
  "keys": "file_789012",
  "body": {
    "fileId": "file_789012",
    "fileName": "video.mp4",
    "fileType": "video/mp4",
    "fileSize": 104857600,
    "storagePath": "/videos/video_789012.mp4",
    "processType": ["thumbnail", "transcode", "metadata"]
  }
}
```

**消费者处理**:
- 生成视频缩略图
- 视频转码（多种分辨率）
- 提取视频元数据
- 更新文件处理状态

### 3.2 事件驱动架构

#### 3.2.1 权限变更事件
**场景**: 角色权限变更后通知相关服务
```java
// 权限变更消息
{
  "topic": "PERMISSION_CHANGE_TOPIC",
  "tags": "role_update",
  "keys": "role_456",
  "body": {
    "roleId": 456,
    "roleName": "管理员",
    "changedPermissions": ["system:user:add", "system:user:delete"],
    "affectedUserIds": [123, 124, 125],
    "changeTime": "2024-01-15 10:30:00",
    "operator": "admin"
  }
}
```

**消费者处理**:
- 更新用户权限缓存
- 记录权限变更日志
- 通知前端权限刷新
- 安全审计记录

#### 3.2.2 登录事件通知
**场景**: 用户登录成功后的相关处理
```java
// 登录事件消息
{
  "topic": "USER_LOGIN_TOPIC",
  "tags": "login_success", 
  "keys": "user_123456",
  "body": {
    "userId": 123456,
    "username": "testuser",
    "loginIp": "192.168.1.100",
    "loginTime": "2024-01-15 10:30:00",
    "deviceInfo": "Chrome/Windows"
  }
}
```

**消费者处理**:
- 记录登录日志
- 更新最后登录时间
- 安全检查（异地登录检测）
- 统计在线用户数

### 3.3 流量削峰填谷

#### 3.3.1 高并发写入削峰
**场景**: 高峰期大量操作日志写入
```java
// 操作日志消息
{
  "topic": "OPERATION_LOG_TOPIC",
  "tags": "user_operation",
  "keys": "log_20240115103000",
  "body": {
    "operationId": "op_789012",
    "userId": 123456,
    "operationType": "UPDATE",
    "operationModule": "用户管理",
    "operationDesc": "修改用户信息",
    "requestParams": "{...}",
    "ipAddress": "192.168.1.100",
    "executeTime": 150,
    "status": 1,
    "operationTime": "2024-01-15 10:30:00"
  }
}
```

**处理策略**:
- 异步批量写入数据库
- 高峰期积压消息，低谷期处理
- 监控消息堆积情况
- 动态调整消费者数量

#### 3.3.2 文件上传流量控制
**场景**: 大量用户同时上传文件
```java
// 文件上传完成消息
{
  "topic": "FILE_UPLOAD_COMPLETE_TOPIC",
  "tags": "upload_success",
  "keys": "file_789012",
  "body": {
    "fileId": "file_789012",
    "userId": 123456,
    "fileName": "document.pdf",
    "fileSize": 5242880,
    "uploadTime": "2024-01-15 10:30:00",
    "processPriority": "NORMAL"
  }
}
```

**处理策略**:
- 控制文件处理并发数
- 优先级队列处理
- 失败重试机制
- 处理进度监控

### 3.4 最终一致性场景

#### 3.4.1 分布式事务
**场景**: 跨服务的数据一致性保证
```java
// 分布式事务消息
{
  "topic": "DISTRIBUTED_TX_TOPIC",
  "tags": "user_create",
  "keys": "tx_123456",
  "body": {
    "transactionId": "tx_123456",
    "businessType": "USER_CREATE",
    "steps": [
      {
        "service": "user-service",
        "action": "createUser",
        "params": {...},
        "status": "PENDING"
      },
      {
        "service": "role-service", 
        "action": "assignDefaultRole",
        "params": {...},
        "status": "PENDING"
      }
    ],
    "createTime": "2024-01-15 10:30:00"
  }
}
```

**处理机制**:
- 本地事务表记录
- 消息可靠性投递
- 事务状态补偿
- 超时事务回滚

## 4. 消息队列配置设计

### 4.1 Topic和Tag设计

#### 4.1.1 Topic命名规范
```
业务领域_操作类型_TOPIC
示例:
- USER_REGISTER_TOPIC
- FILE_UPLOAD_TOPIC  
- PERMISSION_CHANGE_TOPIC
- OPERATION_LOG_TOPIC
```

#### 4.1.2 Tag设计原则
```
操作动作_业务细分
示例:
- register:success
- upload:complete
- permission:update
- login:success
```

### 4.2 消息配置参数

#### 4.2.1 生产者配置
```yaml
rocketmq:
  producer:
    group: auth-center-producer-group
    send-message-timeout: 3000
    compress-message-body-threshold: 4096
    max-message-size: 4194304
    retry-times-when-send-failed: 2
    retry-times-when-send-async-failed: 2
```

#### 4.2.2 消费者配置
```yaml
rocketmq:
  consumer:
    group: auth-center-consumer-group
    consume-thread-min: 5
    consume-thread-max: 32
    pull-batch-size: 32
    consume-message-batch-max-size: 1
    pull-interval: 0
```

## 5. 消息可靠性保障

### 5.1 消息发送可靠性

#### 5.1.1 同步发送（重要业务）
```java
@Service
public class ImportantMessageService {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    public SendResult sendImportantMessage(String topic, String tags, Object message) {
        try {
            Message<Object> msg = MessageBuilder
                .withPayload(message)
                .setHeader(MessageConst.PROPERTY_TOPIC, topic)
                .setHeader(MessageConst.PROPERTY_TAGS, tags)
                .build();
                
            return rocketMQTemplate.syncSend(topic, msg, 3000);
        } catch (Exception e) {
            // 记录发送失败日志
            // 触发告警
            throw new BusinessException("消息发送失败");
        }
    }
}
```

#### 5.1.2 异步发送（普通业务）
```java
@Service  
public class NormalMessageService {
    
    public void sendNormalMessage(String topic, String tags, Object message) {
        Message<Object> msg = MessageBuilder
            .withPayload(message)
            .setHeader(MessageConst.PROPERTY_TOPIC, topic)
            .setHeader(MessageConst.PROPERTY_TAGS, tags)
            .build();
            
        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                // 发送成功处理
                log.info("消息发送成功: {}", sendResult.getMsgId());
            }
            
            @Override
            public void onException(Throwable e) {
                // 发送失败处理
                log.error("消息发送失败", e);
                // 记录到本地重试表
            }
        });
    }
}
```

### 5.2 消息消费可靠性

#### 5.2.1 消费幂等性设计
```java
@Service
@RocketMQMessageListener(
    topic = "USER_REGISTER_TOPIC",
    consumerGroup = "user-register-consumer-group",
    selectorExpression = "register"
)
public class UserRegisterConsumer implements RocketMQListener<MessageExt> {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void onMessage(MessageExt message) {
        String msgId = message.getMsgId();
        String messageKey = message.getKeys();
        
        // 幂等性检查
        if (isMessageProcessed(msgId)) {
            log.info("消息已处理，跳过: {}", msgId);
            return;
        }
        
        try {
            // 业务处理
            processUserRegister(message);
            
            // 标记消息已处理
            markMessageProcessed(msgId);
            
        } catch (Exception e) {
            log.error("消息处理失败: {}", msgId, e);
            // 根据异常类型决定重试或进入死信队列
            throw new RuntimeException("处理失败，需要重试");
        }
    }
    
    private boolean isMessageProcessed(String msgId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("msg_processed:" + msgId));
    }
    
    private void markMessageProcessed(String msgId) {
        redisTemplate.opsForValue().set("msg_processed:" + msgId, "true", Duration.ofHours(24));
    }
}
```

#### 5.2.2 死信队列处理
```java
@Service
@RocketMQMessageListener(
    topic = "%DLQ%user-register-consumer-group",
    consumerGroup = "dlq-user-register-consumer-group"
)
public class UserRegisterDLQConsumer implements RocketMQListener<MessageExt> {
    
    @Override
    public void onMessage(MessageExt message) {
        log.warn("收到死信队列消息: {}", message.getMsgId());
        
        // 死信消息处理策略
        // 1. 记录到异常消息表
        // 2. 发送告警通知
        // 3. 人工干预处理
        handleDeadLetterMessage(message);
    }
}
```

## 6. 监控和运维

### 6.1 监控指标

#### 6.1.1 关键监控指标
- **消息堆积量**: 每个Topic的消息积压情况
- **发送/消费TPS**: 消息处理吞吐量
- **消息延迟**: 生产到消费的时间差
- **错误率**: 发送失败和消费失败的比例
- **消费者延迟**: 消费者处理消息的延迟

#### 6.1.2 告警规则
```yaml
alert_rules:
  - name: "消息堆积告警"
    condition: "topic_message_backlog > 10000"
    severity: "warning"
    
  - name: "消费失败率告警"  
    condition: "consumer_failure_rate > 5%"
    severity: "critical"
    
  - name: "消息延迟告警"
    condition: "message_delay > 300000" # 5分钟
    severity: "warning"
```

### 6.2 运维管理

#### 6.2.1 消息轨迹追踪
```java
// 消息轨迹配置
rocketmq:
  enable-msg-trace: true
  customized-trace-topic: RMQ_SYS_TRACE_TOPIC
```

#### 6.2.2 动态配置管理
```java
// 动态调整消费者线程数
@RestController
public class MQConfigController {
    
    @Autowired
    private DefaultMQPushConsumer consumer;
    
    @PostMapping("/mq/consumer/threads")
    public Response adjustConsumerThreads(@RequestParam int threadCount) {
        consumer.setConsumeThreadMax(threadCount);
        consumer.setConsumeThreadMin(threadCount);
        return Response.buildSuccess();
    }
}
```

## 7. 性能优化

### 7.1 消息压缩
```java
// 大消息压缩
rocketmq:
  producer:
    compress-message-body-threshold: 4096  # 4KB以上压缩
    compression-level: 5
```

### 7.2 批量消息
```java
// 批量发送消息
List<Message<String>> messages = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    Message<String> message = new Message<>("BATCH_TOPIC", "TagA", "Hello batch " + i);
    messages.add(message);
}

SendResult sendResult = rocketMQTemplate.syncSend("BATCH_TOPIC", messages, 3000);
```

### 7.3 顺序消息
```java
// 顺序消息发送
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.ORDERLY  // 顺序消费
)
public class OrderMessageConsumer implements RocketMQListener<MessageExt> {
    // 保证同一订单的消息顺序处理
}
```

## 8. 灾难恢复

### 8.1 数据备份
- **消息数据**: RocketMQ多副本同步
- **配置数据**: 配置中心备份
- **消费进度**: 消费位点持久化

### 8.2 故障转移
- **Broker故障**: 自动切换到Slave节点
- **NameServer故障**: 多节点冗余保障
- **网络分区**: 脑裂保护机制

这套消息队列设计能够有效支撑认证中心在高并发场景下的稳定运行，提供可靠的异步处理能力和系统解耦方案。