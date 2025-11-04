# 开发者指南

## 📋 目录

- [环境准备](#环境准备)
- [项目结构](#项目结构)
- [开发环境搭建](#开发环境搭建)
- [代码规范](#代码规范)
- [调试指南](#调试指南)
- [提交代码](#提交代码)
- [常见问题](#常见问题)

## 🛠️ 环境准备

### 必需软件

| 软件 | 版本要求 | 下载地址 |
|------|----------|----------|
| JDK | 8+ | [Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html) |
| Maven | 3.6+ | [Maven官网](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | [MySQL官网](https://dev.mysql.com/downloads/mysql/) |
| Redis | 7.0+ | [Redis官网](https://redis.io/download) |
| Nacos | 2.2.x | [Nacos官网](https://nacos.io/zh-cn/docs/quick-start.html) |
| IDE | IntelliJ IDEA / Eclipse | [JetBrains](https://www.jetbrains.com/idea/) |

### 可选软件

| 软件 | 用途 | 版本要求 |
|------|------|----------|
| Docker | 容器化开发 | 20.10+ |
| Node.js | 前端开发 | 16+ |
| Git | 版本控制 | 2.30+ |

## 📁 项目结构

```
auth-center/
├── auth-gateway/          # API网关服务
│   ├── src/
│   │   └── main/
│   │       ├── java/      # Java源码
│   │       └── resources/ # 配置文件
│   └── pom.xml
├── auth-server/           # 认证授权服务
├── user-service/          # 用户管理服务
├── permission-service/    # 权限管理服务
├── client-service/        # 客户端管理服务
├── log-service/           # 日志服务
├── monitor-service/       # 监控服务
├── file-service/          # 文件存储服务
├── message-service/       # 消息服务
├── notification-service/  # 通知服务
├── common/               # 公共模块
│   ├── common-core/      # 核心公共模块
│   ├── common-client/    # 客户端公共模块
│   ├── common-security/  # 安全公共模块
│   └── common-web/       # Web公共模块
├── scripts/              # 脚本文件
│   ├── docker-compose.yml
│   ├── init-database.sql
│   └── init-data.sql
├── docs/                 # 文档目录
├── nacos-config/         # Nacos配置文件
└── pom.xml              # 父级POM文件
```

## 🚀 开发环境搭建

### 1. 克隆项目

```bash
git clone https://github.com/zyxof2016/auth-center.git
cd auth-center
```

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE auth_center DEFAULT CHARACTER SET utf8mb4;"

# 导入初始化脚本
mysql -u root -p auth_center < scripts/init-database.sql
mysql -u root -p auth_center < scripts/init-data.sql
```

### 3. 启动基础服务

#### 方式一：使用Docker Compose（推荐）

```bash
cd scripts
docker-compose up -d mysql redis nacos
```

#### 方式二：手动启动

```bash
# 启动Redis
redis-server

# 启动Nacos
cd nacos/bin
./startup.sh -m standalone
```

### 4. 配置Nacos

访问 http://localhost:8848/nacos，使用 nacos/nacos 登录，导入配置文件：

```bash
cd nacos-config
# 使用脚本导入配置
./import-nacos-config.sh
```

### 5. 启动微服务

按以下顺序启动服务：

```bash
# 1. 启动网关服务
cd auth-gateway
mvn spring-boot:run

# 2. 启动认证服务
cd ../auth-server
mvn spring-boot:run

# 3. 启动其他服务（可并行启动）
cd ../user-service && mvn spring-boot:run &
cd ../permission-service && mvn spring-boot:run &
cd ../client-service && mvn spring-boot:run &
cd ../log-service && mvn spring-boot:run &
cd ../monitor-service && mvn spring-boot:run &
cd ../file-service && mvn spring-boot:run &
cd ../message-service && mvn spring-boot:run &
cd ../notification-service && mvn spring-boot:run &
```

### 6. 验证服务启动

访问以下地址检查服务状态：

```bash
# 网关服务
curl http://localhost:8080/actuator/health

# 认证服务
curl http://localhost:8001/actuator/health

# 用户服务
curl http://localhost:8002/actuator/health
```

## 📝 代码规范

### Java代码规范

#### 1. 命名规范

```java
// 类名：大驼峰命名法
public class UserService {
    
    // 常量：全大写，下划线分隔
    private static final String DEFAULT_PASSWORD = "123456";
    
    // 变量和方法：小驼峰命名法
    private String userName;
    
    public void getUserById() {
        // 方法实现
    }
}
```

#### 2. 包结构规范

```
com.auth.center.{service}.{
    domain/           # 领域层
    ├── entity/       # 实体类
    ├── service/      # 领域服务
    └── repository/   # 仓储接口
    
    application/      # 应用层
    ├── service/      # 应用服务
    ├── dto/          # 数据传输对象
    └── convertor/    # 转换器
    
    infrastructure/   # 基础设施层
    ├── persistence/  # 持久化
    ├── config/       # 配置类
    └── client/       # 外部客户端
    
    interfaces/       # 接口层
    ├── controller/   # 控制器
    └── dto/          # 接口DTO
}
```

#### 3. 注释规范

```java
/**
 * 用户服务
 * 
 * @author 开发者姓名
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     * @throws BusinessException 用户不存在异常
     */
    public UserDTO getUserById(Long userId) {
        // 实现逻辑
    }
}
```

### Git提交规范

#### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type类型

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

#### 示例

```
feat(auth): 添加第三方登录功能

- 实现微信登录
- 实现QQ登录
- 添加第三方账号绑定功能

Closes #123
```

## 🐛 调试指南

### 1. 本地调试

#### IDEA远程调试配置

1. 在启动参数中添加：
```bash
-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
```

2. 在IDEA中创建Remote JVM Debug配置：
```
Host: localhost
Port: 5005
```

### 2. 日志调试

#### 配置日志级别

```yaml
# application.yml
logging:
  level:
    com.auth.center: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

#### 查看实时日志

```bash
# 查看服务日志
tail -f logs/auth-center.log

# 使用grep过滤日志
tail -f logs/auth-center.log | grep "ERROR"
```

### 3. 接口调试

#### 使用curl测试接口

```bash
# 用户登录
curl -X POST http://localhost:8080/api/auth/login/password \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "USERNAME",
    "username": "admin",
    "password": "admin123"
  }'

# 获取用户信息
curl -X GET http://localhost:8080/api/user/1 \
  -H "Authorization: Bearer {token}"
```

#### 使用Postman

导入API集合：`docs/postman/Auth Center.postman_collection.json`

## 🚀 提交代码

### 1. 分支管理

#### 分支命名规范

- `master`: 主分支，用于生产环境
- `develop`: 开发分支，用于集成测试
- `feature/*`: 功能分支
- `bugfix/*`: 修复分支
- `hotfix/*`: 热修复分支

#### 分支操作流程

```bash
# 1. 创建功能分支
git checkout -b feature/user-management

# 2. 开发完成后提交
git add .
git commit -m "feat(user): 添加用户管理功能"

# 3. 推送到远程仓库
git push origin feature/user-management

# 4. 创建Pull Request
# 在GitHub/GitLab上创建PR请求合并到develop分支
```

### 2. 代码审查

#### PR清单

- [ ] 代码符合项目规范
- [ ] 添加了必要的测试用例
- [ ] 更新了相关文档
- [ ] 通过了所有CI检查
- [ ] 没有引入安全漏洞

#### 审查要点

1. **代码质量**
   - 逻辑正确性
   - 性能考虑
   - 错误处理

2. **安全性**
   - SQL注入防护
   - XSS防护
   - 敏感信息保护

3. **可维护性**
   - 代码可读性
   - 注释完整性
   - 模块化程度

### 3. 发布流程

```bash
# 1. 合并到master分支
git checkout master
git merge develop

# 2. 更新版本号
mvn versions:set -DnewVersion=1.0.1

# 3. 打标签
git tag -a v1.0.1 -m "Release version 1.0.1"

# 4. 推送标签
git push origin v1.0.1

# 5. 部署到生产环境
# 执行CI/CD流水线
```

## ❓ 常见问题

### 1. 启动问题

#### Q: 服务启动失败，提示端口被占用

```bash
# 查看端口占用情况
netstat -tulpn | grep :8080

# 杀死占用进程
kill -9 <PID>

# 或者修改配置文件中的端口
```

#### Q: 连接数据库失败

```bash
# 检查数据库服务状态
systemctl status mysql

# 检查数据库连接
mysql -h localhost -u root -p

# 检查配置文件中的数据库连接信息
```

### 2. 开发问题

#### Q: Nacos配置不生效

```bash
# 检查Nacos服务状态
curl http://localhost:8848/nacos/v1/ns/operator/servers

# 检查服务是否注册到Nacos
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=auth-server

# 检查配置是否正确导入
```

#### Q: 跨域问题

```yaml
# 在网关配置中添加跨域支持
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
            allowedHeaders: "*"
            allowCredentials: true
```

### 3. 性能问题

#### Q: 接口响应慢

```bash
# 启用性能监控
management:
  endpoints:
    web:
      exposure:
        include: "health,info,metrics,prometheus"

# 查看性能指标
curl http://localhost:8080/actuator/metrics
```

#### Q: 内存溢出

```bash
# 调整JVM参数
-Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200

# 生成堆转储文件
jmap -dump:live,format=b,file=heap.hprof <PID>
```

## 📚 学习资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud Alibaba文档](https://github.com/alibaba/spring-cloud-alibaba/wiki)
- [COLA架构官网](https://github.com/alibaba/COLA)
- [Nacos官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [RocketMQ官方文档](https://rocketmq.apache.org/zh/docs/)

---

💡 **提示**: 如果您在开发过程中遇到问题，请先查看本文档的常见问题部分，或者提交Issue到项目仓库。