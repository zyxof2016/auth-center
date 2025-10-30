# 消息服务 (message-service)

## 简介
消息服务提供统一的消息发送、接收和管理功能，基于RocketMQ实现可靠的消息传递。该服务遵循COLA架构设计，具备完整的消息生命周期管理能力。

## 功能特性
- 消息发送和接收
- 消息状态跟踪
- 消息重试机制
- 消息过期清理
- 消息查询和管理
- 支持延迟消息
- 支持消息去重

## 技术栈
- Spring Boot
- RocketMQ
- Spring Cloud Stream
- COLA架构

## 消息状态
- SENDING: 发送中
- SEND_OK: 发送成功
- SEND_FAILED: 发送失败
- CONSUME_SUCCESS: 消费成功
- CONSUME_FAILED: 消费失败
- RETRYING: 重试中

## API接口

### 发送消息
```
POST /api/message/send
参数：MessageEntity对象
```

### 更新消息状态
```
PUT /api/message/{messageId}/status
参数：
- status: 消息状态
- errorMessage: 错误信息（可选）
```

### 获取待处理消息
```
GET /api/message/pending
参数：
- topic: 消息主题
- maxCount: 最大数量（可选，默认10）
```

### 分页查询消息列表
```
GET /api/message
参数：
- topic: 消息主题（可选）
- status: 消息状态（可选）
- startTime: 开始时间（可选）
- endTime: 结束时间（可选）
- page: 页码（可选，默认1）
- size: 每页大小（可选，默认10）
```

### 重试失败消息
```
POST /api/message/retry
参数：
- topic: 消息主题
```

### 删除过期消息
```
DELETE /api/message/expired
参数：
- expireTime: 过期时间
```

### 根据键查找消息
```
GET /api/message/key/{keys}
```

## 配置说明
- RocketMQ NameServer地址：`rocketmq.name-server`
- RocketMQ生产者组：`rocketmq.producer.group`
- 定时任务：每分钟重试失败消息，每天凌晨2点清理过期消息