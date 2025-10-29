# API接口文档

## 1. API设计规范

### 1.1 请求规范
- **协议**: HTTP/HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: Bearer Token
- **请求头**: `Content-Type: application/json`
- **多租户支持**: 通过`X-Tenant-Id`请求头传递租户ID
- **基础URL**: `http://localhost:8080` (网关地址)

### 1.4 服务端口配置

#### 微服务端口分配
| 服务名称 | 端口 | 服务路径 | 说明 |
|---------|------|----------|------|
| auth-gateway | 8080 | / | API网关服务 |
| auth-server | 8001 | /auth | 认证服务 |
| user-service | 8002 | /api/users | 用户服务 |
| role-service | 8082 | /api/roles | 角色服务 |
| client-service | 8083 | /api/clients | 客户端服务 |
| log-service | 8084 | /api/logs | 日志服务 |
| monitor-service | 8085 | /api/monitor | 监控服务 |
| file-service | 8086 | /api/files | 文件服务 |
| message-service | 8087 | /api/messages | 消息服务 |
| notification-service | 8088 | /api/notifications | 通知服务 |

#### 基础设施端口
| 组件 | 端口 | 说明 |
|------|------|------|
| Nacos | 8848 | 服务注册与发现 |
| Redis | 6379 | 缓存服务 |
| MySQL | 3306 | 数据库服务 |
| Sentinel Dashboard | 8089 | 流量控制面板 |
| MinIO | 9000/9001 | 对象存储服务 |
| RocketMQ | 9876 | 消息队列服务 |

### 1.2 响应规范（基于COLA框架）

#### 基础响应格式
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "success",
  "data": {},
  "timestamp": 1640995200000
}
```

#### 响应类型
- **`Response`**: 无数据返回的操作
- **`SingleResponse<T>`**: 单条数据响应
- **`MultiResponse<T>`**: 多条数据响应（包含`total`字段）
- **`PageResponse<T>`**: 分页数据响应（包含`pageNum`、`pageSize`、`total`、`pages`字段）

#### 响应示例
```java
// 无数据操作成功
Response.buildSuccess();

// 单条数据响应
SingleResponse.of(userDTO);

// 多条数据响应
MultiResponse.of(userList);

// 分页数据响应
PageResponse.of(pageData, pageNum, pageSize, total);
```

### 1.3 服务间调用规范

#### Feign客户端调用
项目使用Spring Cloud OpenFeign进行服务间调用，所有Feign客户端接口位于`common-web`模块中：

```java
// 用户服务Feign客户端
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    
    @GetMapping("/{id}")
    SingleResponse<UserDTO> getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/username/{username}")
    SingleResponse<UserDTO> getUserByUsername(@PathVariable("username") String username);
}

// 角色服务Feign客户端
@FeignClient(name = "role-service", path = "/api/roles")
public interface RoleServiceClient {
    
    @GetMapping("/user/{userId}")
    MultiResponse<RoleDTO> getUserRoles(@PathVariable("userId") Long userId);
}
```

#### 服务间调用示例
```java
@Service
public class AuthService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private RoleServiceClient roleServiceClient;
    
    public UserInfoDTO getUserInfo(Long userId) {
        // 调用用户服务获取用户信息
        SingleResponse<UserDTO> userResponse = userServiceClient.getUserById(userId);
        
        // 调用角色服务获取用户角色
        MultiResponse<RoleDTO> roleResponse = roleServiceClient.getUserRoles(userId);
        
        // 构建用户信息
        return UserInfoDTO.builder()
            .user(userResponse.getData())
            .roles(roleResponse.getData())
            .build();
    }
}
```

### 1.3 错误码规范（基于COLA ErrorCode）

#### 错误码格式
- **系统级错误**: `B0001` - 系统执行出错
- **用户端错误**: `A0201` - 用户不存在
- **认证授权错误**: `A1003` - 令牌已过期
- **业务错误**: `C0101` - 用户管理相关错误

#### 常见错误码
| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| `200` | 成功 | 200 |
| `A0101` | 请求参数错误 | 400 |
| `A0102` | 请求参数缺失 | 400 |
| `A1001` | 未授权访问 | 401 |
| `A1002` | 令牌无效 | 401 |
| `A1003` | 令牌已过期 | 401 |
| `A1004` | 权限不足 | 403 |
| `A0201` | 用户不存在 | 404 |
| `B0001` | 系统执行出错 | 500 |
| `C0101` | 用户已存在 | 400 |
| `C0102` | 密码强度不足 | 400 |

## 2. 认证授权接口

### 2.1 账号密码登录
**接口**: `POST /api/auth/login/password`
**描述**: 用户名/密码登录认证
**请求参数**:
```json
{
  "loginType": "USERNAME",
  "username": "admin",
  "password": "123456",
  "captcha": "abcd",
  "captchaId": "captcha_123456",
  "rememberMe": true
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "tokenType": "bearer",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "avatar": "/avatar/default.png"
    }
  },
  "timestamp": 1640995200000
}
```

### 2.2 手机号/邮箱密码登录
**接口**: `POST /api/auth/login/password`
**描述**: 手机号/邮箱+密码登录认证
**请求参数**:
```json
{
  "loginType": "PHONE",
  "phone": "13800138000",
  "password": "123456",
  "captcha": "abcd",
  "captchaId": "captcha_123456",
  "rememberMe": true
}
```
或
```json
{
  "loginType": "EMAIL",
  "email": "admin@example.com",
  "password": "123456",
  "captcha": "abcd",
  "captchaId": "captcha_123456",
  "rememberMe": true
}
```
**响应数据**: 同账号密码登录

### 2.3 手机号验证码登录
**接口**: `POST /api/auth/login/code`
**描述**: 手机号+验证码登录认证
**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456",
  "bizId": "sms_1234567890",
  "rememberMe": true
}
```
**响应数据**: 同账号密码登录

