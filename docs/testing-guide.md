# 测试指南

## 📋 目录

- [测试概述](#测试概述)
- [测试环境搭建](#测试环境搭建)
- [单元测试](#单元测试)
- [集成测试](#集成测试)
- [API测试](#api测试)
- [性能测试](#性能测试)
- [安全测试](#安全测试)
- [测试报告](#测试报告)

## 🎯 测试概述

### 测试策略

本项目采用多层次测试策略，确保代码质量和系统稳定性：

1. **单元测试** - 测试单个方法和类的功能
2. **集成测试** - 测试模块间的交互
3. **API测试** - 测试REST接口的功能
4. **性能测试** - 测试系统性能指标
5. **安全测试** - 测试系统安全性

### 测试工具栈

| 测试类型 | 工具 | 用途 |
|----------|------|------|
| 单元测试 | JUnit 5 + Mockito | 单元测试框架 |
| 集成测试 | Spring Boot Test | 集成测试支持 |
| API测试 | Postman + RestAssured | API接口测试 |
| 性能测试 | JMeter + Gatling | 性能压力测试 |
| 代码覆盖率 | JaCoCo | 测试覆盖率统计 |
| 安全测试 | OWASP ZAP | 安全漏洞扫描 |

## 🛠️ 测试环境搭建

### 1. 测试数据库配置

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  redis:
    host: localhost
    port: 6379
    database: 1
```

### 2. 测试依赖配置

```xml
<dependencies>
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2内存数据库 -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- 测试覆盖率 -->
    <dependency>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.8</version>
    </dependency>
</dependencies>
```

### 3. 测试配置类

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
    }
    
    @Bean
    @Primary
    public RedisTemplate<String, Object> testRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
```

## 🧪 单元测试

### 1. Service层测试

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("创建用户 - 成功")
    void shouldCreateUserSuccessfully() {
        // Given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
        
        User savedUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .build();
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserDTO result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("创建用户 - 用户名已存在")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("existinguser")
                .build();
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名已存在");
        
        verify(userRepository).existsByUsername("existinguser");
        verifyNoMoreInteractions(userRepository);
    }
}
```

### 2. Controller层测试

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    @DisplayName("获取用户列表 - 成功")
    void shouldGetUserListSuccessfully() throws Exception {
        // Given
        UserDTO user = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        
        PageResponse<UserDTO> pageResponse = PageResponse.of(
                Collections.singletonList(user), 1, 10, 1, 1);
        
        when(userService.getUserPage(any(), anyInt(), anyInt()))
                .thenReturn(pageResponse);
        
        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.total").value(1));
        
        verify(userService).getUserPage(any(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("创建用户 - 成功")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .build();
        
        UserDTO createdUser = UserDTO.builder()
                .id(1L)
                .username("newuser")
                .email("new@example.com")
                .build();
        
        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(createdUser);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
        
        verify(userService).createUser(any(UserCreateRequest.class));
    }
}
```

### 3. Repository层测试

```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("根据用户名查找用户 - 存在")
    void shouldFindUserByUsernameWhenExists() {
        // Given
        User user = User.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> result = userRepository.findByUsername("testuser");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("根据用户名查找用户 - 不存在")
    void shouldReturnEmptyWhenUsernameNotExists() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");
        
        // Then
        assertThat(result).isEmpty();
    }
}
```

## 🔗 集成测试

### 1. 数据库集成测试

```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class UserIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("用户创建完整流程测试")
    void shouldCreateUserWithCompleteFlow() {
        // Given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("integrationuser")
                .password("password123")
                .email("integration@example.com")
                .build();
        
        // When
        UserDTO createdUser = userService.createUser(request);
        
        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        
        // 验证数据库中的数据
        Optional<User> savedUser = userRepository.findById(createdUser.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo("integrationuser");
    }
    
    @Test
    @DisplayName("用户更新完整流程测试")
    void shouldUpdateUserWithCompleteFlow() {
        // Given - 先创建用户
        UserCreateRequest createRequest = UserCreateRequest.builder()
                .username("updateuser")
                .password("password123")
                .email("update@example.com")
                .build();
        
        UserDTO createdUser = userService.createUser(createRequest);
        
        // When - 更新用户
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("updated@example.com")
                .realName("Updated Name")
                .build();
        
        UserDTO updatedUser = userService.updateUser(createdUser.getId(), updateRequest);
        
        // Then
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getRealName()).isEqualTo("Updated Name");
        
        // 验证数据库中的数据
        Optional<User> savedUser = userRepository.findById(createdUser.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("updated@example.com");
    }
}
```

### 2. Redis集成测试

```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class RedisIntegrationTest {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CacheService cacheService;
    
    @Test
    @DisplayName("Redis缓存操作测试")
    void shouldPerformCacheOperations() {
        // Given
        String key = "test:user:1";
        UserDTO user = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .build();
        
        // When - 设置缓存
        cacheService.set(key, user, 3600);
        
        // Then - 验证缓存存在
        UserDTO cachedUser = cacheService.get(key, UserDTO.class);
        assertThat(cachedUser).isNotNull();
        assertThat(cachedUser.getUsername()).isEqualTo("testuser");
        
        // When - 删除缓存
        cacheService.delete(key);
        
        // Then - 验证缓存已删除
        UserDTO deletedUser = cacheService.get(key, UserDTO.class);
        assertThat(deletedUser).isNull();
    }
}
```

### 3. 消息队列集成测试

```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageQueueIntegrationTest {
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    @MockBean
    private NotificationService notificationService;
    
    @Test
    @DisplayName("消息队列发送接收测试")
    void shouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        String topic = "test-topic";
        MessageDTO message = MessageDTO.builder()
                .id("msg-123")
                .content("Test message")
                .build();
        
        // When - 发送消息
        rocketMQTemplate.syncSend(topic, message);
        
        // 等待消息处理
        Thread.sleep(1000);
        
        // Then - 验证消息被处理
        verify(notificationService, timeout(2000)).processMessage(any(MessageDTO.class));
    }
}
```

## 🌐 API测试

### 1. 使用RestAssured进行API测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("用户登录API测试")
    void testUserLoginApi() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .loginType(LoginType.USERNAME_PASSWORD)
                .username("admin")
                .password("admin123")
                .build();
        
        // When & Then
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/auth/login/password")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.accessToken", notNullValue())
                .body("data.userInfo.username", equalTo("admin"));
    }
    
    @Test
    @DisplayName("获取用户信息API测试")
    void testGetUserInfoApi() {
        // Given - 先登录获取token
        String token = loginAndGetToken();
        
        // When & Then
        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/auth/userinfo")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.username", equalTo("admin"));
    }
    
    private String loginAndGetToken() {
        LoginRequest request = LoginRequest.builder()
                .loginType(LoginType.USERNAME_PASSWORD)
                .username("admin")
                .password("admin123")
                .build();
        
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/auth/login/password");
        
        return response.jsonPath().getString("data.accessToken");
    }
}
```

### 2. Postman集合

创建Postman测试集合文件 `Auth Center.postman_collection.json`：

```json
{
  "info": {
    "name": "Auth Center API",
    "description": "认证中心API测试集合"
  },
  "item": [
    {
      "name": "认证相关",
      "item": [
        {
          "name": "用户登录",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"loginType\": \"USERNAME_PASSWORD\",\n  \"username\": \"admin\",\n  \"password\": \"admin123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login/password",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login", "password"]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Status code is 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Response has token\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData.data.accessToken).to.be.a('string');",
                  "    pm.globals.set(\"authToken\", jsonData.data.accessToken);",
                  "});"
                ]
              }
            }
          ]
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    }
  ]
}
```

## ⚡ 性能测试

### 1. JMeter性能测试

创建JMeter测试计划 `Auth Center Performance Test.jmx`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.5">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="认证中心性能测试" enabled="true">
      <stringProp name="TestPlan.comments">认证中心API性能测试</stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.tearDown_on_shutdown">true</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="用户定义的变量" enabled="true">
        <collectionProp name="Arguments.arguments">
          <elementProp name="BASE_URL" elementType="Argument">
            <stringProp name="Argument.name">BASE_URL</stringProp>
            <stringProp name="Argument.value">http://localhost:8080</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="用户登录测试" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="循环控制器" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">100</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">50</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="用户登录" enabled="true">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="用户定义的变量" enabled="true">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <boolProp name="HTTPArgument.always_encode">true</boolProp>
                <stringProp name="Argument.value">{"loginType":"USERNAME_PASSWORD","username":"admin","password":"admin123"}</stringProp>
                <stringProp name="Argument.metadata">=</stringProp>
              </elementProp>
            </collectionProp>
          </elementProp>
          <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
          <stringProp name="HTTPSampler.port"></stringProp>
          <stringProp name="HTTPSampler.protocol"></stringProp>
          <stringProp name="HTTPSampler.contentEncoding">UTF-8</stringProp>
          <stringProp name="HTTPSampler.path">/api/auth/login/password</stringProp>
          <stringProp name="HTTPSampler.method">POST</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
          <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
          <stringProp name="HTTPSampler.connect_timeout"></stringProp>
          <stringProp name="HTTPSampler.response_timeout"></stringProp>
        </HTTPSamplerProxy>
        <hashTree/>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

### 2. Gatling性能测试

```scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AuthCenterPerformanceTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val scn = scenario("用户登录性能测试")
    .exec(
      http("用户登录")
        .post("/api/auth/login/password")
        .body(StringBody("""{"loginType":"USERNAME_PASSWORD","username":"admin","password":"admin123"}"""))
        .check(status.is(200))
        .check(jsonPath("$.data.accessToken").saveAs("token"))
    )
    .pause(1)
    .exec(
      http("获取用户信息")
        .get("/api/auth/userinfo")
        .header("Authorization", "Bearer ${token}")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsersPerSec(10) to(100) during(30 seconds),
      constantUsersPerSec(100) during(60 seconds)
    )
  ).protocols(httpProtocol)
}
```

## 🔒 安全测试

### 1. OWASP ZAP安全扫描

```bash
# 启动ZAP代理
docker run -u zap -p 8080:8080 -i owasp/zap2docker-stable zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.addrs.addr.name=.* -config api.addrs.addr.regex=true

