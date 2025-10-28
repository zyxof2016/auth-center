# 认证中心分布式微服务系统

## 项目概述

基于Spring Cloud Alibaba和Vue.js的分布式认证授权中心，支持多租户、OAuth2.1、RBAC权限管理、大文件上传下载等核心功能。采用COLA架构模式，提供完整的认证授权解决方案。

## 🚀 核心特性

### 🔐 认证授权
- **多租户支持** - 完整的数据隔离和资源隔离
- **OAuth2.1协议** - 支持授权码、密码、客户端等多种认证模式
- **JWT令牌管理** - 无状态会话，支持令牌刷新
- **单点登录(SSO)** - 统一认证入口

### 🔒 权限管理  
- **RBAC权限模型** - 基于角色的访问控制
- **细粒度权限控制** - 菜单级+操作级权限控制
- **动态权限验证** - 实时权限校验和更新
- **数据权限隔离** - 支持不同级别的数据访问控制

### 📁 文件存储
- **多存储提供商** - 支持OSS、MinIO、S3、Google Cloud Storage
- **大文件分片上传** - 支持GB级别大文件上传，自动分片
- **断点续传功能** - 网络中断后自动恢复上传/下载
- **实时进度监控** - 上传/下载进度实时显示
- **文件校验机制** - MD5/SHA256文件完整性校验

### 📊 系统管理
- **用户管理** - 用户CRUD、状态管理、权限分配
- **角色管理** - 角色定义、权限配置、菜单分配
- **客户端管理** - OAuth2客户端注册和配置
- **日志审计** - 操作日志、登录日志记录分析
- **系统监控** - 服务健康监控、性能指标展示

## 🛠️ 技术栈

### 后端技术栈
- **核心框架**: Spring Boot 2.7.x + Spring Cloud Alibaba 2021.0.5.0
- **架构模式**: COLA 4.0 架构模式（Clean Architecture）
- **安全认证**: Spring Security + OAuth2.1 + JWT
- **数据存储**: MySQL 8.0 + Redis 7.0 + 对象存储（OSS/MinIO/S3）
- **服务治理**: Nacos 2.2.x（注册中心 + 配置中心）
- **消息队列**: RocketMQ 5.0 + Spring Cloud Stream（高并发异步处理）
- **API网关**: Spring Cloud Gateway + 动态路由 + 认证拦截
- **服务通信**: OpenFeign + 负载均衡
- **容错保护**: Sentinel 熔断降级 + 限流保护
- **分布式事务**: Seata AT模式
- **文件处理**: 分片上传 + 断点续传 + 多存储适配
- **系统监控**: Spring Boot Admin + Prometheus + Grafana + SkyWalking

### 前端技术栈
- **核心框架**: Vue 3 + TypeScript + Composition API
- **UI组件库**: Element Plus（现代化UI设计）
- **状态管理**: Pinia（轻量级状态管理）
- **路由管理**: Vue Router 4（动态路由 + 权限控制）
- **构建工具**: Vite（快速构建 + 热更新）
- **HTTP客户端**: Axios（请求拦截 + 统一错误处理）
- **文件上传**: 分片上传组件 + 进度显示 + 断点续传
- **工具库**: Crypto-js（文件校验）、File API（文件处理）

## 🏗️ 系统架构

### 微服务模块划分

```
auth-center/
├── auth-gateway          # API网关服务 - 路由转发、认证拦截、限流熔断
├── auth-server           # 认证授权服务 - OAuth2.1认证、JWT令牌管理
├── user-service          # 用户管理服务 - 用户CRUD、多租户支持
├── role-service          # 角色权限服务 - RBAC权限管理、菜单权限
├── client-service        # 客户端管理服务 - OAuth2客户端注册配置
├── log-service           # 日志服务 - 操作日志、审计日志记录
├── monitor-service       # 监控服务 - 系统监控、性能指标收集
├── file-service          # 文件存储服务 - 文件上传下载、大文件处理
└── common/              # 公共模块
    ├── common-core       # 核心公共模块 - 工具类、DTO、异常处理
    ├── common-security   # 安全公共模块 - JWT工具、权限注解
    └── common-web        # Web公共模块 - 统一响应、全局异常处理
```