### 2.4 发送登录验证码
**接口**: `POST /api/auth/login/send-code`
**描述**: 发送手机号/邮箱登录验证码
**请求参数**:
```json
{
  "receiver": "13800138000",
  "codeType": "LOGIN",
  "captcha": "abcd",
  "captchaId": "captcha_123456"
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "验证码发送成功",
  "data": {
    "bizId": "sms_1234567890",
    "expireSeconds": 300
  },
  "timestamp": 1640995200000
}
```

### 2.5 第三方登录 - 获取授权地址
**接口**: `GET /api/auth/third/authorize-url`
**描述**: 获取第三方登录授权地址
**查询参数**:
```
thirdType=WECHAT&redirectUri=http://localhost:8080/callback&state=random_state
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "获取成功",
  "data": {
    "authorizeUrl": "https://open.weixin.qq.com/connect/qrconnect?appid=wx123456&redirect_uri=http://localhost:8080/callback&response_type=code&scope=snsapi_login&state=random_state",
    "thirdType": "WECHAT",
    "expireTime": "2024-01-15 11:30:00"
  },
  "timestamp": 1640995200000
}
```

### 2.6 第三方登录 - 回调处理
**接口**: `POST /api/auth/third/callback`
**描述**: 第三方登录回调处理
**请求参数**:
```json
{
  "thirdType": "WECHAT",
  "code": "authorization_code",
  "state": "random_state"
}
```
**响应数据**: 同账号密码登录

### 2.7 第三方账号绑定
**接口**: `POST /api/auth/third/bind`
**描述**: 绑定第三方账号到当前用户
**请求参数**:
```json
{
  "thirdType": "WECHAT",
  "code": "authorization_code",
  "state": "random_state"
}
```
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "绑定成功",
  "data": null,
  "timestamp": 1640995200000
}
```

### 2.8 第三方账号解绑
**接口**: `DELETE /api/auth/third/unbind`
**描述**: 解绑第三方账号
**请求参数**:
```json
{
  "thirdType": "WECHAT"
}
```
**响应数据**: 同绑定接口

### 2.9 获取用户绑定的第三方账号
**接口**: `GET /api/auth/third/bindings`
**描述**: 获取用户绑定的第三方账号列表
**响应数据** (MultiResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "thirdType": "WECHAT",
      "thirdNickname": "微信用户",
      "thirdAvatar": "https://third-party.com/avatar.jpg",
      "bindTime": "2024-01-15 10:30:00",
      "status": 1
    }
  ],
  "total": 1,
  "timestamp": 1640995200000
}
```

