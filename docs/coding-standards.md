# 代码规范

## 📋 目录

- [Java代码规范](#java代码规范)
- [数据库规范](#数据库规范)
- [Git提交规范](#git提交规范)
- [API设计规范](#api设计规范)
- [文档规范](#文档规范)
- [代码审查规范](#代码审查规范)

## ☕ Java代码规范

### 1. 命名规范

#### 1.1 包命名
```java
// 包名全部小写，使用点号分隔
com.auth.center.user.service
com.auth.center.auth.interfaces.controller
```

#### 1.2 类命名
```java
// 类名使用大驼峰命名法（PascalCase）
public class UserService {
}

public class UserApplicationService {
}

// 接口命名
public interface UserRepository {
}

// 实现类命名
public class UserRepositoryImpl implements UserRepository {
}
```

#### 1.3 方法命名
```java
// 方法名使用小驼峰命名法（camelCase）
public UserDTO getUserById(Long id) {
}

public void createUser(UserCreateRequest request) {
}

// 布尔类型方法以is、has、can开头
public boolean isValid() {
}

public boolean hasPermission(String permission) {
}
```

#### 1.4 变量命名
```java
// 变量名使用小驼峰命名法
private String userName;
private List<UserDTO> userList;
private static final String DEFAULT_PASSWORD = "123456";

// 常量全大写，下划线分隔
public static final int MAX_RETRY_COUNT = 3;
```

### 2. 代码格式

#### 2.1 缩进和空格
```java
// 使用4个空格缩进，不使用Tab
public class UserService {
    
    private UserRepository userRepository;
    
    public UserDTO getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return userRepository.findById(id);
    }
}
```

#### 2.2 大括号风格
```java
// 使用K&R风格，左大括号不换行
if (condition) {
    // do something
} else {
    // do something else
}

// 即使只有一行也要使用大括号
if (condition) {
    return true;
}
```

#### 2.3 行长度限制
```java
// 每行不超过120个字符
String longString = "This is a long string that should be broken " +
                   "into multiple lines for readability";

// 方法参数过多时换行
public void createUser(String username, String password, String email,
                      String phone, String realName) {
    // method implementation
}
```

### 3. 注释规范

#### 3.1 类注释
```java
/**
 * 用户服务类
 * 
 * 提供用户相关的业务逻辑处理，包括用户创建、查询、更新、删除等功能
 * 
 * @author 开发者姓名
 * @since 1.0.0
 * @see UserDTO
 * @see UserRepository
 */
@Service
public class UserService {
    
}
```

#### 3.2 方法注释
```java
/**
 * 根据用户ID获取用户信息
 * 
 * @param userId 用户ID，不能为空
 * @return 用户信息，如果用户不存在返回null
 * @throws IllegalArgumentException 当用户ID为空时抛出
 * @throws BusinessException 当用户不存在时抛出
 */
public UserDTO getUserById(Long userId) {
    if (userId == null) {
        throw new IllegalArgumentException("用户ID不能为空");
    }
    
    User user = userRepository.findById(userId);
    if (user == null) {
        throw new BusinessException("用户不存在");
    }
    
    return UserConvertor.toDTO(user);
}
```

#### 3.3 行内注释
```java
public void processUser(User user) {
    // 验证用户信息
    if (user == null || user.getId() == null) {
        return;
    }
    
    // 更新用户状态
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
}
```

### 4. 异常处理

#### 4.1 异常定义
```java
// 业务异常
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

// 错误码枚举
public enum ErrorCode {
    USER_NOT_FOUND("A0201", "用户不存在"),
    INVALID_PASSWORD("A0202", "密码错误");
    
    private final String code;
    private final String message;
}
```

#### 4.2 异常处理
```java
@Service
public class UserService {
    
    public UserDTO getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId);
            return UserConvertor.toDTO(user);
        } catch (DataAccessException e) {
            log.error("数据库访问异常", e);
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "系统异常，请稍后重试");
        }
    }
}
```

### 5. 日志规范

#### 5.1 日志级别
```java
@Service
@Slf4j
public class UserService {
    
    public UserDTO getUserById(Long userId) {
        log.debug("开始查询用户，userId: {}", userId);
        
        try {
            User user = userRepository.findById(userId);
            log.info("查询用户成功，userId: {}", userId);
            return UserConvertor.toDTO(user);
        } catch (Exception e) {
            log.error("查询用户失败，userId: {}", userId, e);
            throw e;
        }
    }
}
```

#### 5.2 日志格式
```yaml
# logback-spring.xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
```

## 🗄️ 数据库规范

### 1. 表设计规范

#### 1.1 表命名
```sql
-- 表名使用小写字母和下划线
CREATE TABLE sys_user (
    -- 表结构
);

CREATE TABLE sys_role (
    -- 表结构
);
```

#### 1.2 字段命名
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_name VARCHAR(64) NOT NULL COMMENT '用户名',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';
```

#### 1.3 索引命名
```sql
-- 普通索引：idx_表名_字段名
CREATE INDEX idx_user_username ON sys_user(username);

-- 唯一索引：uk_表名_字段名
CREATE UNIQUE INDEX uk_user_username ON sys_user(username);

-- 组合索引：idx_表名_字段1_字段2
CREATE INDEX idx_user_status_type ON sys_user(status, user_type);
```

### 2. SQL编写规范

#### 2.1 查询语句
```sql
-- 关键字大写，字段名小写
SELECT id, user_name, email, phone 
FROM sys_user 
WHERE status = 1 
  AND created_time >= '2024-01-01'
ORDER BY created_time DESC 
LIMIT 10;
```

#### 2.2 插入语句
```sql
-- 指定字段列表
INSERT INTO sys_user (user_name, email, phone, status) 
VALUES ('testuser', 'test@example.com', '13800138000', 1);
```

#### 2.3 更新语句
```sql
-- 使用WHERE条件避免全表更新
UPDATE sys_user 
SET email = 'new@example.com', updated_time = NOW() 
WHERE id = 1;
```

## 📝 Git提交规范

### 1. 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 1.1 Type类型
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动
- `ci`: CI/CD相关
- `revert`: 回滚提交

#### 1.2 Scope范围
- `auth`: 认证模块
- `user`: 用户模块
- `permission`: 权限模块
- `gateway`: 网关模块
- `common`: 公共模块

#### 1.3 Subject
- 简短描述，不超过50个字符
- 使用祈使语气
- 首字母小写，结尾不加句号

#### 1.4 Body
- 详细描述本次提交的内容
- 说明修改的原因和实现方式
- 可以分多行，每行不超过72个字符

#### 1.5 Footer
- 关联的Issue：`Closes #123`
- 破坏性变更：`BREAKING CHANGE:`
- 影响范围：`AFFECTS:`

### 2. 提交示例

#### 2.1 功能提交
```
feat(auth): 添加第三方登录功能

- 实现微信登录
- 实现QQ登录
- 添加第三方账号绑定功能
- 更新登录策略工厂

Closes #123
```

#### 2.2 修复提交
```
fix(user): 修复用户创建时密码加密问题

用户创建时密码没有进行加密处理，导致登录失败。
现在使用BCrypt进行密码加密。

Fixes #456
```

#### 2.3 重构提交
```
refactor(common): 重构响应对象结构

- 统一响应格式
- 添加错误码枚举
- 简化异常处理逻辑
```

## 🌐 API设计规范

### 1. RESTful API规范

#### 1.1 URL命名
```
# 使用名词复数形式
GET    /api/users           # 获取用户列表
POST   /api/users           # 创建用户
GET    /api/users/{id}      # 获取用户详情
PUT    /api/users/{id}      # 更新用户
DELETE /api/users/{id}      # 删除用户

# 嵌套资源
GET    /api/users/{id}/roles     # 获取用户角色
POST   /api/users/{id}/roles     # 分配角色
```

#### 1.2 HTTP方法使用
```
GET    - 获取资源
POST   - 创建资源
PUT    - 完整更新资源
PATCH  - 部分更新资源
DELETE - 删除资源
```

#### 1.3 状态码规范
```
200 OK              - 请求成功
201 Created         - 资源创建成功
204 No Content      - 删除成功
400 Bad Request     - 请求参数错误
401 Unauthorized    - 未授权
403 Forbidden       - 权限不足
404 Not Found       - 资源不存在
500 Internal Server Error - 服务器错误
```

### 2. 请求响应规范

#### 2.1 请求参数
```json
// 查询参数
GET /api/users?page=1&size=10&status=1&keyword=admin

// 请求体
POST /api/users
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### 2.2 响应格式
```json
// 成功响应
{
  "success": true,
  "errCode": "200",
  "errMessage": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  },
  "timestamp": 1640995200000
}

// 分页响应
{
  "success": true,
  "errCode": "200",
  "errMessage": "success",
  "data": [
    {
      "id": 1,
      "username": "testuser"
    }
  ],
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "pages": 10
}

// 错误响应
{
  "success": false,
  "errCode": "A0201",
  "errMessage": "用户不存在",
  "data": null,
  "timestamp": 1640995200000
}
```

### 3. 接口文档规范

#### 3.1 Swagger注解
```java
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {
    
    @GetMapping
    @ApiOperation("获取用户列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", paramType = "query"),
        @ApiImplicitParam(name = "size", value = "每页大小", paramType = "query")
    })
    public PageResponse<UserDTO> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // 实现
    }
}
```

## 📚 文档规范

### 1. 代码注释

#### 1.1 JavaDoc规范
```java
/**
 * 用户服务类
 * 
 * <p>提供用户相关的业务逻辑处理，包括：</p>
 * <ul>
 *   <li>用户创建和验证</li>
 *   <li>用户信息查询和更新</li>
 *   <li>用户状态管理</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * UserService userService = new UserService();
 * UserDTO user = userService.getUserById(1L);
 * }</pre>
 * 
 * @author 开发者姓名
 * @version 1.0.0
 * @since 1.0.0
 * @see UserDTO
 * @see UserRepository
 */
```

#### 1.2 配置文件注释
```yaml
# 数据库配置
spring:
  datasource:
    # 数据库连接URL
    url: jdbc:mysql://localhost:3306/auth_center
    # 数据库用户名
    username: root
    # 数据库密码
    password: 123456
```

### 2. README文档

#### 2.1 项目README结构
```markdown
# 项目名称

项目简介

## 特性
- 特性1
- 特性2

## 快速开始
### 环境要求
### 安装步骤
### 使用示例

## 文档
- [API文档](./docs/api.md)
- [开发指南](./docs/developer-guide.md)

## 贡献
贡献指南

## 许可证
许可证信息
```

### 3. API文档

#### 3.1 接口文档结构
```markdown
# 接口名称

## 接口描述
接口功能说明

## 请求信息
- **URL**: `/api/users`
- **方法**: `POST`
- **内容类型**: `application/json`

## 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

## 响应示例
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser"
  }
}
```

## 错误码
| 错误码 | 说明 |
|--------|------|
| A0201 | 用户不存在 |
```

