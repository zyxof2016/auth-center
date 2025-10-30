# 通知服务 (notification-service)

## 简介
通知服务提供统一的通知发送和管理功能，支持多种通知渠道（邮件、短信、站内通知等）。该服务遵循COLA架构设计，具备完整的通知生命周期管理能力。

## 功能特性
- 多渠道通知发送（邮件、短信、站内通知）
- 通知状态跟踪
- 通知重试机制
- 未读通知统计
- 实时通知推送（WebSocket）
- 通知模板管理

## 技术栈
- Spring Boot
- Spring Mail
- Spring WebSocket
- COLA架构
- 阿里云短信服务
- 腾讯云短信服务

## 支持的通知类型
- EMAIL: 邮件通知
- SMS: 短信通知
- IN_APP: 站内通知
- WECHAT: 微信通知
- DINGTALK: 钉钉通知

## API接口

### 发送通知
```
POST /api/notification/send
参数：NotificationEntity对象
```

### 更新通知状态
```
PUT /api/notification/{notificationId}/status
参数：
- status: 通知状态
- errorMessage: 错误信息（可选）
```

### 分页查询通知列表
```
GET /api/notification
参数：
- notificationType: 通知类型（可选）
- receiver: 接收人（可选）
- status: 通知状态（可选）
- startTime: 开始时间（可选）
- endTime: 结束时间（可选）
- page: 页码（可选，默认1）
- size: 每页大小（可选，默认10）
```

### 获取用户通知列表
```
GET /api/notification/user/{receiver}
参数：
- maxCount: 最大数量（可选，默认10）
```

### 重试失败通知
```
POST /api/notification/retry
```

### 标记通知为已读
```
PUT /api/notification/{notificationId}/read
```

### 获取未读通知数量
```
GET /api/notification/unread-count/{receiver}
```

## 配置说明
- 邮件配置：`spring.mail.*`
- 短信服务配置：`sms.aliyun.*`, `sms.tencent.*`
- WebSocket配置：`spring.websocket.*`
- RocketMQ配置：`rocketmq.*`