# 执行安全扫描
docker run --network host -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8080
```

### 2. 安全测试用例

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {
    
    @LocalServerPort
    private int port;
    
    @Test
    @DisplayName("SQL注入防护测试")
    void testSqlInjectionProtection() {
        String maliciousInput = "admin'; DROP TABLE sys_user; --";
        
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "loginType", "USERNAME_PASSWORD",
                    "username", maliciousInput,
                    "password", "password"
                ))
        .when()
                .post("/api/auth/login/password")
        .then()
                .statusCode(400) // 应该返回400错误，而不是500
                .body("success", equalTo(false));
    }
    
    @Test
    @DisplayName("XSS防护测试")
    void testXssProtection() {
        String xssPayload = "<script>alert('XSS')</script>";
        
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(Map.of(
                    "username", xssPayload,
                    "password", "password"
                ))
        .when()
                .post("/api/auth/login/password")
        .then()
                .statusCode(400);
    }
    
    @Test
    @DisplayName("未授权访问测试")
    void testUnauthorizedAccess() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/users")
        .then()
                .statusCode(401); // 应该返回401未授权
    }
}
```

## 📊 测试报告

### 1. JaCoCo代码覆盖率

配置Maven插件：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

生成测试报告：

```bash
mvn clean test jacoco:report
```

