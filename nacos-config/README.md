# Nacos配置中心配置说明

## 📋 配置迁移概述

本项目已将数据库、Redis等公共配置迁移到Nacos配置中心，实现配置的集中管理和动态刷新。

## 🔧 配置结构

### 1. 公共配置
- **common-datasource.yml**: 公共数据源配置（MySQL）
- **common-redis.yml**: 公共Redis配置

### 2. 服务专用配置
- **auth-gateway.yml**: 网关服务配置
- **auth-server.yml**: 认证服务配置

## 🚀 配置导入步骤

### 方法一：使用脚本导入（推荐）
```bash
# 进入nacos-config目录
cd nacos-config

# 给脚本执行权限
chmod +x import-nacos-config.sh

# 执行导入脚本
./import-nacos-config.sh
```

### 方法二：手动导入
1. 访问Nacos控制台：http://localhost:8848/nacos
2. 登录账号：nacos/nacos
3. 在"配置管理"->"配置列表"中逐个导入配置文件

## 📊 配置详情

### 公共数据源配置 (common-datasource.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_center?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 公共Redis配置 (common-redis.yml)
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 3000
```

## 🔄 配置动态刷新

所有配置都启用了动态刷新功能，修改Nacos中的配置后，服务会自动重新加载配置，无需重启服务。

## 📁 服务配置说明

### 网关服务 (auth-gateway)
- 端口：8080
- 路由配置：所有API路由规则
- Sentinel配置：流量控制面板

### 认证服务 (auth-server)
- 端口：8001
- 上下文路径：/auth
- 包含认证专用配置

## 🛠️ 本地开发配置

### 本地配置文件结构
每个服务只保留必要的本地配置：
```yaml
spring:
  application:
    name: service-name
  cloud:
    nacos:
      username: nacos
      password: nacos
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
        namespace: public
        group: DEFAULT_GROUP
        shared-configs[0]:
          data-id: common-datasource.yml
          group: DEFAULT_GROUP
          refresh: true
        shared-configs[1]:
          data-id: common-redis.yml
          group: DEFAULT_GROUP
          refresh: true
```

## 🔍 配置验证

### 检查配置是否生效
1. 启动Nacos服务
2. 启动任意微服务
3. 查看服务启动日志，确认配置加载成功
4. 访问服务健康检查接口验证配置正确性

### 配置修改测试
1. 在Nacos控制台修改配置
2. 观察服务日志，确认配置动态刷新
3. 验证配置修改是否生效

## 📝 注意事项

1. **Nacos服务必须启动**：所有微服务依赖Nacos配置中心
2. **配置导入顺序**：先导入公共配置，再导入服务专用配置
3. **环境隔离**：生产环境建议使用不同的namespace进行环境隔离
4. **敏感信息加密**：生产环境建议对密码等敏感信息进行加密存储

## 🔗 相关文档

- [Nacos官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Cloud Alibaba配置中心](https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config)
- [项目部署文档](../docs/deployment.md)