### 2.2 用户登出
**接口**: `POST /api/auth/logout`
**描述**: 用户退出登录
**请求头**: `Authorization: Bearer {token}`
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "登出成功",
  "data": null,
  "timestamp": 1640995200000
}
```

### 2.3 刷新令牌
**接口**: `POST /api/auth/refresh`
**描述**: 刷新访问令牌
**请求参数**:
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
**响应数据**: 同登录接口

### 2.4 获取用户信息
**接口**: `GET /api/auth/userinfo`
**描述**: 获取当前登录用户信息
**请求头**: `Authorization: Bearer {token}`
**响应数据**:
```json
{
  "id": 1,
  "username": "admin",
  "realName": "管理员",
  "email": "admin@example.com",
  "phone": "13800138000",
  "avatar": "/avatar/default.png",
  "roles": ["super_admin"],
  "permissions": ["system:user:list", "system:user:add"]
}
```

## 3. 用户管理接口

### 3.1 获取用户列表
**接口**: `GET /api/users`
**描述**: 分页查询用户列表
**查询参数**:
```
page=1&size=10&username=admin&status=1&startTime=2024-01-01&endTime=2024-12-31
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "email": "admin@example.com",
      "phone": "13800138000",
      "status": 1,
      "lastLoginTime": "2024-01-15 10:30:00",
      "createdTime": "2024-01-01 00:00:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "pages": 10
}
```

### 3.2 新增用户
**接口**: `POST /api/users`
**描述**: 创建新用户
**请求参数**:
```json
{
  "username": "testuser",
  "password": "123456",
  "realName": "测试用户",
  "email": "test@example.com",
  "phone": "13800138001",
  "roleIds": [2, 3]
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "用户创建成功",
  "data": {
    "id": 100,
    "username": "testuser"
  },
  "timestamp": 1640995200000
}
```

### 3.3 编辑用户
**接口**: `PUT /api/users/{id}`
**描述**: 更新用户信息
**路径参数**: `id` - 用户ID
**请求参数**:
```json
{
  "realName": "更新后的姓名",
  "email": "updated@example.com",
  "phone": "13800138002",
  "roleIds": [2, 4]
}
```

### 3.4 删除用户
**接口**: `DELETE /api/users/{id}`
**描述**: 删除用户
**路径参数**: `id` - 用户ID

### 3.5 重置密码
**接口**: `PUT /api/users/{id}/password`
**描述**: 重置用户密码
**路径参数**: `id` - 用户ID
**请求参数**:
```json
{
  "newPassword": "newpassword123"
}
```

## 4. 角色管理接口

### 4.1 获取角色列表
**接口**: `GET /api/roles`
**描述**: 分页查询角色列表
**查询参数**:
```
page=1&size=10&roleName=管理员&status=1
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "roleCode": "super_admin",
      "roleName": "超级管理员",
      "roleType": 1,
      "dataScope": 1,
      "status": 1,
      "createdTime": "2024-01-01 00:00:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 50,
  "pages": 5
}
```

### 4.2 新增角色
**接口**: `POST /api/roles`
**描述**: 创建新角色
**请求参数**:
```json
{
  "roleCode": "test_role",
  "roleName": "测试角色",
  "roleType": 2,
  "dataScope": 2,
  "menuIds": [1, 2, 3],
  "description": "测试角色描述"
}
```

### 4.3 获取角色权限
**接口**: `GET /api/roles/{id}/permissions`
**描述**: 获取角色拥有的权限
**路径参数**: `id` - 角色ID
**响应数据**:
```json
{
  "menuIds": [1, 2, 3, 4],
  "permissionList": ["system:user:list", "system:user:add"]
}
```

### 4.4 分配角色权限
**接口**: `PUT /api/roles/{id}/permissions`
**描述**: 为角色分配权限
**路径参数**: `id` - 角色ID
**请求参数**:
```json
{
  "menuIds": [1, 2, 3, 4, 5]
}
```

## 5. 菜单管理接口

### 5.1 获取菜单树
**接口**: `GET /api/menus/tree`
**描述**: 获取菜单树形结构
**响应数据** (MultiResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "menuType": 1,
      "path": "/system",
      "icon": "el-icon-setting",
      "sortOrder": 1,
      "children": [
        {
          "id": 2,
          "menuName": "用户管理",
          "menuType": 2,
          "path": "/system/user",
          "perms": "system:user:list",
          "sortOrder": 1
        }
      ]
    }
  ],
  "total": 1,
  "timestamp": 1640995200000
}
```

### 5.2 新增菜单
**接口**: `POST /api/menus`
**描述**: 创建新菜单
**请求参数**:
```json
{
  "parentId": 1,
  "menuName": "新增菜单",
  "menuType": 2,
  "path": "/system/new",
  "perms": "system:new:list",
  "icon": "el-icon-plus",
  "sortOrder": 10
}
```

## 6. 客户端管理接口

### 6.1 获取客户端列表
**接口**: `GET /api/clients`
**描述**: 分页查询OAuth2客户端列表
**查询参数**:
```
page=1&size=10&clientName=测试应用&status=1
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "clientId": "test_client",
      "clientName": "测试客户端",
      "clientType": 1,
      "authorizedGrantTypes": "authorization_code,password",
      "redirectUris": "http://localhost:8080/callback",
      "accessTokenValidity": 7200,
      "status": 1,
      "createdTime": "2024-01-01 00:00:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 20,
  "pages": 2
}
```

### 6.2 新增客户端
**接口**: `POST /api/clients`
**描述**: 注册新的OAuth2客户端
**请求参数**:
```json
{
  "clientId": "new_client",
  "clientName": "新客户端",
  "clientType": 1,
  "authorizedGrantTypes": "authorization_code,refresh_token",
  "redirectUris": "http://localhost:3000/callback",
  "scope": "read,write",
  "accessTokenValidity": 7200,
  "refreshTokenValidity": 604800
}
```

### 6.3 重置客户端密钥
**接口**: `PUT /api/clients/{id}/secret`
**描述**: 重置客户端密钥
**路径参数**: `id` - 客户端ID
**响应数据**:
```json
{
  "clientSecret": "new_secret_key"
}
```

## 7. 日志管理接口

### 7.1 查询操作日志
**接口**: `GET /api/logs/operation`
**描述**: 分页查询操作日志
**查询参数**:
```
page=1&size=20&username=admin&operationType=新增&startTime=2024-01-01&endTime=2024-01-31
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "operationType": "新增",
      "operationModule": "用户管理",
      "operationDesc": "新增用户test",
      "requestMethod": "POST",
      "requestUrl": "/api/users",
      "ipAddress": "192.168.1.1",
      "executeTime": 150,
      "status": 1,
      "createdTime": "2024-01-15 10:30:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 20,
  "total": 1000,
  "pages": 50
}
```