### 2. Allure测试报告

添加依赖：

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.20.1</version>
    <scope>test</scope>
</dependency>
```

生成报告：

```bash
mvn clean test
allure serve target/allure-results
```

### 3. 测试报告模板

创建测试报告模板 `test-report-template.md`：

```markdown
# 测试报告

## 测试概览

- **测试时间**: {{testDate}}
- **测试版本**: {{version}}
- **测试环境**: {{environment}}
- **测试人员**: {{tester}}

## 测试结果

| 测试类型 | 总数 | 通过 | 失败 | 跳过 | 覆盖率 |
|----------|------|------|------|------|--------|
| 单元测试 | {{unitTests.total}} | {{unitTests.passed}} | {{unitTests.failed}} | {{unitTests.skipped}} | {{unitTests.coverage}}% |
| 集成测试 | {{integrationTests.total}} | {{integrationTests.passed}} | {{integrationTests.failed}} | {{integrationTests.skipped}} | {{integrationTests.coverage}}% |
| API测试 | {{apiTests.total}} | {{apiTests.passed}} | {{apiTests.failed}} | {{apiTests.skipped}} | - |
| 性能测试 | {{performanceTests.total}} | {{performanceTests.passed}} | {{performanceTests.failed}} | {{performanceTests.skipped}} | - |

## 测试详情

### 单元测试

{{#unitTests.details}}
- **{{name}}**: {{status}} ({{duration}}ms)
{{/unitTests.details}}

### 集成测试

{{#integrationTests.details}}
- **{{name}}**: {{status}} ({{duration}}ms)
{{/integrationTests.details}}

### API测试

{{#apiTests.details}}
- **{{name}}**: {{status}} ({{responseTime}}ms)
{{/apiTests.details}}

### 性能测试

{{#performanceTests.details}}
- **{{name}}**: 
  - TPS: {{tps}}
  - 平均响应时间: {{avgResponseTime}}ms
  - 95%响应时间: {{p95ResponseTime}}ms
{{/performanceTests.details}}

## 问题汇总

{{#issues}}
- **{{title}}**: {{description}} (严重程度: {{severity}})
{{/issues}}

## 结论

{{conclusion}}
```

## 🚀 持续集成

### 1. GitHub Actions配置

```yaml
name: Test Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: auth_center_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
      
      redis:
        image: redis:7.0
        ports:
          - 6379:6379
        options: --health-cmd="redis-cli ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Run tests
      run: mvn clean test
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml
        flags: unittests
        name: codecov-umbrella
    
    - name: Run integration tests
      run: mvn verify -P integration-tests
    
    - name: Run API tests
      run: mvn verify -P api-tests
    
    - name: Run security tests
      run: mvn verify -P security-tests
```

### 2. 测试脚本

创建测试脚本 `run-tests.sh`：

```bash
#!/bin/bash

echo "开始执行测试..."

# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 等待服务启动
sleep 30

# 运行单元测试
echo "执行单元测试..."
mvn clean test

# 运行集成测试
echo "执行集成测试..."
mvn verify -P integration-tests

# 运行API测试
echo "执行API测试..."
mvn verify -P api-tests

# 运行安全测试
echo "执行安全测试..."
mvn verify -P security-tests

# 生成测试报告
echo "生成测试报告..."
mvn jacoco:report
allure generate target/allure-results -o target/allure-report

# 清理测试环境
docker-compose -f docker-compose.test.yml down

echo "测试完成！"
```

---

💡 **提示**: 定期运行测试并生成报告，确保代码质量和系统稳定性。建议在每次代码提交后自动运行测试套件。