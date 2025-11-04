# 部署运维文档

## 📋 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [Docker部署](#docker部署)
- [Kubernetes部署](#kubernetes部署)
- [配置管理](#配置管理)
- [监控告警](#监控告警)
- [日志管理](#日志管理)
- [备份恢复](#备份恢复)
- [安全配置](#安全配置)
- [性能优化](#性能优化)
- [故障排查](#故障排查)

## 🖥️ 环境要求

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
| Maven | 3.6+ | 构建工具 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.0+ | 缓存数据库 |
| Nacos | 2.2.x | 服务注册发现 |
| Docker | 20.10+ | 容器化部署 |
| Kubernetes | 1.24+ | 容器编排 |
| Node.js | 16+ | 前端运行环境（可选） |

### 1.3 端口规划
| 服务 | 端口 | 说明 |
|------|------|------|
| auth-gateway | 8080 | API网关 |
| auth-server | 8001 | 认证服务 |
| user-service | 8002 | 用户服务 |
| permission-service | 8082 | 权限服务 |
| client-service | 8083 | 客户端服务 |
| log-service | 8084 | 日志服务 |
| monitor-service | 8085 | 监控服务 |
| file-service | 8086 | 文件服务 |
| message-service | 8087 | 消息服务 |
| notification-service | 8088 | 通知服务 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| Nacos | 8848 | 注册中心 |
| MinIO | 9000/9001 | 对象存储 |

## 🚀 快速开始

### 2.1 本地开发环境搭建

#### 2.1.1 环境准备
```bash
# 1. 克隆项目
git clone https://github.com/zyxof2016/auth-center.git
cd auth-center

# 2. 数据库初始化
mysql -u root -p -e "CREATE DATABASE auth_center DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p auth_center < scripts/init-database.sql
mysql -u root -p auth_center < scripts/init-data.sql

# 3. 启动基础服务
cd scripts
docker-compose up -d mysql redis nacos

# 4. 导入Nacos配置
cd ../nacos-config
./import-nacos-config.sh
```

#### 2.1.2 服务启动
```bash
# 按顺序启动服务
cd auth-gateway && mvn spring-boot:run &
cd auth-server && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd permission-service && mvn spring-boot:run &
cd client-service && mvn spring-boot:run &
cd log-service && mvn spring-boot:run &
cd monitor-service && mvn spring-boot:run &
cd file-service && mvn spring-boot:run &
cd message-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &
```

#### 2.1.3 验证部署
```bash
# 检查服务状态
curl http://localhost:8080/actuator/health
curl http://localhost:8001/actuator/health

# 测试登录接口
curl -X POST http://localhost:8080/api/auth/login/password \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "USERNAME",
    "username": "admin",
    "password": "admin123"
  }'
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

## 🐳 Docker部署

### 3.1 构建Docker镜像

#### 3.1.1 创建Dockerfile
```dockerfile
# auth-gateway/Dockerfile
FROM openjdk:8-jre-alpine

LABEL maintainer="auth-center@example.com"
LABEL version="1.0.0"

# 设置工作目录
WORKDIR /app

# 复制jar文件
COPY target/auth-gateway-1.0.0-SNAPSHOT.jar app.jar

# 设置时区
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
```

#### 3.1.2 构建脚本
```bash
#!/bin/bash
# build-images.sh

# 构建所有服务的Docker镜像
services=("auth-gateway" "auth-server" "user-service" "permission-service" "client-service" "log-service" "monitor-service" "file-service" "message-service" "notification-service")

for service in "${services[@]}"; do
    echo "Building $service image..."
    cd $service
    mvn clean package -DskipTests
    docker build -t auth-center/$service:1.0.0 .
    cd ..
done

echo "All images built successfully!"
```

### 3.2 生产环境Docker Compose

#### 3.2.1 生产配置
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  # MySQL主库
  mysql-master:
    image: mysql:8.0
    container_name: auth-center-mysql-master
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: auth_center
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql_master_data:/var/lib/mysql
      - ./mysql/master.cnf:/etc/mysql/conf.d/mysql.cnf
      - ./backup:/backup
    command: --server-id=1 --log-bin=mysql-bin --binlog-format=ROW
    networks:
      - auth-center-network

  # Redis集群
  redis-master:
    image: redis:7.0-alpine
    container_name: auth-center-redis-master
    restart: unless-stopped
    ports:
      - "6379:6379"
    volumes:
      - redis_master_data:/data
      - ./redis/redis.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
    networks:
      - auth-center-network

  redis-slave:
    image: redis:7.0-alpine
    container_name: auth-center-redis-slave
    restart: unless-stopped
    ports:
      - "6380:6379"
    volumes:
      - redis_slave_data:/data
    command: redis-server --slaveof redis-master 6379
    depends_on:
      - redis-master
    networks:
      - auth-center-network

  # Nacos集群
  nacos1:
    image: nacos/nacos-server:v2.2.3
    container_name: auth-center-nacos1
    restart: unless-stopped
    environment:
      - MODE=cluster
      - NACOS_SERVERS=nacos2:8848 nacos3:8848
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=mysql-master
      - MYSQL_SERVICE_DB_NAME=nacos
      - MYSQL_SERVICE_USER=${MYSQL_USER}
      - MYSQL_SERVICE_PASSWORD=${MYSQL_PASSWORD}
    ports:
      - "8848:8848"
    volumes:
      - nacos1_data:/home/nacos/data
      - nacos1_logs:/home/nacos/logs
    depends_on:
      - mysql-master
    networks:
      - auth-center-network

  # 应用服务
  auth-gateway:
    image: auth-center/auth-gateway:1.0.0
    container_name: auth-center-gateway
    restart: unless-stopped
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_CLOUD_NACOS_SERVER_ADDR=nacos1:8848,nacos2:8848,nacos3:8848
      - SPRING_REDIS_HOST=redis-master
      - SPRING_REDIS_SENTINEL_MASTER=mymaster
    ports:
      - "8080:8080"
    depends_on:
      - nacos1
      - redis-master
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
    networks:
      - auth-center-network

  auth-server:
    image: auth-center/auth-server:1.0.0
    container_name: auth-center-auth-server
    restart: unless-stopped
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-master:3306/auth_center
      - SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
      - SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}
    depends_on:
      - mysql-master
      - nacos1
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
    networks:
      - auth-center-network

  # 监控服务
  prometheus:
    image: prom/prometheus:latest
    container_name: auth-center-prometheus
    restart: unless-stopped
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
    networks:
      - auth-center-network

  grafana:
    image: grafana/grafana:latest
    container_name: auth-center-grafana
    restart: unless-stopped
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./monitoring/grafana/datasources:/etc/grafana/provisioning/datasources
    depends_on:
      - prometheus
    networks:
      - auth-center-network

volumes:
  mysql_master_data:
  redis_master_data:
  redis_slave_data:
  nacos1_data:
  nacos1_logs:
  prometheus_data:
  grafana_data:

networks:
  auth-center-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

#### 3.2.2 环境变量文件
```bash
# .env.prod
# 数据库配置
MYSQL_ROOT_PASSWORD=YourRootPassword123!
MYSQL_USER=auth_user
MYSQL_PASSWORD=AuthUserPassword123!

# Redis配置
REDIS_PASSWORD=RedisPassword123!

# Nacos配置
NACOS_USERNAME=nacos
NACOS_PASSWORD=NacosPassword123!

# Grafana配置
GRAFANA_PASSWORD=GrafanaPassword123!

# JWT配置
JWT_SECRET=YourJWTSecretKey123456789

# 文件存储配置
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

## ☸️ Kubernetes部署

### 4.1 集群规划

#### 4.1.1 节点规划
| 节点类型 | 数量 | 配置 | 用途 |
|----------|------|------|------|
| Master | 3 | 4C8G | 控制平面 |
| Worker | 6 | 8C16G | 应用节点 |
| Storage | 3 | 4C8G | 存储节点 |

#### 4.1.2 命名空间
```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: auth-center
  labels:
    name: auth-center
    environment: production
---
apiVersion: v1
kind: Namespace
metadata:
  name: auth-center-monitoring
  labels:
    name: auth-center-monitoring
```

### 4.2 配置管理

#### 4.2.1 ConfigMap
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
        url: jdbc:mysql://mysql.auth-center:3306/auth_center?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
        hikari:
          maximum-pool-size: 20
          minimum-idle: 5
          connection-timeout: 30000
          idle-timeout: 600000
          max-lifetime: 1800000
      
      redis:
        host: redis.auth-center
        port: 6379
        password: ${REDIS_PASSWORD}
        database: 0
        timeout: 3000
        lettuce:
          pool:
            max-active: 20
            max-wait: -1
            max-idle: 10
            min-idle: 0
      
      cloud:
        nacos:
          server-addr: nacos.auth-center:8848
          username: ${NACOS_USERNAME}
          password: ${NACOS_PASSWORD}
          discovery:
            namespace: auth-center
          config:
            namespace: auth-center
    
    management:
      endpoints:
        web:
          exposure:
            include: "*"
      endpoint:
        health:
          show-details: always
      metrics:
        export:
          prometheus:
            enabled: true
    
    logging:
      level:
        com.auth.center: INFO
        org.springframework.security: WARN
      pattern:
        console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
      file:
        name: /app/logs/auth-center.log
        max-size: 100MB
        max-history: 30

  logback-spring.xml: |
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/app/logs/auth-center.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/app/logs/auth-center.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>10GB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="FILE"/>
        </root>
    </configuration>
```

#### 4.2.2 Secret
```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: auth-center-secret
  namespace: auth-center
type: Opaque
data:
  # Base64编码的值
  db-username: YXV0aF91c2Vy  # auth_user
  db-password: QXV0aFVzZXJQYXNzd29yZDEyMyE=  # AuthUserPassword123!
  redis-password: UmVkaXNQYXNzd29yZDEyMyE=  # RedisPassword123!
  nacos-username: bmFjb3M=  # nacos
  nacos-password: TmFjb3NQYXNzd29yZDEyMyE=  # NacosPassword123!
  jwt-secret: WW91ckpXVFNlY3JldEtleTEyMzQ1Njc4OQ==  # YourJWTSecretKey123456789
```

### 4.3 存储配置

#### 4.3.1 持久化存储
```yaml
# storage.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: mysql-pv
  namespace: auth-center
spec:
  capacity:
    storage: 100Gi
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: fast-ssd
  hostPath:
    path: /data/mysql
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: auth-center
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 100Gi
  storageClassName: fast-ssd
```

### 4.4 应用部署

#### 4.4.1 认证服务部署
```yaml
# auth-server-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-server
  namespace: auth-center
  labels:
    app: auth-server
    version: v1.0.0
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-server
  template:
    metadata:
      labels:
        app: auth-server
        version: v1.0.0
    spec:
      containers:
      - name: auth-server
        image: auth-center/auth-server:1.0.0
        imagePullPolicy: Always
        ports:
        - containerPort: 8001
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: db-username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: db-password
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: redis-password
        - name: NACOS_USERNAME
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: nacos-username
        - name: NACOS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: nacos-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: jwt-secret
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        - name: logs-volume
          mountPath: /app/logs
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8001
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8001
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
      volumes:
      - name: config-volume
        configMap:
          name: auth-center-config
      - name: logs-volume
        emptyDir: {}
      imagePullSecrets:
      - name: registry-secret
---
apiVersion: v1
kind: Service
metadata:
  name: auth-server
  namespace: auth-center
  labels:
    app: auth-server
spec:
  selector:
    app: auth-server
  ports:
  - name: http
    port: 8001
    targetPort: 8001
    protocol: TCP
  type: ClusterIP
```

#### 4.4.2 网关服务部署
```yaml
# auth-gateway-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-gateway
  namespace: auth-center
  labels:
    app: auth-gateway
    version: v1.0.0
spec:
  replicas: 2
  selector:
    matchLabels:
      app: auth-gateway
  template:
    metadata:
      labels:
        app: auth-gateway
        version: v1.0.0
    spec:
      containers:
      - name: auth-gateway
        image: auth-center/auth-gateway:1.0.0
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: redis-password
        - name: NACOS_USERNAME
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: nacos-username
        - name: NACOS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: auth-center-secret
              key: nacos-password
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
      volumes:
      - name: config-volume
        configMap:
          name: auth-center-config
---
apiVersion: v1
kind: Service
metadata:
  name: auth-gateway
  namespace: auth-center
  labels:
    app: auth-gateway
spec:
  selector:
    app: auth-gateway
  ports:
  - name: http
    port: 80
    targetPort: 8080
    protocol: TCP
  type: LoadBalancer
  loadBalancerIP: 192.168.1.100
```

### 4.5 Ingress配置

#### 4.5.1 HTTPS入口
```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: auth-center-ingress
  namespace: auth-center
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/use-regex: "true"
    nginx.ingress.kubernetes.io/rewrite-target: /$2
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/rate-limit: "100"
    nginx.ingress.kubernetes.io/rate-limit-window: "1m"
spec:
  tls:
  - hosts:
    - auth.example.com
    secretName: auth-center-tls
  rules:
  - host: auth.example.com
    http:
      paths:
      - path: /api(/|$)(.*)
        pathType: Prefix
        backend:
          service:
            name: auth-gateway
            port:
              number: 80
```

### 4.6 自动扩缩容

#### 4.6.1 HPA配置
```yaml
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: auth-gateway-hpa
  namespace: auth-center
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: auth-gateway
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
```

## 📊 监控告警

### 5.1 Prometheus配置

#### 5.1.1 Prometheus部署
```yaml
# prometheus-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus
  namespace: auth-center-monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus
  template:
    metadata:
      labels:
        app: prometheus
    spec:
      containers:
      - name: prometheus
        image: prom/prometheus:v2.40.0
        ports:
        - containerPort: 9090
        volumeMounts:
        - name: config-volume
          mountPath: /etc/prometheus
        - name: storage-volume
          mountPath: /prometheus
        command:
        - '/bin/prometheus'
        - '--config.file=/etc/prometheus/prometheus.yml'
        - '--storage.tsdb.path=/prometheus'
        - '--web.console.libraries=/etc/prometheus/console_libraries'
        - '--web.console.templates=/etc/prometheus/consoles'
        - '--storage.tsdb.retention.time=30d'
        - '--web.enable-lifecycle'
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
      volumes:
      - name: config-volume
        configMap:
          name: prometheus-config
      - name: storage-volume
        persistentVolumeClaim:
          claimName: prometheus-pvc
```

#### 5.1.2 监控配置
```yaml
# prometheus-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-config
  namespace: auth-center-monitoring
data:
  prometheus.yml: |
    global:
      scrape_interval: 15s
      evaluation_interval: 15s
    
    rule_files:
      - "/etc/prometheus/rules/*.yml"
    
    alerting:
      alertmanagers:
        - static_configs:
            - targets:
              - alertmanager:9093
    
    scrape_configs:
      - job_name: 'prometheus'
        static_configs:
          - targets: ['localhost:9090']
      
      - job_name: 'auth-center'
        kubernetes_sd_configs:
          - role: pod
            namespaces:
              names:
                - auth-center
        relabel_configs:
          - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
            action: keep
            regex: true
          - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
            action: replace
            target_label: __metrics_path__
            regex: (.+)
          - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
            action: replace
            regex: ([^:]+)(?::\d+)?;(\d+)
            replacement: $1:$2
            target_label: __address__
          - action: labelmap
            regex: __meta_kubernetes_pod_label_(.+)
          - source_labels: [__meta_kubernetes_namespace]
            action: replace
            target_label: kubernetes_namespace
          - source_labels: [__meta_kubernetes_pod_name]
            action: replace
            target_label: kubernetes_pod_name
      
      - job_name: 'kubernetes-apiservers'
        kubernetes_sd_configs:
          - role: endpoints
        scheme: https
        tls_config:
          ca_file: /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
        bearer_token_file: /var/run/secrets/kubernetes.io/serviceaccount/token
        relabel_configs:
          - source_labels: [__meta_kubernetes_namespace, __meta_kubernetes_service_name, __meta_kubernetes_endpoint_port_name]
            action: keep
            regex: default;kubernetes;https
      
      - job_name: 'kubernetes-nodes'
        kubernetes_sd_configs:
          - role: node
        scheme: https
        tls_config:
          ca_file: /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
        bearer_token_file: /var/run/secrets/kubernetes.io/serviceaccount/token
        relabel_configs:
          - action: labelmap
            regex: __meta_kubernetes_node_label_(.+)
      
      - job_name: 'kubernetes-cadvisor'
        kubernetes_sd_configs:
          - role: node
        scheme: https
        metrics_path: /metrics/cadvisor
        tls_config:
          ca_file: /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
        bearer_token_file: /var/run/secrets/kubernetes.io/serviceaccount/token
        relabel_configs:
          - action: labelmap
            regex: __meta_kubernetes_node_label_(.+)
          - target_label: __address__
            replacement: kubernetes.default.svc:443
          - source_labels: [__meta_kubernetes_node_name]
            regex: (.+)
            target_label: __metrics_path__
            replacement: /api/v1/nodes/${1}/proxy/metrics/cadvisor
```

### 5.2 告警规则

#### 5.2.1 业务告警规则
```yaml
# alert-rules.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-rules
  namespace: auth-center-monitoring
data:
  auth-center.yml: |
    groups:
    - name: auth-center.rules
      rules:
      # 服务可用性告警
      - alert: ServiceDown
        expr: up{job=~"auth-center.*"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.instance }} is down"
          description: "Service {{ $labels.instance }} has been down for more than 1 minute."
      
      # 高错误率告警
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / rate(http_server_requests_seconds_count[5m]) > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }} for {{ $labels.instance }}"
      
      # 高响应时间告警
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "95th percentile response time is {{ $value }}s for {{ $labels.instance }}"
      
      # 内存使用率告警
      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes / jvm_memory_max_bytes) * 100 > 85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage detected"
          description: "Memory usage is {{ $value }}% for {{ $labels.instance }}"
      
      # CPU使用率告警
      - alert: HighCpuUsage
        expr: rate(process_cpu_seconds_total[5m]) * 100 > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage detected"
          description: "CPU usage is {{ $value }}% for {{ $labels.instance }}"
      
      # 数据库连接池告警
      - alert: DatabaseConnectionPoolExhaustion
        expr: hikaricp_connections_active / hikaricp_connections_max > 0.9
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool nearly exhausted"
          description: "Connection pool usage is {{ $value | humanizePercentage }} for {{ $labels instance }}"
      
      # 登录失败率告警
      - alert: HighLoginFailureRate
        expr: rate(login_failures_total[5m]) / rate(login_attempts_total[5m]) > 0.3
        for: 3m
        labels:
          severity: warning
        annotations:
          summary: "High login failure rate detected"
          description: "Login failure rate is {{ $value | humanizePercentage }}"
      
      # 令牌刷新频率异常告警
      - alert: UnusualTokenRefreshRate
        expr: rate(token_refresh_requests_total[5m]) > 100
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Unusual token refresh rate"
          description: "Token refresh rate is {{ $value }} requests per second"
```

### 5.3 Grafana仪表板

#### 5.3.1 Grafana配置
```yaml
# grafana-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: auth-center-monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:9.3.0
        ports:
        - containerPort: 3000
        env:
        - name: GF_SECURITY_ADMIN_PASSWORD
          valueFrom:
            secretKeyRef:
              name: grafana-secret
              key: admin-password
        - name: GF_INSTALL_PLUGINS
          value: "grafana-clock-panel,grafana-simple-json-datasource"
        volumeMounts:
        - name: grafana-storage
          mountPath: /var/lib/grafana
        - name: grafana-config
          mountPath: /etc/grafana/provisioning
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
      volumes:
      - name: grafana-storage
        persistentVolumeClaim:
          claimName: grafana-pvc
      - name: grafana-config
        configMap:
          name: grafana-config
```

## 🔧 配置管理

### 6.1 Nacos配置中心

#### 6.1.1 配置导入脚本
```bash
#!/bin/bash
# import-configs.sh

NACOS_SERVER="http://localhost:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
CONFIG_DIR="./nacos-config"

# 导入配置文件
import_config() {
    local data_id=$1
    local group=$2
    local file_path=$3
    
    echo "Importing $data_id..."
    
    curl -X POST "$NACOS_SERVER/nacos/v1/cs/configs" \
        -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        -d "dataId=$data_id&group=$group&content=$(cat $file_path)"
    
    if [ $? -eq 0 ]; then
        echo "Successfully imported $data_id"
    else
        echo "Failed to import $data_id"
    fi
}

# 导入公共配置
import_config "common-datasource.yml" "DEFAULT_GROUP" "$CONFIG_DIR/common-datasource.yml"
import_config "common-redis.yml" "DEFAULT_GROUP" "$CONFIG_DIR/common-redis.yml"

# 导入服务配置
import_config "auth-gateway.yml" "DEFAULT_GROUP" "$CONFIG_DIR/auth-gateway.yml"
import_config "auth-server.yml" "DEFAULT_GROUP" "$CONFIG_DIR/auth-server.yml"
import_config "user-service.yml" "DEFAULT_GROUP" "$CONFIG_DIR/user-service.yml"

echo "All configurations imported successfully!"
```

#### 6.1.2 配置备份脚本
```bash
#!/bin/bash
# backup-configs.sh

NACOS_SERVER="http://localhost:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
BACKUP_DIR="./backup/$(date +%Y%m%d)"

mkdir -p $BACKUP_DIR

# 备份配置列表
configs=(
    "common-datasource.yml:DEFAULT_GROUP"
    "common-redis.yml:DEFAULT_GROUP"
    "auth-gateway.yml:DEFAULT_GROUP"
    "auth-server.yml:DEFAULT_GROUP"
    "user-service.yml:DEFAULT_GROUP"
)

for config in "${configs[@]}"; do
    IFS=':' read -r data_id group <<< "$config"
    
    echo "Backing up $data_id..."
    
    curl -X GET "$NACOS_SERVER/nacos/v1/cs/configs" \
        -u "$NACOS_USERNAME:$NACOS_PASSWORD" \
        -d "dataId=$data_id&group=$group" \
        -o "$BACKUP_DIR/$data_id"
    
    if [ $? -eq 0 ]; then
        echo "Successfully backed up $data_id"
    else
        echo "Failed to backup $data_id"
    fi
done

echo "Configuration backup completed!"
```

## 📝 日志管理

### 7.1 ELK Stack部署

#### 7.1.1 Elasticsearch部署
```yaml
# elasticsearch-deployment.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: elasticsearch
  namespace: auth-center-monitoring
spec:
  serviceName: elasticsearch
  replicas: 3
  selector:
    matchLabels:
      app: elasticsearch
  template:
    metadata:
      labels:
        app: elasticsearch
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:8.5.0
        ports:
        - containerPort: 9200
          name: http
        - containerPort: 9300
          name: transport
        env:
        - name: cluster.name
          value: "auth-center-logs"
        - name: node.name
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: discovery.seed_hosts
          value: "elasticsearch-0.elasticsearch,elasticsearch-1.elasticsearch,elasticsearch-2.elasticsearch"
        - name: cluster.initial_master_nodes
          value: "elasticsearch-0,elasticsearch-1,elasticsearch-2"
        - name: ES_JAVA_OPTS
          value: "-Xms1g -Xmx1g"
        - name: xpack.security.enabled
          value: "false"
        volumeMounts:
        - name: elasticsearch-data
          mountPath: /usr/share/elasticsearch/data
        resources:
          requests:
            memory: 1Gi
            cpu: 500m
          limits:
            memory: 2Gi
            cpu: 1000m
  volumeClaimTemplates:
  - metadata:
      name: elasticsearch-data
    spec:
      accessModes: [ "ReadWriteOnce" ]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 50Gi
```

#### 7.1.2 Logstash配置
```yaml
# logstash-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: logstash
  namespace: auth-center-monitoring
spec:
  replicas: 2
  selector:
    matchLabels:
      app: logstash
  template:
    metadata:
      labels:
        app: logstash
    spec:
      containers:
      - name: logstash
        image: docker.elastic.co/logstash/logstash:8.5.0
        ports:
        - containerPort: 5044
          name: beats
        - containerPort: 9600
          name: http
        env:
        - name: LS_JAVA_OPTS
          value: "-Xmx1g -Xms1g"
        volumeMounts:
        - name: logstash-config
          mountPath: /usr/share/logstash/pipeline
        resources:
          requests:
            memory: 1Gi
            cpu: 500m
          limits:
            memory: 2Gi
            cpu: 1000m
      volumes:
      - name: logstash-config
        configMap:
          name: logstash-config
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: logstash-config
  namespace: auth-center-monitoring
data:
  logstash.yml: |
    http.host: "0.0.0.0"
    path.config: /usr/share/logstash/pipeline
  pipeline.conf: |
    input {
      beats {
        port => 5044
      }
    }
    filter {
      if [fields][service] {
        mutate {
          add_field => { "service_name" => "%{[fields][service]}" }
        }
      }
      
      date {
        match => [ "timestamp", "yyyy-MM-dd HH:mm:ss.SSS" ]
      }
      
      if [level] == "ERROR" {
        mutate {
          add_tag => [ "error" ]
        }
      }
    }
    output {
      elasticsearch {
        hosts => [ "elasticsearch:9200" ]
        index => "auth-center-logs-%{+YYYY.MM.dd}"
      }
    }
```

#### 7.1.3 Filebeat配置
```yaml
# filebeat-daemonset.yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: filebeat
  namespace: auth-center-monitoring
spec:
  selector:
    matchLabels:
      app: filebeat
  template:
    metadata:
      labels:
        app: filebeat
    spec:
      containers:
      - name: filebeat
        image: docker.elastic.co/beats/filebeat:8.5.0
        args: [
          "-c", "/etc/filebeat.yml",
          "-e",
        ]
        env:
        - name: ELASTICSEARCH_HOST
          value: elasticsearch:9200
        - name: LOGSTASH_HOST
          value: logstash:5044
        securityContext:
          runAsUser: 0
        volumeMounts:
        - name: config
          mountPath: /etc/filebeat.yml
          readOnly: true
          subPath: filebeat.yml
        - name: logs
          mountPath: /app/logs
          readOnly: true
        resources:
          requests:
            memory: 200Mi
            cpu: 100m
          limits:
            memory: 500Mi
            cpu: 200m
      volumes:
      - name: config
        configMap:
          defaultMode: 0600
          name: filebeat-config
      - name: logs
        hostPath:
          path: /var/log/auth-center
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: filebeat-config
  namespace: auth-center-monitoring
data:
  filebeat.yml: |
    filebeat.inputs:
    - type: log
      enabled: true
      paths:
        - /app/logs/*.log
      fields:
        service: auth-center
        environment: production
      fields_under_root: true
      multiline.pattern: '^\d{4}-\d{2}-\d{2}'
      multiline.negate: true
      multiline.match: after
    
    output.logstash:
      hosts: ["${LOGSTASH_HOST:5044}"]
    
    logging.level: info
    logging.to_files: true
    logging.files:
      path: /var/log/filebeat
      name: filebeat
      keepfiles: 7
      permissions: 0644
```

## 🔒 安全配置

### 8.1 网络安全

#### 8.1.1 网络策略
```yaml
# network-policy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: auth-center-network-policy
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
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
  - ports:
    - protocol: TCP
      port: 8080
    - protocol: TCP
      port: 8001
    - protocol: TCP
      port: 8002
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: auth-center
  - to: []
    ports:
    - protocol: TCP
      port: 53
    - protocol: UDP
      port: 53
    - protocol: TCP
      port: 443
    - protocol: TCP
      port: 80
```

#### 8.1.2 Pod安全策略
```yaml
# pod-security-policy.yaml
apiVersion: policy/v1beta1
kind: PodSecurityPolicy
metadata:
  name: auth-center-psp
spec:
  privileged: false
  allowPrivilegeEscalation: false
  requiredDropCapabilities:
    - ALL
  volumes:
    - 'configMap'
    - 'emptyDir'
    - 'projected'
    - 'secret'
    - 'downwardAPI'
    - 'persistentVolumeClaim'
  runAsUser:
    rule: 'MustRunAsNonRoot'
  seLinux:
    rule: 'RunAsAny'
  fsGroup:
    rule: 'RunAsAny'
```

### 8.2 RBAC权限控制

#### 8.2.1 服务账户
```yaml
# service-account.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: auth-center-sa
  namespace: auth-center
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: auth-center-role
  namespace: auth-center
rules:
- apiGroups: [""]
  resources: ["pods", "services", "configmaps", "secrets"]
  verbs: ["get", "list", "watch"]
- apiGroups: ["apps"]
  resources: ["deployments", "replicasets"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: auth-center-rolebinding
  namespace: auth-center
subjects:
- kind: ServiceAccount
  name: auth-center-sa
  namespace: auth-center
roleRef:
  kind: Role
  name: auth-center-role
  apiGroup: rbac.authorization.k8s.io
```

## 🚀 性能优化

### 9.1 JVM优化

#### 9.1.1 生产环境JVM参数
```yaml
# jvm-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: jvm-config
  namespace: auth-center
data:
  jvm.options: |
    # 堆内存设置
    -Xms1g
    -Xmx2g
    
    # GC设置
    -XX:+UseG1GC
    -XX:MaxGCPauseMillis=200
    -XX:G1HeapRegionSize=16m
    -XX:G1NewSizePercent=30
    -XX:G1MaxNewSizePercent=40
    -XX:G1MixedGCCountTarget=8
    -XX:InitiatingHeapOccupancyPercent=45
    -XX:G1MixedGCLiveThresholdPercent=85
    
    # GC日志
    -Xloggc:/app/logs/gc.log
    -XX:+PrintGCDetails
    -XX:+PrintGCTimeStamps
    -XX:+PrintGCApplicationStoppedTime
    -XX:+PrintGCDateStamps
    -XX:+UseGCLogFileRotation
    -XX:NumberOfGCLogFiles=5
    -XX:GCLogFileSize=10M
    
    # 内存溢出处理
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:HeapDumpPath=/app/logs/
    -XX:OnOutOfMemoryError="kill -9 %p"
    
    # 性能监控
    -XX:+UnlockDiagnosticVMOptions
    -XX:+LogVMOutput
    -XX:+PrintFlagsFinal
    -XX:+PrintCompilation
    
    # 其他优化
    -XX:+UseStringDeduplication
    -XX:+OptimizeStringConcat
    -XX:+UseCompressedOops
    -XX:+UseCompressedClassPointers
```

### 9.2 数据库优化

#### 9.2.1 MySQL配置优化
```yaml
# mysql-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: mysql-config
  namespace: auth-center
data:
  my.cnf: |
    [mysqld]
    # 基础配置
    server-id = 1
    port = 3306
    default-storage-engine = InnoDB
    character-set-server = utf8mb4
    collation-server = utf8mb4_unicode_ci
    
    # 内存配置
    innodb_buffer_pool_size = 1G
    innodb_buffer_pool_instances = 1
    innodb_log_file_size = 256M
    innodb_log_buffer_size = 16M
    key_buffer_size = 32M
    
    # 连接配置
    max_connections = 1000
    max_connect_errors = 10000
    wait_timeout = 28800
    interactive_timeout = 28800
    
    # 查询缓存
    query_cache_type = 1
    query_cache_size = 64M
    query_cache_limit = 2M
    
    # 慢查询日志
    slow_query_log = 1
    slow_query_log_file = /var/log/mysql/slow.log
    long_query_time = 2
    
    # 二进制日志
    log-bin = mysql-bin
    binlog_format = ROW
    binlog_cache_size = 2M
    max_binlog_cache_size = 8M
    max_binlog_size = 512M
    
    # InnoDB配置
    innodb_file_per_table = 1
    innodb_flush_log_at_trx_commit = 2
    innodb_lock_wait_timeout = 50
    innodb_rollback_on_timeout = 1
    innodb_deadlock_detect = 1
    
    # 安全配置
    local-infile = 0
    skip-show-database = 1
```

## 🔍 故障排查

### 10.1 常见故障处理

#### 10.1.1 服务启动失败
```bash
# 检查Pod状态
kubectl get pods -n auth-center -o wide

# 查看Pod详细信息
kubectl describe pod <pod-name> -n auth-center

# 查看Pod日志
kubectl logs <pod-name> -n auth-center --tail=100

# 进入Pod调试
kubectl exec -it <pod-name> -n auth-center -- /bin/bash

# 检查资源使用情况
kubectl top pods -n auth-center
kubectl top nodes
```

#### 10.1.2 数据库连接问题
```bash
# 检查数据库连接
kubectl exec -it mysql-0 -n auth-center -- mysql -u root -p

# 检查数据库状态
kubectl exec -it mysql-0 -n auth-center -- mysql -u root -p -e "SHOW PROCESSLIST;"

# 检查慢查询
kubectl exec -it mysql-0 -n auth-center -- mysql -u root -p -e "SHOW VARIABLES LIKE 'slow_query_log';"

# 检查主从同步状态
kubectl exec -it mysql-0 -n auth-center -- mysql -u root -p -e "SHOW SLAVE STATUS\G;"
```

#### 10.1.3 网络连接问题
```bash
# 检查服务发现
kubectl get svc -n auth-center
kubectl get endpoints -n auth-center

# 测试服务连通性
kubectl exec -it <pod-name> -n auth-center -- nslookup auth-server.auth-center

# 检查网络策略
kubectl get networkpolicy -n auth-center

# 测试端口连通性
kubectl exec -it <pod-name> -n auth-center -- telnet auth-server.auth-center 8001
```

### 10.2 性能问题排查

#### 10.2.1 内存问题排查
```bash
# 查看JVM内存使用
kubectl exec -it <pod-name> -n auth-center -- jstat -gc 1

# 生成堆转储
kubectl exec -it <pod-name> -n auth-center -- jmap -dump:live,format=b,file=/tmp/heap.hprof 1

# 分析GC日志
kubectl logs <pod-name> -n auth-center | grep "GC"

# 查看内存使用趋势
kubectl exec -it <pod-name> -n auth-center -- cat /proc/meminfo
```

#### 10.2.2 CPU问题排查
```bash
# 查看CPU使用情况
kubectl top pods -n auth-center

# 查看线程堆栈
kubectl exec -it <pod-name> -n auth-center -- jstack 1 > stack.txt

# 查看系统负载
kubectl exec -it <pod-name> -n auth-center -- uptime

# 查看进程信息
kubectl exec -it <pod-name> -n auth-center -- ps aux
```

### 10.3 监控指标说明

#### 10.3.1 关键指标阈值
| 指标 | 正常范围 | 警告阈值 | 严重阈值 |
|------|----------|----------|----------|
| CPU使用率 | < 70% | 70-85% | > 85% |
| 内存使用率 | < 80% | 80-90% | > 90% |
| 磁盘使用率 | < 80% | 80-90% | > 90% |
| 响应时间 | < 200ms | 200-500ms | > 500ms |
| 错误率 | < 1% | 1-5% | > 5% |
| 连接池使用率 | < 80% | 80-90% | > 90% |

#### 10.3.2 告警处理流程
1. **接收告警** - 通过邮件、短信、钉钉等方式接收
2. **确认告警** - 检查告警详情和影响范围
3. **定位问题** - 查看日志、监控指标、系统状态
4. **解决问题** - 采取相应措施恢复服务
5. **根因分析** - 分析问题原因，制定预防措施
6. **记录文档** - 记录处理过程和解决方案

---

💡 **提示**: 定期进行故障演练和性能测试，确保系统的稳定性和可靠性。建立完善的监控告警体系，及时发现和解决问题。