### 7.2 查询登录日志
**接口**: `GET /api/logs/login`
**描述**: 分页查询登录日志
**查询参数**:
```
page=1&size=20&username=admin&status=1&startTime=2024-01-01&endTime=2024-01-31
```
**响应数据**:
```json
{
  "total": 500,
  "list": [
    {
      "id": 1,
      "username": "admin",
      "loginType": "password",
      "loginIp": "192.168.1.1",
      "loginLocation": "北京市",
      "status": 1,
      "loginTime": "2024-01-15 10:30:00"
    }
  ]
}
```

## 8. 系统监控接口

### 8.1 获取系统信息
**接口**: `GET /api/monitor/system`
**描述**: 获取系统运行信息
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": {
    "os": {
      "name": "Linux",
      "version": "5.4.0",
      "arch": "x86_64"
    },
    "jvm": {
      "version": "1.8.0_292",
      "memory": {
        "total": "1024M",
        "used": "512M",
        "free": "512M"
      }
    },
    "cpu": {
      "cores": 8,
      "usage": 45.5
    },
    "disk": {
      "total": "100G",
      "used": "50G",
      "free": "50G"
    }
  },
  "timestamp": 1640995200000
}
```

### 8.2 获取服务状态
**接口**: `GET /api/monitor/services`
**描述**: 获取微服务健康状态
**响应数据** (MultiResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "serviceName": "auth-server",
      "status": "UP",
      "instanceCount": 2,
      "lastCheckTime": "2024-01-15 10:30:00"
    },
    {
      "serviceName": "user-service",
      "status": "UP", 
      "instanceCount": 3,
      "lastCheckTime": "2024-01-15 10:29:00"
    }
  ],
  "total": 2,
  "timestamp": 1640995200000
}
```

## 9. OAuth2.1授权接口

### 9.1 授权码模式
**接口**: `GET /oauth/authorize`
**描述**: OAuth2.1授权端点
**查询参数**:
```
response_type=code&client_id=test_client&redirect_uri=http://localhost:8080/callback&scope=read&state=random_state
```

**接口**: `POST /oauth/token`
**描述**: 获取访问令牌
**请求参数**:
```json
{
  "grant_type": "authorization_code",
  "code": "authorization_code",
  "redirect_uri": "http://localhost:8080/callback",
  "client_id": "test_client",
  "client_secret": "client_secret"
}
```

### 9.2 密码模式
**接口**: `POST /oauth/token`
**描述**: 密码模式获取令牌
**请求参数**:
```json
{
  "grant_type": "password",
  "username": "admin",
  "password": "123456",
  "client_id": "test_client",
  "client_secret": "client_secret"
}
```

## 10. 文件存储接口

### 10.1 小文件上传
**接口**: `POST /api/files/upload`
**描述**: 小文件上传接口（建议小于10MB），支持多种存储提供商
**请求头**: `Content-Type: multipart/form-data`
**请求参数**: 
- `file` - 文件（必填）
- `storageType` - 存储类型（可选，默认：minio，可选值：oss/minio/s3/google）
- `bucketName` - 存储桶名称（可选，默认：auth-center）
- `category` - 文件分类（可选，如：avatar/document/image等）
- `isPublic` - 是否公开访问（可选，默认：false）

