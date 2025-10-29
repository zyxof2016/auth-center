# 部署运维文档

## 1. 环境要求

### 1.1 硬件要求
| 环境 | CPU | 内存 | 磁盘 | 网络 |
|------|-----|------|------|------|
| 开发环境 | 4核 | 8GB | 50GB | 100Mbps |
| 测试环境 | 8核 | 16GB | 100GB | 1Gbps |
| 生产环境 | 16核 | 32GB | 500GB | 10Gbps |

### 1.2 软件要求
| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 8+ | Java运行环境 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.0+ | 缓存数据库 |
| Nacos | 2.2.x | 服务注册发现 |
| Node.js | 16+ | 前端运行环境 |
| Docker | 20.10+ | 容器化部署 |
| Kubernetes | 1.24+ | 容器编排 |

## 2. 开发环境部署

### 2.1 本地开发环境搭建

#### 2.1.1 数据库初始化
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE auth_center DEFAULT CHARACTER SET utf8mb4;"

# 执行初始化脚本
mysql -u root -p auth_center < docs/sql/init.sql
```

#### 2.1.2 服务启动顺序
1. **启动基础设施**
```bash
# 启动Redis
redis-server /usr/local/etc/redis.conf

# 启动Nacos
cd nacos/bin
sh startup.sh -m standalone
```

2. **启动微服务**
```bash
# 启动网关服务
cd auth-gateway
mvn spring-boot:run

# 启动认证服务
cd auth-server
mvn spring-boot:run

# 启动其他服务
cd user-service && mvn spring-boot:run
cd role-service && mvn spring-boot:run
```

3. **启动前端应用**
```bash
cd frontend
npm install
npm run dev
```

### 2.2 Docker Compose快速启动

#### 2.2.1 docker-compose.yml
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: auth_center
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/init-database.sql:/docker-entrypoint-initdb.d/init-database.sql
      - ./scripts/init-data.sql:/docker-entrypoint-initdb.d/init-data.sql

  redis:
    image: redis:7.0-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes

  nacos:
    image: nacos/nacos-server:2.2.3
    environment:
      MODE: standalone
    ports:
      - "8848:8848"

  auth-gateway:
    build: ./auth-gateway
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  auth-server:
    build: ./auth-server
    ports:
      - "8001:8001"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  user-service:
    build: ./user-service
    ports:
      - "8002:8002"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  role-service:
    build: ./role-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  client-service:
    build: ./client-service
    ports:
      - "8083:8083"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  log-service:
    build: ./log-service
    ports:
      - "8084:8084"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  monitor-service:
    build: ./monitor-service
    ports:
      - "8085:8085"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  file-service:
    build: ./file-service
    ports:
      - "8086:8086"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  message-service:
    build: ./message-service
    ports:
      - "8087:8087"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  notification-service:
    build: ./notification-service
    ports:
      - "8088:8088"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

  role-service:
    build: ./role-service
    ports:
      - "8003:8003"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - nacos
      - redis
      - mysql

volumes:
  mysql_data:
```

#### 2.2.2 启动命令
```bash
# 一键启动所有服务
cd scripts && docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f auth-server

# 停止服务
docker-compose down

# 重新构建并启动
docker-compose up --build -d
```

## 3. 生产环境部署

### 3.1 Kubernetes集群部署

#### 3.1.1 命名空间配置
```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: auth-center
  labels:
    name: auth-center
```

#### 3.1.2 配置映射
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: auth-center-config
  namespace: auth-center
data:
  application.yml: |
    spring:
      datasource:
        url: jdbc:mysql://mysql.auth-center:3306/auth_center
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
    nacos:
      server-addr: nacos.auth-center:8848
```

#### 3.1.3 服务部署
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-gateway
  namespace: auth-center
spec:
  replicas: 2
  selector:
    matchLabels:
      app: auth-gateway
  template:
    metadata:
      labels:
        app: auth-gateway
    spec:
      containers:
      - name: auth-gateway
        image: registry.example.com/auth-center/auth-gateway:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: auth-gateway
  namespace: auth-center
spec:
  selector:
    app: auth-gateway
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### 3.1.4 服务入口配置
```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: auth-center-ingress
  namespace: auth-center
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - auth.example.com
    secretName: auth-center-tls
  rules:
  - host: auth.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: auth-gateway
            port:
              number: 80
```

### 3.2 数据库部署

#### 3.2.1 MySQL主从配置
```sql
-- 主库配置
[mysqld]
server-id=1
log-bin=mysql-bin
binlog-format=ROW

-- 从库配置
[mysqld]
server-id=2
relay-log=mysql-relay-bin
read-only=1
```

#### 3.2.2 Redis集群配置
```yaml
# redis-cluster.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: redis-cluster-config
  namespace: auth-center
data:
  redis.conf: |
    cluster-enabled yes
    cluster-config-file nodes.conf
    cluster-node-timeout 5000
    appendonly yes