### 大文件处理架构

```
大文件上传流程：
1. 前端检测文件大小 → 自动启用分片上传
2. 调用初始化接口 → 获取上传ID和分片URL
3. 并行上传分片 → 实时进度监控
4. 完成上传 → 合并分片生成文件
5. 支持断点续传 → 网络中断自动恢复

大文件下载流程：
1. 获取文件分片信息 → 支持Range请求
2. 并行下载分片 → 断点续传支持
3. 合并分片 → 生成完整文件
4. 文件校验 → MD5完整性验证
```

### 📊 数据库设计

#### 核心业务表

1. **用户表 (sys_user)** - 用户基本信息、多租户支持
2. **角色表 (sys_role)** - 角色定义、权限集合
3. **菜单表 (sys_menu)** - 系统菜单、权限标识
4. **客户端表 (sys_client)** - OAuth2客户端配置
5. **租户表 (sys_tenant)** - 多租户管理
6. **操作日志表 (sys_operation_log)** - 用户操作审计

#### 文件存储相关表

7. **文件表 (sys_file)** - 文件元数据信息
   - 文件ID、名称、大小、类型、存储路径
   - 存储提供商、存储桶、文件分类
   - MD5校验值、上传状态、访问权限

8. **文件分片表 (sys_file_chunk)** - 大文件分片信息
   - 分片索引、大小、ETag、上传状态
   - 分片上传时间、完成时间

9. **存储配置表 (sys_storage_config)** - 存储提供商配置
   - 存储类型、访问密钥、配置参数
   - 默认存储桶、配额限制

## ✨ 功能特性

### 🔐 认证授权功能
- ✅ **多租户支持** - 完整的数据隔离和资源隔离
- ✅ **OAuth2.1认证授权** - 支持授权码、密码、客户端模式
- ✅ **JWT令牌管理** - 无状态会话，支持令牌刷新
- ✅ **单点登录(SSO)** - 统一认证入口
- ✅ **图形验证码** - 登录安全防护

### 🔒 权限管理功能
- ✅ **RBAC权限模型** - 基于角色的访问控制
- ✅ **细粒度权限控制** - 菜单级+操作级权限
- ✅ **动态权限验证** - 实时权限校验和更新
- ✅ **数据权限隔离** - 支持不同级别的数据访问控制

### 📁 文件存储功能
- ✅ **多存储提供商** - OSS、MinIO、S3、Google Cloud Storage
- ✅ **大文件分片上传** - 支持GB级别大文件，自动分片处理
- ✅ **断点续传功能** - 网络中断后自动恢复上传/下载
- ✅ **实时进度监控** - 上传/下载进度和速度实时显示
- ✅ **文件校验机制** - MD5/SHA256文件完整性校验
- ✅ **文件预览支持** - 图片、文档、视频在线预览
- ✅ **存储空间管理** - 存储配额控制和空间监控

### 📊 系统管理功能
- ✅ **用户管理** - 用户CRUD、状态管理、权限分配
- ✅ **角色管理** - 角色定义、权限配置、菜单分配
- ✅ **菜单权限管理** - 菜单树管理、权限标识配置
- ✅ **客户端管理** - OAuth2客户端注册和配置管理
- ✅ **租户管理** - 多租户配置和数据隔离
- ✅ **日志查询** - 操作日志、登录日志记录分析
- ✅ **系统监控** - 服务健康监控、性能指标展示

### 🚀 高并发处理
- ✅ **消息队列异步处理** - RocketMQ实现系统解耦和流量削峰
- ✅ **异步任务处理** - 用户注册、文件处理等耗时操作异步化
- ✅ **事件驱动架构** - 权限变更、登录事件等实时通知
- ✅ **最终一致性保障** - 分布式事务消息可靠性投递
- ✅ **流量削峰填谷** - 高峰期消息积压，低谷期批量处理
- ✅ **消息可靠性保障** - 幂等消费、死信队列、重试机制