**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "文件上传成功",
  "data": {
    "fileId": "file_1234567890",
    "fileName": "avatar.png",
    "fileUrl": "https://minio.example.com/auth-center/avatar_1234567890.png",
    "fileSize": 102400,
    "fileType": "image/png",
    "storageType": "minio",
    "bucketName": "auth-center",
    "category": "avatar",
    "isPublic": false,
    "uploadTime": "2024-01-15 10:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.2 大文件分片上传 - 初始化上传
**接口**: `POST /api/files/large/init`
**描述**: 初始化大文件上传，生成上传ID和分片信息
**请求参数**:
```json
{
  "fileName": "large_video.mp4",
  "fileSize": 104857600,
  "fileType": "video/mp4",
  "chunkSize": 5242880,
  "storageType": "minio",
  "bucketName": "auth-center",
  "category": "video",
  "isPublic": false,
  "md5": "d41d8cd98f00b204e9800998ecf8427e"
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "上传初始化成功",
  "data": {
    "uploadId": "upload_1234567890",
    "chunkSize": 5242880,
    "totalChunks": 20,
    "uploadUrls": [
      "https://minio.example.com/auth-center/upload_1234567890/chunk_1",
      "https://minio.example.com/auth-center/upload_1234567890/chunk_2",
      "https://minio.example.com/auth-center/upload_1234567890/chunk_3"
    ],
    "expireTime": "2024-01-15 11:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.3 大文件分片上传 - 上传分片
**接口**: `PUT /api/files/large/upload/{uploadId}/{chunkIndex}`
**描述**: 上传单个文件分片
**路径参数**:
- `uploadId` - 上传ID
- `chunkIndex` - 分片索引（从1开始）
**请求头**: `Content-Type: application/octet-stream`
**请求体**: 文件分片二进制数据
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "分片上传成功",
  "data": {
    "uploadId": "upload_1234567890",
    "chunkIndex": 1,
    "etag": "d41d8cd98f00b204e9800998ecf8427e",
    "uploadedChunks": [1],
    "progress": 5.0
  },
  "timestamp": 1640995200000
}
```

### 10.4 大文件分片上传 - 完成上传
**接口**: `POST /api/files/large/complete/{uploadId}`
**描述**: 完成大文件上传，合并所有分片
**路径参数**: `uploadId` - 上传ID
**请求参数**:
```json
{
  "etags": [
    {"chunkIndex": 1, "etag": "etag1"},
    {"chunkIndex": 2, "etag": "etag2"},
    {"chunkIndex": 3, "etag": "etag3"}
  ]
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "文件上传完成",
  "data": {
    "fileId": "file_1234567890",
    "fileName": "large_video.mp4",
    "fileUrl": "https://minio.example.com/auth-center/large_video_1234567890.mp4",
    "fileSize": 104857600,
    "fileType": "video/mp4",
    "storageType": "minio",
    "bucketName": "auth-center",
    "category": "video",
    "isPublic": false,
    "uploadTime": "2024-01-15 10:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.5 大文件分片上传 - 查询上传进度
**接口**: `GET /api/files/large/progress/{uploadId}`
**描述**: 查询大文件上传进度
**路径参数**: `uploadId` - 上传ID
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": {
    "uploadId": "upload_1234567890",
    "fileName": "large_video.mp4",
    "fileSize": 104857600,
    "uploadedSize": 52428800,
    "progress": 50.0,
    "uploadedChunks": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    "totalChunks": 20,
    "status": "uploading",
    "startTime": "2024-01-15 10:30:00",
    "lastUpdateTime": "2024-01-15 10:35:00"
  },
  "timestamp": 1640995200000
}
```

### 10.6 大文件分片上传 - 取消上传
**接口**: `DELETE /api/files/large/cancel/{uploadId}`
**描述**: 取消大文件上传，清理已上传的分片
**路径参数**: `uploadId` - 上传ID
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "上传已取消",
  "data": null,
  "timestamp": 1640995200000
}
```

### 10.7 大文件下载 - 获取下载地址
**接口**: `POST /api/files/large/download-url`
**描述**: 获取大文件下载地址，支持分片下载
**请求参数**:
```json
{
  "fileId": "file_1234567890",
  "downloadType": "direct",
  "rangeSupport": true
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "下载地址获取成功",
  "data": {
    "fileId": "file_1234567890",
    "fileName": "large_video.mp4",
    "fileSize": 104857600,
    "downloadUrl": "https://minio.example.com/auth-center/large_video_1234567890.mp4",
    "rangeSupport": true,
    "expireTime": "2024-01-15 11:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.8 大文件下载 - 分片下载信息
**接口**: `GET /api/files/large/download-info/{fileId}`
**描述**: 获取大文件分片下载信息，支持断点续传
**路径参数**: `fileId` - 文件ID
**查询参数**: `chunkSize=5242880`（可选，默认5MB）
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "下载信息获取成功",
  "data": {
    "fileId": "file_1234567890",
    "fileName": "large_video.mp4",
    "fileSize": 104857600,
    "chunkSize": 5242880,
    "totalChunks": 20,
    "downloadUrls": [
      {
        "chunkIndex": 1,
        "startByte": 0,
        "endByte": 5242879,
        "url": "https://minio.example.com/auth-center/large_video_1234567890.mp4?range=0-5242879"
      },
      {
        "chunkIndex": 2,
        "startByte": 5242880,
        "endByte": 10485759,
        "url": "https://minio.example.com/auth-center/large_video_1234567890.mp4?range=5242880-10485759"
      }
    ],
    "md5": "d41d8cd98f00b204e9800998ecf8427e",
    "expireTime": "2024-01-15 11:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.9 文件下载
**接口**: `GET /api/files/download/{fileId}`
**描述**: 文件下载接口
**路径参数**: `fileId` - 文件ID
**查询参数**:
- `download` - 是否直接下载（可选，默认：false）
- `preview` - 是否预览（可选，默认：false）

**响应**: 文件流或重定向到文件URL

## 11. 通知服务接口

### 11.1 邮件发送接口

#### 11.1.1 发送邮件
**接口**: `POST /api/notification/email/send`
**描述**: 发送邮件通知
**请求参数**:
```json
{
  "to": ["user@example.com"],
  "cc": ["admin@example.com"],
  "subject": "欢迎加入认证中心",
  "templateCode": "USER_REGISTER_WELCOME",
  "templateVariables": {
    "username": "testuser",
    "registerTime": "2024-01-15 10:30:00"
  }
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "邮件发送成功",
  "data": {
    "notificationId": "notify_1234567890",
    "sendStatus": "SUCCESS",
    "sendTime": "2024-01-15 10:30:00"
  },
  "timestamp": 1640995200000
}
```

#### 11.1.2 异步发送邮件
**接口**: `POST /api/notification/email/send-async`
**描述**: 异步发送邮件（通过消息队列）
**请求参数**: 同同步发送接口
**响应数据**: 立即返回接收成功，实际发送通过消息队列异步处理

### 11.2 短信发送接口

#### 11.2.1 发送短信
**接口**: `POST /api/notification/sms/send`
**描述**: 发送短信通知
**请求参数**:
```json
{
  "phoneNumber": "13800138000",
  "templateCode": "SMS_VERIFICATION_CODE",
  "templateParam": {
    "code": "123456",
    "minutes": 5
  },
  "signName": "认证中心"
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "短信发送成功",
  "data": {
    "notificationId": "notify_1234567890",
    "sendStatus": "SUCCESS",
    "externalId": "sms_7890123456",
    "sendTime": "2024-01-15 10:30:00"
  },
  "timestamp": 1640995200000
}
```

#### 11.2.2 发送验证码短信
**接口**: `POST /api/notification/sms/send-verification`
**描述**: 发送验证码短信（带频率限制）
**请求参数**:
```json
{
  "phoneNumber": "13800138000",
  "bizType": "REGISTER",
  "signName": "认证中心"
}
```
**响应数据**: 同普通短信发送接口

### 11.3 站内通知接口

#### 11.3.1 发送个人通知
**接口**: `POST /api/notification/push/send-to-user`
**描述**: 发送站内个人通知
**请求参数**:
```json
{
  "userId": "123456",
  "title": "权限变更通知",
  "content": "您的角色权限已更新，请重新登录生效。",
  "type": "INFO",
  "extra": {
    "changeType": "ROLE_UPDATE",
    "effectiveTime": "2024-01-15 10:30:00"
  }
}
```
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "通知发送成功",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 11.3.2 发送广播通知
**接口**: `POST /api/notification/push/broadcast`
**描述**: 发送广播通知给所有在线用户
**请求参数**:
```json
{
  "title": "系统维护通知",
  "content": "系统将于今晚22:00-24:00进行维护，请提前保存数据。",
  "type": "WARNING"
}
```

### 11.4 通知模板管理接口

#### 11.4.1 获取通知模板列表
**接口**: `GET /api/notification/templates`
**描述**: 分页查询通知模板列表
**查询参数**:
```
page=1&size=10&templateType=EMAIL&status=1
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "templateCode": "USER_REGISTER_WELCOME",
      "templateName": "用户注册欢迎邮件",
      "templateType": "EMAIL",
      "templateContent": "<html>...</html>",
      "templateVariables": [
        {"name": "username", "description": "用户名"},
        {"name": "registerTime", "description": "注册时间"}
      ],
      "status": 1,
      "createdTime": "2024-01-01 00:00:00"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 50,
  "pages": 5
}
```

#### 11.4.2 新增通知模板
**接口**: `POST /api/notification/templates`
**描述**: 创建新的通知模板
**请求参数**:
```json
{
  "templateCode": "PASSWORD_RESET_EMAIL",
  "templateName": "密码重置邮件",
  "templateType": "EMAIL",
  "templateContent": "您的密码重置链接为：${resetUrl}",
  "templateVariables": [
    {"name": "resetUrl", "description": "重置链接"}
  ],
  "channelType": "DEFAULT"
}
```

#### 11.4.3 预览模板渲染
**接口**: `POST /api/notification/templates/{templateCode}/preview`
**描述**: 预览模板渲染效果
**路径参数**: `templateCode` - 模板编码
**请求参数**:
```json
{
  "templateVariables": {
    "username": "testuser",
    "resetUrl": "https://auth-center.com/reset-password?token=abc123"
  }
}
```
**响应数据**: 渲染后的模板内容

### 11.5 通知记录查询接口

#### 11.5.1 查询通知发送记录
**接口**: `GET /api/notification/records`
**描述**: 分页查询通知发送记录
**查询参数**:
```
page=1&size=20&notificationType=EMAIL&sendStatus=SUCCESS&startTime=2024-01-01&endTime=2024-01-31
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "id": 1,
      "notificationId": "notify_1234567890",
      "templateCode": "USER_REGISTER_WELCOME",
      "notificationType": "EMAIL",
      "receiver": "user@example.com",
      "title": "欢迎加入认证中心",
      "sendStatus": "SUCCESS",
      "sendTime": "2024-01-15 10:30:00",
      "completeTime": "2024-01-15 10:30:05"
    }
  ],
  "pageNum": 1,
  "pageSize": 20,
  "total": 1000,
  "pages": 50
}
```

#### 11.5.2 手动重试发送
**接口**: `POST /api/notification/records/{recordId}/retry`
**描述**: 手动重试发送失败的通知
**路径参数**: `recordId` - 记录ID
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "重试发送成功",
  "data": null,
  "timestamp": 1640995200000
}
```

### 11.6 通知统计接口

#### 11.6.1 获取通知发送统计
**接口**: `GET /api/notification/statistics`
**描述**: 获取通知发送统计信息
**查询参数**:
```
startTime=2024-01-01&endTime=2024-01-31&groupBy=DAY
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": {
    "totalCount": 10000,
    "successCount": 9800,
    "failureCount": 200,
    "successRate": 98.0,
    "dailyStatistics": [
      {
        "date": "2024-01-15",
        "emailCount": 500,
        "smsCount": 300,
        "pushCount": 200,
        "successRate": 98.5
      }
    ],
    "channelStatistics": [
      {
        "channelType": "EMAIL",
        "count": 6000,
        "successRate": 99.0
      },
      {
        "channelType": "SMS", 
        "count": 3500,
        "successRate": 96.5
      }
    ]
  },
  "timestamp": 1640995200000
}
```

### 11.7 通知渠道配置接口

#### 11.7.1 获取通知渠道列表
**接口**: `GET /api/notification/channels`
**描述**: 获取可用的通知渠道配置
**响应数据** (MultiResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "channelCode": "ALIYUN_SMS",
      "channelName": "阿里云短信",
      "channelType": "SMS",
      "provider": "ALIYUN",
      "priority": 1,
      "dailyLimit": 1000,
      "status": 1
    },
    {
      "channelCode": "TENCENT_SMS",
      "channelName": "腾讯云短信", 
      "channelType": "SMS",
      "provider": "TENCENT",
      "priority": 2,
      "dailyLimit": 800,
      "status": 1
    }
  ],
  "total": 5,
  "timestamp": 1640995200000
}
```