```

## 4. 配置管理

### 4.1 Nacos配置中心

#### 4.1.1 公共配置
```yaml
# application.yml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  redis:
    host: ${REDIS_HOST:redis}
    port: ${REDIS_PORT:6379}
    database: 0
    timeout: 3000
    lettuce:
      pool:
        max-active: 20
        max-wait: -1
        max-idle: 10
        min-idle: 0
```

#### 4.1.2 服务专属配置
```yaml
# auth-server.yml
server:
  port: 8001

spring:
  security:
    oauth2:
      authorization:
        check-token-access: isAuthenticated()
      resource:
        token-info-uri: http://auth-server:8001/oauth/check_token

jwt:
  secret: ${JWT_SECRET:defaultSecretKey}
  expiration: 7200
```

### 4.2 环境变量配置

#### 4.2.1 环境变量清单
```bash
# 数据库配置
DB_HOST=mysql.auth-center
DB_PORT=3306
DB_NAME=auth_center
DB_USERNAME=admin
DB_PASSWORD=secure_password

# Redis配置
REDIS_HOST=redis.auth-center
REDIS_PORT=6379
REDIS_PASSWORD=redis_password

# Nacos配置
NACOS_HOST=nacos.auth-center
NACOS_PORT=8848

# JWT配置
JWT_SECRET=your_jwt_secret_key

# 文件存储
FILE_UPLOAD_PATH=/data/uploads
```

## 5. 监控告警

### 5.1 Spring Boot Actuator配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

### 5.2 Prometheus监控配置
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'auth-center'
    scrape_interval: 15s
    static_configs:
      - targets: ['auth-gateway:8080', 'auth-server:8001']
    metrics_path: '/actuator/prometheus'
```

### 5.3 告警规则配置
```yaml
# alert-rules.yml
groups:
- name: auth-center-alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "High error rate detected"
      description: "Error rate is {{ $value }} per second"

  - alert: ServiceDown
    expr: up{job="auth-center"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Service {{ $labels.instance }} is down"
```

## 6. 日志管理

### 6.1 日志配置
```yaml
# logback-spring.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/auth-center.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/auth-center.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 6.2 ELK日志收集
```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  paths:
    - /var/log/auth-center/*.log
  fields:
    service: auth-center

output.logstash:
  hosts: ["logstash:5044"]
```

## 7. 备份恢复

### 7.1 数据库备份
```bash
#!/bin/bash
# backup.sh

# 数据库备份
mysqldump -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME > /backup/auth-center-$(date +%Y%m%d).sql

# 压缩备份文件
gzip /backup/auth-center-$(date +%Y%m%d).sql

# 保留最近7天的备份
find /backup -name "auth-center-*.sql.gz" -mtime +7 -delete
```

### 7.2 配置文件备份
```bash
# 备份Nacos配置
curl -X GET "http://nacos:8848/nacos/v1/cs/configs?dataId=auth-server.yml&group=DEFAULT_GROUP" > /backup/config/auth-server-$(date +%Y%m%d).yml
```

## 8. 安全配置

### 8.1 网络安全
```yaml
# network-policy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: auth-center-policy
  namespace: auth-center
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: auth-center
    ports:
    - protocol: TCP
      port: 8080
```

### 8.2 SSL/TLS配置
```yaml
# tls-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: auth-center-tls
  namespace: auth-center
type: kubernetes.io/tls
data:
  tls.crt: <base64-encoded-cert>
  tls.key: <base64-encoded-key>
```

## 9. 性能优化

### 9.1 JVM参数优化
```bash
# JVM参数
JAVA_OPTS="-Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=4 -XX:ConcGCThreads=2"
```

### 9.2 数据库连接池优化
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 10. 故障排查

### 10.1 常见问题解决

#### 10.1.1 服务无法启动
```bash
# 检查服务状态
kubectl get pods -n auth-center

# 查看服务日志
kubectl logs -f deployment/auth-gateway -n auth-center

# 检查资源限制
kubectl describe pod auth-gateway-xxx -n auth-center
```

#### 10.1.2 数据库连接问题
```bash
# 检查数据库连接
mysql -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD -e "SHOW DATABASES;"

# 检查连接池状态
curl http://localhost:8080/actuator/health
```

#### 10.1.3 内存泄漏排查
```bash
# 查看JVM内存使用
jstat -gc <pid> 1s

# 生成堆转储文件
jmap -dump:live,format=b,file=heap.hprof <pid>
```

### 10.2 监控指标说明
- **QPS**: 每秒请求数，正常范围 < 1000
- **响应时间**: 平均响应时间 < 200ms
- **错误率**: 错误请求比例 < 1%
- **内存使用率**: JVM堆内存使用率 < 80%
- **CPU使用率**: 系统CPU使用率 < 80%