# 文件服务 (file-service)

## 简介
文件服务提供统一的文件存储和管理功能，支持多种存储后端（MinIO、阿里云OSS等）。该服务遵循COLA架构设计，具备完整的文件上传、下载、管理和存储能力。

## 功能特性
- 多存储后端支持（MinIO、阿里云OSS）
- 文件上传和下载
- 文件信息管理
- 文件分类和权限控制
- 文件元数据存储
- 支持大文件上传

## 技术栈
- Spring Boot
- MinIO SDK
- 阿里云OSS SDK
- COLA架构

## 支持的存储类型
- MinIO
- 阿里云OSS
- AWS S3（预留）
- Google Cloud Storage（预留）

## API接口

### 上传文件
```
POST /api/file/upload
参数：
- file: 文件内容
- tenantId: 租户ID
- userId: 用户ID
- username: 用户名
- category: 文件分类（可选，默认为other）
- isPublic: 是否公开（可选，默认为false）
- storageType: 存储类型（可选，默认为minio）
```

### 获取文件信息
```
GET /api/file/info/{fileId}
```

### 分页查询文件列表
```
GET /api/file
参数：
- tenantId: 租户ID
- fileName: 文件名（可选）
- category: 文件分类（可选）
- storageType: 存储类型（可选）
- page: 页码（可选，默认为1）
- size: 每页大小（可选，默认为10）
```

### 删除文件
```
DELETE /api/file/{fileId}
```

### 更新文件信息
```
PUT /api/file/{fileId}
参数：
- fileName: 文件名（可选）
- category: 文件分类（可选）
- description: 描述（可选）
- isPublic: 是否公开（可选）
```

### 获取用户文件列表
```
GET /api/file/user/{userId}
参数：
- tenantId: 租户ID
```

### 下载文件
```
GET /api/file/download/{fileId}
```

### 获取文件访问URL
```
GET /api/file/url/{fileId}
```

## 配置说明
- 文件存储类型配置：`file.storage.type`
- MinIO配置：`file.storage.minio.*`
- 阿里云OSS配置：`file.storage.oss.*`
- 文件上传大小限制：`spring.servlet.multipart.max-file-size`