## 12. 异常处理规范

### 12.1 错误响应格式
所有异常都会返回统一的错误响应格式：
```json
{
  "success": false,
  "errCode": "A0201",
  "errMessage": "用户不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 12.2 常见错误场景

#### 参数校验错误
```json
{
  "success": false,
  "errCode": "A0101",
  "errMessage": "请求参数错误: username不能为空",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 认证失败
```json
{
  "success": false,
  "errCode": "A1001",
  "errMessage": "未授权访问",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 权限不足
```json
{
  "success": false,
  "errCode": "A1004",
  "errMessage": "权限不足",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 业务异常
```json
{
  "success": false,
  "errCode": "C0101",
  "errMessage": "用户已存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 12.3 异常处理机制

#### 全局异常处理器
系统通过`GlobalExceptionHandler`统一处理所有异常：
- **业务异常**: 返回具体的业务错误码和消息
- **参数校验异常**: 返回参数校验错误信息
- **认证授权异常**: 返回401或403状态码
- **系统异常**: 返回500状态码，生产环境隐藏详细错误信息

#### 异常抛出示例
```java
// 业务异常
throw BusinessException.of(ErrorCode.USER_NOT_EXIST);

// 参数校验
@Valid @RequestBody UserCreateDTO userDTO

// 权限检查
if (!hasPermission("system:user:add")) {
    throw BusinessException.of(ErrorCode.PERMISSION_DENIED);
}
```

## 13. API使用最佳实践

### 13.1 请求头规范
```http
Authorization: Bearer {access_token}
Content-Type: application/json
X-Tenant-Id: {tenant_id}
X-Request-Id: {request_id}
```

### 13.2 响应处理建议
1. **检查success字段**: 始终检查响应中的success字段
2. **处理错误码**: 根据code字段进行相应的错误处理
3. **重试机制**: 对于网络错误或服务不可用，实现合理的重试机制
4. **超时设置**: 设置合理的请求超时时间
5. **日志记录**: 记录请求和响应日志，便于问题排查

### 13.3 客户端示例
```javascript
// 统一请求处理
async function apiRequest(url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${getToken()}`,
        'Content-Type': 'application/json',
        'X-Tenant-Id': getTenantId(),
        ...options.headers
      },
      ...options
    });
    
    const result = await response.json();
    
    if (!result.success) {
      // 处理业务错误
      handleBusinessError(result.code, result.message);
      return null;
    }
    
    return result.data;
  } catch (error) {
    // 处理网络错误
    handleNetworkError(error);
    return null;
  }
}
```

这套API接口规范完全遵循COLA框架的设计原则，确保了前后端交互的一致性和可维护性。

### 10.2 文件下载
**接口**: `GET /api/files/download/{fileId}`
**描述**: 文件下载接口
**路径参数**: `fileId` - 文件ID
**查询参数**:
- `download` - 是否直接下载（可选，默认：false）
- `preview` - 是否预览（可选，默认：false）

**响应**: 文件流或重定向到文件URL

### 10.3 获取文件信息
**接口**: `GET /api/files/{fileId}`
**描述**: 获取文件详细信息
**路径参数**: `fileId` - 文件ID
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": {
    "fileId": "file_1234567890",
    "fileName": "avatar.png",
    "fileUrl": "https://minio.example.com/auth-center/avatar_1234567890.png",
    "fileSize": 102400,
    "fileType": "image/png",
    "storageType": "minio",
    "bucketName": "auth-center",
    "category": "avatar",
    "isPublic": false,
    "uploadTime": "2024-01-15 10:30:00",
    "uploader": "admin",
    "metadata": {
      "width": 200,
      "height": 200,
      "format": "PNG"
    }
  },
  "timestamp": 1640995200000
}
```

### 10.4 文件列表查询
**接口**: `GET /api/files`
**描述**: 分页查询文件列表
**查询参数**:
```
page=1&size=20&category=avatar&storageType=minio&startTime=2024-01-01&endTime=2024-01-31
```
**响应数据** (PageResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "fileId": "file_1234567890",
      "fileName": "avatar.png",
      "fileSize": 102400,
      "fileType": "image/png",
      "storageType": "minio",
      "category": "avatar",
      "uploadTime": "2024-01-15 10:30:00",
      "uploader": "admin"
    }
  ],
  "pageNum": 1,
  "pageSize": 20,
  "total": 100,
  "pages": 5
}
```

### 10.5 删除文件
**接口**: `DELETE /api/files/{fileId}`
**描述**: 删除文件
**路径参数**: `fileId` - 文件ID
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "文件删除成功",
  "data": null,
  "timestamp": 1640995200000
}
```

### 10.6 生成文件预览URL
**接口**: `POST /api/files/preview-url`
**描述**: 生成文件预览URL（带时效性）
**请求参数**:
```json
{
  "fileId": "file_1234567890",
  "expireMinutes": 60
}
```
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "预览URL生成成功",
  "data": {
    "previewUrl": "https://minio.example.com/auth-center/avatar_1234567890.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expireTime": "2024-01-15 11:30:00"
  },
  "timestamp": 1640995200000
}
```

