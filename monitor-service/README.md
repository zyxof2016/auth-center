# 监控服务 (monitor-service)

## 简介
监控服务基于Spring Boot Admin和Elasticsearch实现，提供系统监控、告警和健康检查功能。该服务遵循COLA架构设计，具备完整的监控指标收集、存储和告警能力。

## 功能特性
- 系统指标监控（CPU、内存、线程等）
- 服务健康状态检查
- 告警规则配置和管理
- 告警触发和通知
- 监控数据可视化（集成Spring Boot Admin）
- 基于Elasticsearch的数据存储

## 技术栈
- Spring Boot Admin Server
- Spring Boot Actuator
- Elasticsearch
- Micrometer Prometheus
- COLA架构

## 核心组件

### 1. 系统指标收集
定时收集JVM和系统指标，包括：
- CPU使用率
- 内存使用情况
- 线程数
- GC信息

### 2. 服务健康检查
定时检查服务的健康状态，包括：
- 服务可用性
- 响应时间
- 错误信息

### 3. 告警管理
支持自定义告警规则：
- 指标阈值告警
- 告警级别设置
- 告警处理和恢复

## API接口

### 记录系统指标
```
POST /api/monitor/metrics
```

### 更新服务健康状态
```
POST /api/monitor/health
```

### 创建告警规则
```
POST /api/monitor/alert/rule
```

### 记录告警
```
POST /api/monitor/alert/record
```

### 获取系统指标历史数据
```
GET /api/monitor/metrics/history
```

### 获取服务健康状态列表
```
GET /api/monitor/health/list
```

### 获取活跃告警列表
```
GET /api/monitor/alert/active
```

### 处理告警
```
PUT /api/monitor/alert/{alertId}/handle
```

## 配置说明
- Spring Boot Admin地址：`http://localhost:8080`
- Elasticsearch地址：`localhost:9200`
- Nacos配置中心：`localhost:8848`
- 定时任务：每30秒收集指标，每分钟检查健康状态和评估告警规则