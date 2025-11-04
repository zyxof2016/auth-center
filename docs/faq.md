# 常见问题解答 (FAQ)

## 📋 目录

- [环境搭建问题](#环境搭建问题)
- [开发调试问题](#开发调试问题)
- [部署运维问题](#部署运维问题)
- [功能使用问题](#功能使用问题)
- [性能优化问题](#性能优化问题)
- [安全问题](#安全问题)

## 🛠️ 环境搭建问题

### Q: 项目启动失败，提示端口被占用怎么办？

**A:** 按以下步骤排查和解决：

1. **查看端口占用情况**
   ```bash
   # Windows
   netstat -ano | findstr :8080
   
   # Linux/Mac
   netstat -tulpn | grep :8080
   ```

2. **终止占用进程**
   ```bash
   # Windows
   taskkill /PID <进程ID> /F
   
   # Linux/Mac
   kill -9 <进程ID>
   ```

3. **修改配置文件中的端口**
   ```yaml
   # auth-gateway/src/main/resources/application.yml
   server:
     port: 8081  # 修改为其他端口
   ```

### Q: 数据库连接失败怎么解决？

**A:** 检查以下几个方面：

1. **数据库服务状态**
   ```bash
   # 检查MySQL服务是否启动
   systemctl status mysql
   # 或
   service mysql status
   ```

2. **数据库连接参数**
   ```yaml
   # 检查application.yml中的配置
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/auth_center?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
       username: root
       password: your_password
   ```

3. **数据库权限**
   ```sql
   -- 创建用户并授权
   CREATE USER 'auth_user'@'%' IDENTIFIED BY 'Auth123456!';
   GRANT ALL PRIVILEGES ON auth_center.* TO 'auth_user'@'%';
   FLUSH PRIVILEGES;
   ```

4. **防火墙设置**
   ```bash
   # 检查MySQL端口是否被防火墙阻止
   sudo ufw status
   sudo ufw allow 3306
   ```

### Q: Nacos启动失败或连接不上怎么办？

**A:** 按以下步骤排查：

1. **检查Java环境**
   ```bash
   java -version
   # 确保Java版本为8+
   ```

2. **检查Nacos配置**
   ```bash
   # 查看Nacos配置文件
   cat nacos/conf/application.properties
   ```

3. **单机模式启动**
   ```bash
   cd nacos/bin
   # Linux/Mac
   ./startup.sh -m standalone
   # Windows
   startup.cmd -m standalone
   ```

4. **检查Nacos服务状态**
   ```bash
   curl http://localhost:8848/nacos/v1/ns/operator/servers
   ```

### Q: Redis连接失败怎么办？

**A:** 检查Redis配置和状态：

1. **检查Redis服务**
   ```bash
   redis-cli ping
   # 应该返回 PONG
   ```

2. **检查Redis配置**
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: your_password  # 如果设置了密码
       database: 0
   ```

3. **启动Redis服务**
   ```bash
   # 启动Redis
   redis-server
   
   # 或使用Docker
   docker run -d -p 6379:6379 redis:7.0-alpine
   ```

## 🐛 开发调试问题

### Q: 如何开启Debug模式？

**A:** 有几种方式开启Debug模式：

1. **修改日志级别**
   ```yaml
   # application.yml
   logging:
     level:
       com.auth.center: DEBUG
       org.springframework.security: DEBUG
       org.springframework.web: DEBUG
   ```

2. **JVM Debug参数**
   ```bash
   # 启动时添加Debug参数
   java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar
   ```

3. **IDEA远程调试**
   - 创建Remote JVM Debug配置
   - Host: localhost
   - Port: 5005

### Q: 如何查看接口调用日志？

**A:** 配置日志记录：

1. **配置请求日志**
   ```yaml
   logging:
     level:
       org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
   ```

2. **自定义日志过滤器**
   ```java
   @Component
   @Slf4j
   public class RequestLoggingFilter extends OncePerRequestFilter {
       @Override
       protected void doFilterInternal(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      FilterChain filterChain) {
           log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
           filterChain.doFilter(request, response);
           log.info("Response: {}", response.getStatus());
       }
   }
   ```

### Q: 单元测试中如何模拟数据库？

**A:** 使用测试配置和内存数据库：

1. **使用H2内存数据库**
   ```yaml
   # application-test.yml
   spring:
     datasource:
       url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
       driver-class-name: org.h2.Driver
       username: sa
       password: 
   ```

2. **使用@MockBean注解**
   ```java
   @SpringBootTest
   class UserServiceTest {
       @MockBean
       private UserRepository userRepository;
       
       @Test
       void testCreateUser() {
           // 测试逻辑
       }
   }
   ```

3. **使用Testcontainers**
   ```java
   @Testcontainers
   class UserRepositoryTest {
       @Container
       static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
       
       @Test
       void testDatabaseOperations() {
           // 使用真实的MySQL容器进行测试
       }
   }
   ```

### Q: 如何解决跨域问题？

**A:** 配置CORS支持：

1. **网关层配置**
   ```yaml
   spring:
     cloud:
       gateway:
         globalcors:
           cors-configurations:
             '[/**]':
               allowedOriginPatterns: "*"
               allowedMethods: ["GET", "POST", "PUT", "DELETE"]
               allowedHeaders: "*"
               allowCredentials: true
   ```

2. **使用@CrossOrigin注解**
   ```java
   @RestController
   @CrossOrigin(origins = "*")
   public class UserController {
       // 控制器方法
   }
   ```

3. **自定义CORS配置**
   ```java
   @Configuration
   public class CorsConfig {
       @Bean
       public CorsConfigurationSource corsConfigurationSource() {
           CorsConfiguration configuration = new CorsConfiguration();
           configuration.setAllowedOriginPatterns(Arrays.asList("*"));
           configuration.setAllowedMethods(Arrays.asList("*"));
           configuration.setAllowedHeaders(Arrays.asList("*"));
           configuration.setAllowCredentials(true);
           
           UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
           source.registerCorsConfiguration("/**", configuration);
           return source;
       }
   }
   ```

## 🚀 部署运维问题

### Q: Docker容器启动失败怎么办？

**A:** 按以下步骤排查：

1. **查看容器日志**
   ```bash
   docker logs <container_name>
   ```

2. **检查容器状态**
   ```bash
   docker ps -a
   ```

3. **进入容器调试**
   ```bash
   docker exec -it <container_name> /bin/bash
   ```

4. **检查网络连接**
   ```bash
   docker network ls
   docker network inspect <network_name>
   ```

### Q: Kubernetes Pod无法启动怎么办？

**A:** 排查Pod启动问题：

1. **查看Pod状态**
   ```bash
   kubectl get pods -n auth-center
   kubectl describe pod <pod_name> -n auth-center
   ```

2. **查看Pod日志**
   ```bash
   kubectl logs <pod_name> -n auth-center
   kubectl logs <pod_name> -n auth-center --previous  # 查看前一个版本的日志
   ```

3. **检查资源限制**
   ```bash
   kubectl describe node <node_name>
   ```

4. **检查配置映射**
   ```bash
   kubectl get configmap -n auth-center
   kubectl describe configmap <configmap_name> -n auth-center
   ```

### Q: 如何配置负载均衡？

**A:** 几种负载均衡配置方式：

1. **Nginx负载均衡**
   ```nginx
   upstream auth_servers {
       server 192.168.1.10:8080;
       server 192.168.1.11:8080;
       server 192.168.1.12:8080;
   }
   
   server {
       listen 80;
       location / {
           proxy_pass http://auth_servers;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

2. **Kubernetes Service**
   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: auth-gateway-service
   spec:
     selector:
       app: auth-gateway
     ports:
     - port: 80
       targetPort: 8080
     type: LoadBalancer
   ```

3. **Spring Cloud LoadBalancer**
   ```yaml
   spring:
     cloud:
       loadbalancer:
         ribbon:
           enabled: false
   ```

### Q: 如何进行健康检查？

**A:** 配置健康检查：

1. **Spring Boot Actuator**
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: "health,info,metrics"
     endpoint:
       health:
         show-details: always
   ```

2. **Kubernetes健康检查**
   ```yaml
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
   ```

## 🔧 功能使用问题

### Q: 如何配置第三方登录？

**A:** 配置第三方登录步骤：

1. **申请第三方应用**
   - 微信开放平台：https://open.weixin.qq.com/
   - QQ互联：https://connect.qq.com/
   - GitHub：https://github.com/settings/applications

2. **配置应用信息**
   ```yaml
   # application.yml
   third:
     wechat:
       app-id: your_wechat_app_id
       app-secret: your_wechat_app_secret
       redirect-uri: http://localhost:8080/callback/wechat
     qq:
       app-id: your_qq_app_id
       app-secret: your_qq_app_secret
       redirect-uri: http://localhost:8080/callback/qq
   ```

3. **配置回调地址**
   - 在第三方平台配置正确的回调地址
   - 确保回调地址与配置文件中的地址一致

### Q: 如何配置文件存储？

**A:** 配置文件存储服务：

1. **MinIO配置**
   ```yaml
   minio:
     endpoint: http://localhost:9000
     access-key: minioadmin
     secret-key: minioadmin
     bucket-name: auth-center
   ```

2. **阿里云OSS配置**
   ```yaml
   oss:
     endpoint: https://oss-cn-hangzhou.aliyuncs.com
     access-key-id: your_access_key_id
     access-key-secret: your_access_key_secret
     bucket-name: your_bucket_name
   ```

3. **AWS S3配置**
   ```yaml
   s3:
     region: us-east-1
     access-key: your_access_key
     secret-key: your_secret_key
     bucket-name: your_bucket_name
   ```

### Q: 如何配置邮件发送？

**A:** 配置邮件服务：

1. **SMTP配置**
   ```yaml
   spring:
     mail:
       host: smtp.example.com
       port: 587
       username: your_email@example.com
       password: your_password
       properties:
         mail:
           smtp:
             auth: true
             starttls:
               enable: true
   ```

2. **邮件模板配置**
   ```java
   @Service
   public class EmailService {
       @Autowired
       private JavaMailSender mailSender;
       
       public void sendEmail(String to, String subject, String content) {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setTo(to);
           message.setSubject(subject);
           message.setText(content);
           mailSender.send(message);
       }
   }
   ```

### Q: 如何配置短信发送？

**A:** 配置短信服务：

1. **阿里云短信配置**
   ```yaml
   aliyun:
     sms:
       access-key-id: your_access_key_id
       access-key-secret: your_access_key_secret
       sign-name: 认证中心
       template-code: SMS_123456789
   ```

2. **腾讯云短信配置**
   ```yaml
   tencent:
     sms:
       secret-id: your_secret_id
       secret-key: your_secret_key
       sign-name: 认证中心
       template-id: 123456
   ```

## ⚡ 性能优化问题

### Q: 接口响应慢怎么办？

**A:** 优化接口性能：

1. **添加缓存**
   ```java
   @Cacheable(value = "users", key = "#id")
   public UserDTO getUserById(Long id) {
       return userRepository.findById(id);
   }
   ```

2. **数据库查询优化**
   ```sql
   -- 添加索引
   CREATE INDEX idx_username ON sys_user(username);
   CREATE INDEX idx_email ON sys_user(email);
   
   -- 优化查询
   EXPLAIN SELECT * FROM sys_user WHERE username = 'admin';
   ```

3. **异步处理**
   ```java
   @Async
   public CompletableFuture<Void> sendNotificationAsync(NotificationDTO notification) {
       notificationService.send(notification);
       return CompletableFuture.completedFuture(null);
   }
   ```

### Q: 内存溢出怎么办？

**A:** 解决内存溢出问题：

1. **调整JVM参数**
   ```bash
   -Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
   ```

2. **分析内存使用**
   ```bash
   # 生成堆转储文件
   jmap -dump:live,format=b,file=heap.hprof <PID>
   
   # 分析堆转储文件
   jhat heap.hprof
   ```

3. **优化代码**
   ```java
   // 避免内存泄漏
   @PreDestroy
   public void cleanup() {
       // 清理资源
   }
   
   // 使用对象池
   private final ObjectPool<SomeObject> objectPool = new GenericObjectPool<>(new SomeObjectFactory());
   ```

### Q: 数据库连接池耗尽怎么办？

**A:** 优化数据库连接池：

1. **调整连接池配置**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 20
         minimum-idle: 5
         connection-timeout: 30000
         idle-timeout: 600000
         max-lifetime: 1800000
   ```

2. **监控连接池状态**
   ```java
   @Autowired
   private HikariDataSource dataSource;
   
   public void monitorConnectionPool() {
       HikariPoolMXBean poolProxy = dataSource.getHikariPoolMXBean();
       log.info("Active connections: {}", poolProxy.getActiveConnections());
       log.info("Idle connections: {}", poolProxy.getIdleConnections());
   }
   ```

## 🔒 安全问题

### Q: 如何防止SQL注入？

**A:** 防止SQL注入的措施：

1. **使用参数化查询**
   ```java
   // 使用MyBatis
   @Select("SELECT * FROM sys_user WHERE username = #{username}")
   User findByUsername(@Param("username") String username);
   
   // 使用JPA
   @Query("SELECT u FROM User u WHERE u.username = :username")
   User findByUsername(@Param("username") String username);
   ```

2. **输入验证**
   ```java
   @PostMapping("/users")
   public Response createUser(@Valid @RequestBody UserCreateRequest request) {
       // 使用@Valid进行参数验证
   }
   ```

3. **使用ORM框架**
   - 优先使用MyBatis、JPA等ORM框架
   - 避免直接拼接SQL语句

### Q: 如何防止XSS攻击？

**A:** 防止XSS攻击的措施：

1. **输入过滤**
   ```java
   public String sanitizeInput(String input) {
       return input.replaceAll("<script>", "")
                   .replaceAll("</script>", "")
                   .replaceAll("javascript:", "");
   }
   ```

2. **输出编码**
   ```java
   // 在模板引擎中自动编码
   <div th:text="${userInput}"></div>
   ```

3. **使用安全框架**
   ```xml
   <dependency>
       <groupId>org.owasp.antisamy</groupId>
       <artifactId>antisamy</artifactId>
   </dependency>
   ```

### Q: 如何配置HTTPS？

**A:** 配置HTTPS步骤：

1. **生成SSL证书**
   ```bash
   # 使用keytool生成证书
   keytool -genkeypair -alias auth-center -keyalg RSA -keysize 2048 -validity 365 -keystore auth-center.jks
   ```

2. **配置SSL**
   ```yaml
   server:
     port: 8443
     ssl:
       enabled: true
       key-store: classpath:auth-center.jks
       key-store-password: changeit
       key-alias: auth-center
   ```

3. **HTTP重定向到HTTPS**
   ```java
   @Bean
   public ServletWebServerFactory servletContainer() {
       TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
           @Override
           protected void postProcessContext(Context context) {
               SecurityConstraint securityConstraint = new SecurityConstraint();
               securityConstraint.setUserConstraint("CONFIDENTIAL");
               SecurityCollection collection = new SecurityCollection();
               collection.addPattern("/*");
               securityConstraint.addCollection(collection);
               context.addConstraint(securityConstraint);
           }
       };
       tomcat.addAdditionalTomcatConnectors(redirectConnector());
       return tomcat;
   }
   ```

---

💡 **提示**: 如果您遇到的问题不在本FAQ中，请提交Issue到项目仓库，我们会及时回复和解决。