### 10.7 存储桶管理
**接口**: `GET /api/files/buckets`
**描述**: 获取存储桶列表
**响应数据** (MultiResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "查询成功",
  "data": [
    {
      "bucketName": "auth-center",
      "storageType": "minio",
      "totalFiles": 100,
      "totalSize": "10.5MB",
      "createTime": "2024-01-01 00:00:00"
    }
  ],
  "total": 1,
  "timestamp": 1640995200000
}
```

## 11. 公共接口

### 11.1 获取验证码
**接口**: `GET /api/captcha`
**描述**: 获取图形验证码
**响应数据** (SingleResponse格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "获取验证码成功",
  "data": {
    "captchaId": "captcha_123456",
    "captchaImage": "data:image/png;base64,..."
  },
  "timestamp": 1640995200000
}
```

### 10.3 验证验证码
**接口**: `POST /api/captcha/verify`
**描述**: 验证图形验证码
**请求参数**:
```json
{
  "captchaId": "captcha_123456",
  "captchaCode": "abcd"
}
```
**响应数据** (Response格式):
```json
{
  "success": true,
  "errCode": "200",
  "errMessage": "验证码验证成功",
  "data": null,
  "timestamp": 1640995200000
}
```

## 11. 异常处理规范

### 11.1 错误响应格式
所有异常都会返回统一的错误响应格式：
```json
{
  "success": false,
  "errCode": "A0201",
  "errMessage": "用户不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 11.2 常见错误场景

#### 参数校验错误
```json
{
  "success": false,
  "errCode": "A0101",
  "errMessage": "请求参数错误: username不能为空",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 认证失败
```json
{
  "success": false,
  "errCode": "A1001",
  "errMessage": "未授权访问",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 权限不足
```json
{
  "success": false,
  "errCode": "A1004",
  "errMessage": "权限不足",
  "data": null,
  "timestamp": 1640995200000
}
```

#### 业务异常
```json
{
  "success": false,
  "errCode": "C0101",
  "errMessage": "用户已存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 11.3 异常处理机制

#### 全局异常处理器
系统通过`GlobalExceptionHandler`统一处理所有异常：
- **业务异常**: 返回具体的业务错误码和消息
- **参数校验异常**: 返回参数校验错误信息
- **认证授权异常**: 返回401或403状态码
- **系统异常**: 返回500状态码，生产环境隐藏详细错误信息

#### 异常抛出示例
```java
// 业务异常
throw BusinessException.of(ErrorCode.USER_NOT_EXIST);

// 参数校验
@Valid @RequestBody UserCreateDTO userDTO

// 权限检查
if (!hasPermission("system:user:add")) {
    throw BusinessException.of(ErrorCode.PERMISSION_DENIED);
}
```

## 12. API使用最佳实践

### 12.1 请求头规范
```http
Authorization: Bearer {access_token}
Content-Type: application/json
X-Tenant-Id: {tenant_id}
X-Request-Id: {request_id}
```

### 12.2 响应处理建议
1. **检查success字段**: 始终检查响应中的success字段
2. **处理错误码**: 根据code字段进行相应的错误处理
3. **重试机制**: 对于网络错误或服务不可用，实现合理的重试机制
4. **超时设置**: 设置合理的请求超时时间
5. **日志记录**: 记录请求和响应日志，便于问题排查

### 12.3 客户端示例
```javascript
// 统一请求处理
async function apiRequest(url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${getToken()}`,
        'Content-Type': 'application/json',
        'X-Tenant-Id': getTenantId(),
        ...options.headers
      },
      ...options
    });
    
    const result = await response.json();
    
    if (!result.success) {
      // 处理业务错误
      handleBusinessError(result.code, result.message);
      return null;
    }
    
    return result.data;
  } catch (error) {
    // 处理网络错误
    handleNetworkError(error);
    return null;
  }
}
```

这套API接口规范完全遵循COLA框架的设计原则，确保了前后端交互的一致性和可维护性。