## 🔍 代码审查规范

### 1. 审查清单

#### 1.1 代码质量
- [ ] 代码逻辑正确
- [ ] 没有明显的性能问题
- [ ] 错误处理完善
- [ ] 没有硬编码
- [ ] 代码可读性好

#### 1.2 安全性
- [ ] SQL注入防护
- [ ] XSS防护
- [ ] 敏感信息保护
- [ ] 权限控制正确
- [ ] 输入验证完善

#### 1.3 规范性
- [ ] 命名规范
- [ ] 注释完整
- [ ] 格式统一
- [ ] 异常处理规范
- [ ] 日志记录适当

### 2. 审查流程

#### 2.1 Pull Request审查
1. 创建Pull Request
2. 自动化检查通过
3. 至少一人代码审查
4. 修改审查意见
5. 合并到目标分支

#### 2.2 审查意见
```markdown
## 代码审查意见

### 建议
1. 建议将魔法数字提取为常量
2. 建议添加单元测试
3. 建议优化SQL查询

### 问题
1. 存在SQL注入风险
2. 异常处理不完整
3. 日志级别使用不当

### 必须修改
1. 安全漏洞必须修复
2. 功能错误必须修正
```

### 3. 审查工具

#### 3.1 静态代码分析
- SonarQube
- Checkstyle
- PMD
- FindBugs

#### 3.2 代码覆盖率
- JaCoCo
- Cobertura
- Emma

---

💡 **提示**: 代码规范的目的是提高代码质量和可维护性，团队应该定期回顾和更新规范，确保规范的有效性。