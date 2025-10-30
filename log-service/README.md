# 日志服务 (log-service)

## 简介
日志服务负责收集、存储和查询系统中的各种日志信息，包括操作日志和登录日志。该服务基于COLA架构设计，使用Elasticsearch作为存储引擎，通过RocketMQ接收日志消息。

## 功能特性
- 操作日志记录
- 登录日志记录
- 日志查询与分页
- 日志统计分析
- 基于RocketMQ的消息驱动

## 技术栈
- Spring Boot
- Elasticsearch
- RocketMQ
- COLA架构

## API接口

### 记录操作日志
```
POST /api/log/operation
```

### 记录登录日志
```
POST /api/log/login
```

### 查询操作日志
```
GET /api/log/operation
```

### 查询登录日志
```
GET /api/log/login
```

### 统计操作日志数量
```
GET /api/log/operation/count
```

### 统计登录日志数量
```
GET /api/log/login/count
```

## 消息格式
通过RocketMQ发送日志消息，消息格式如下：
```json
{
  "type": "operation|login",
  "data": "{...}"  // 具体的日志数据JSON字符串
}
```

## 配置说明
- Elasticsearch地址：`localhost:9200`
- RocketMQ NameServer：`localhost:9876`
- Nacos配置中心：`localhost:8848`