### 📧 通知服务功能
- ✅ **邮件发送服务** - 支持HTML邮件模板、附件发送、批量发送
- ✅ **短信发送服务** - 多服务商适配（阿里云、腾讯云等）、验证码发送
- ✅ **站内通知服务** - WebSocket实时推送、通知状态管理
- ✅ **多渠道通知管理** - 统一通知发送接口、模板变量替换
- ✅ **通知发送策略** - 发送限流、失败重试、发送状态跟踪
- ✅ **通知模板管理** - 可视化模板编辑、模板预览、多租户模板隔离

### 🛡️ 安全特性
- ✅ **防暴力破解** - 登录失败次数限制
- ✅ **密码加密** - BCrypt强加密存储
- ✅ **会话安全** - JWT无状态会话管理
- ✅ **API安全** - 接口权限验证和限流保护
- ✅ **数据安全** - SQL注入防护、XSS防护

## 🚀 快速开始

### 环境要求
- **JDK**: 8+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Node.js**: 16+
- **对象存储**: MinIO/OSS/S3（可选，用于文件存储）

### 启动步骤

#### 1. 环境准备
```bash
# 启动Nacos注册中心
docker run --name nacos -p 8848:8848 nacos/nacos-server:latest

# 启动Redis服务
docker run --name redis -p 6379:6379 redis:7.0-alpine

# 启动MinIO（文件存储）
docker run --name minio -p 9000:9000 -p 9001:9001 minio/minio server /data --console-address ":9001"
```

#### 2. 数据库初始化
```sql
-- 创建数据库和导入脚本
CREATE DATABASE auth_center;
-- 导入 docs/database.md 中的表结构
```

#### 3. 后端服务启动
```bash
# 启动网关服务
cd auth-gateway && mvn spring-boot:run

# 启动认证服务  
cd auth-server && mvn spring-boot:run

# 启动其他微服务...
```

#### 4. 前端应用启动
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 🧪 测试大文件上传

#### 前端代码示例
```javascript
// 使用大文件上传组件
<LargeFileUploader 
  :chunk-size="5 * 1024 * 1024"
  :max-size="2 * 1024 * 1024 * 1024"
  @progress="handleProgress"
  @success="handleSuccess"
  @error="handleError"
/>

// 手动控制上传
const uploader = new LargeFileUploader(file, 5 * 1024 * 1024)
await uploader.initUpload()
await uploader.uploadAllChunks()
const fileInfo = await uploader.completeUpload()
```

#### API调用示例
```bash
# 初始化大文件上传
curl -X POST /api/files/large/init \
  -H "Authorization: Bearer {token}" \
  -d '{
    "fileName": "large_video.mp4",
    "fileSize": 104857600,
    "chunkSize": 5242880
  }'

# 上传分片
curl -X PUT /api/files/large/upload/{uploadId}/1 \
  -H "Content-Type: application/octet-stream" \
  --data-binary @chunk1.dat
```

## 📦 部署说明

### 开发环境
- **Docker Compose** - 快速搭建完整环境
- **热部署支持** - 开发时自动重启
- **本地调试** - 支持远程调试和日志查看

### 生产环境  
- **Kubernetes集群** - 高可用部署
- **健康检查** - 服务健康状态监控
- **弹性伸缩** - 根据负载自动扩缩容
- **监控告警** - Prometheus + Grafana监控体系
- **日志收集** - ELK日志分析平台

## 📚 文档目录

- [🏗️ 架构设计文档](./docs/architecture.md) - 系统架构和模块设计
- [🗄️ 数据库设计文档](./docs/database.md) - 数据库表结构和关系
- [🔌 API接口文档](./docs/api.md) - 完整的API接口说明
- [🚀 部署运维文档](./docs/deployment.md) - 部署和运维指南
- [🎨 前端开发文档](./docs/frontend.md) - 前端架构和开发指南
- [📁 文件存储指南](./docs/file-storage.md) - 大文件上传下载详细说明

## 🤝 贡献指南

欢迎提交Issue和Pull Request来完善这个项目！

## 📄 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。