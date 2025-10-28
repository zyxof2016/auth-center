# API响应格式和异常处理指南

## 1. 响应格式规范

### 1.1 基础响应类 (Response)
用于无数据返回的简单操作，如新增、删除、修改等。

**示例代码：**
```java
// 成功响应
return Response.buildSuccess();

// 失败响应
return Response.buildFailure("A0201", "用户不存在");

// 使用错误码枚举
return Response.buildFailure(ErrorCode.USER_NOT_EXIST);
```

**响应格式：**
```json
{
    "success": true,
    "errCode": null,
    "errMessage": null,
    "timestamp": 1640995200000
}
```

### 1.2 单条数据响应 (SingleResponse)
用于返回单条数据的查询操作。

**示例代码：**
```java
// 成功响应
UserDTO user = userService.getUserById(1L);
return SingleResponse.of(user);

// 失败响应
return SingleResponse.buildFailure(ErrorCode.USER_NOT_EXIST);
```

**响应格式：**
```json
{
    "success": true,
    "errCode": null,
    "errMessage": null,
    "timestamp": 1640995200000,
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "管理员"
    }
}
```

### 1.3 多条数据响应 (MultiResponse)
用于返回多条数据的查询操作。

**示例代码：**
```java
// 成功响应
List<UserDTO> users = userService.listUsers();
return MultiResponse.of(users);

// 带总数响应
return MultiResponse.of(users, users.size());
```

**响应格式：**
```json
{
    "success": true,
    "errCode": null,
    "errMessage": null,
    "timestamp": 1640995200000,
    "data": [
        {
            "id": 1,
            "username": "admin",
            "realName": "管理员"
        }
    ],
    "total": 1
}
```

### 1.4 分页数据响应 (PageResponse)
用于分页查询操作。

**示例代码：**
```java
// 分页查询
PageResult<UserDTO> pageResult = userService.pageUsers(pageNum, pageSize);
return PageResponse.of(
    pageResult.getData(), 
    pageResult.getPageNum(), 
    pageResult.getPageSize(), 
    pageResult.getTotal()
);

// 空分页响应
return PageResponse.empty(pageNum, pageSize);
```

**响应格式：**
```json
{
    "success": true,
    "errCode": null,
    "errMessage": null,
    "timestamp": 1640995200000,
    "data": [
        {
            "id": 1,
            "username": "admin",
            "realName": "管理员"
        }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "total": 100,
    "pages": 10
}
```

## 2. 错误码规范

### 2.1 错误码结构
错误码采用5位编码格式：
- 第1位：错误级别（A-用户端错误，B-系统级错误，C-第三方服务错误）
- 第2-3位：业务模块
- 第4-5位：具体错误

### 2.2 常用错误码

#### 系统级错误码
```java
ErrorCode.SUCCESS           // 成功
ErrorCode.SYSTEM_ERROR     // 系统执行出错
ErrorCode.SYSTEM_TIMEOUT   // 系统执行超时
ErrorCode.SYSTEM_FLOW_LIMIT // 系统限流
```

#### 用户端错误码
```java
ErrorCode.USER_ERROR                 // 用户端错误
ErrorCode.USER_NOT_EXIST            // 用户不存在
ErrorCode.USERNAME_OR_PASSWORD_ERROR // 用户名或密码错误
ErrorCode.USER_UNAUTHORIZED        // 用户未授权
```

#### 认证授权错误码
```java
ErrorCode.AUTH_ERROR                // 认证授权错误
ErrorCode.AUTH_TOKEN_EXPIRED       // 令牌已过期
ErrorCode.AUTH_TOKEN_INVALID       // 令牌无效
ErrorCode.AUTH_FORBIDDEN           // 禁止访问
```

## 3. 异常处理规范

### 3.1 业务异常 (BusinessException)
用于处理业务逻辑相关的异常。

**示例代码：**
```java
// 抛出业务异常
if (user == null) {
    throw new BusinessException(ErrorCode.USER_NOT_EXIST);
}

// 使用静态方法
throw BusinessException.of(ErrorCode.USER_NOT_EXIST);

// 自定义错误信息
throw BusinessException.of(ErrorCode.USER_NOT_EXIST, "用户ID: " + userId + " 不存在");
```

### 3.2 参数校验异常
使用Spring Validation进行参数校验。

**DTO类示例：**
```java
@Data
public class UserCreateDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

**Controller示例：**
```java
@PostMapping("/users")
public Response createUser(@Valid @RequestBody UserCreateDTO userDTO) {
    userService.createUser(userDTO);
    return Response.buildSuccess();
}
```

**校验失败响应：**
```json
{
    "success": false,
    "errCode": "A0330",
    "errMessage": "用户名不能为空, 密码长度必须在6-20个字符之间",
    "timestamp": 1640995200000
}
```

### 3.3 全局异常处理
系统会自动捕获并处理以下异常：

- `BusinessException`：业务异常
- `MethodArgumentNotValidException`：参数校验异常
- `BindException`：参数绑定异常
- `ConstraintViolationException`：参数校验异常
- `NoHandlerFoundException`：404异常
- `NullPointerException`：空指针异常
- `Exception`：其他所有异常

## 4. 最佳实践

### 4.1 Controller层规范
```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 查询用户列表
     */
    @GetMapping
    public MultiResponse<UserDTO> listUsers() {
        List<UserDTO> users = userService.listUsers();
        return MultiResponse.of(users);
    }
    
    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return SingleResponse.of(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Response createUser(@Valid @RequestBody UserCreateDTO userDTO) {
        userService.createUser(userDTO);
        return Response.buildSuccess();
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Response updateUser(@PathVariable Long id, 
                              @Valid @RequestBody UserUpdateDTO userDTO) {
        userService.updateUser(id, userDTO);
        return Response.buildSuccess();
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Response deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Response.buildSuccess();
    }
}
```

### 4.2 Service层规范
```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.of(ErrorCode.USER_NOT_EXIST);
        }
        return UserConvertor.toDTO(user);
    }
    
    @Override
    public void createUser(UserCreateDTO userDTO) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw BusinessException.of(ErrorCode.USER_ALREADY_EXIST);
        }
        
        // 创建用户
        User user = UserConvertor.toEntity(userDTO);
        userMapper.insert(user);
        
        log.info("用户创建成功: {}", userDTO.getUsername());
    }
}
```

### 4.3 统一日志记录
```java
@Aspect
@Component
@Slf4j
public class LogAspect {
    
    @Around("execution(* com.auth.center..controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            log.info("接口调用成功: {} - 耗时: {}ms", 
                    joinPoint.getSignature().getName(), 
                    endTime - startTime);
            
            return result;
        } catch (BusinessException e) {
            long endTime = System.currentTimeMillis();
            log.warn("业务异常: {} - {} - 耗时: {}ms", 
                    e.getErrCode(), e.getErrMessage(), 
                    endTime - startTime);
            throw e;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("系统异常: {} - 耗时: {}ms", 
                    joinPoint.getSignature().getName(), 
                    endTime - startTime, e);
            throw e;
        }
    }
}
```

## 5. 前端对接规范

### 5.1 响应处理
```javascript
// 统一响应拦截器
axios.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.success) {
            return res;
        } else {
            // 业务错误
            Message.error(res.errMessage || '系统错误');
            return Promise.reject(new Error(res.errMessage || 'Error'));
        }
    },
    error => {
        // 网络错误或系统错误
        Message.error('网络错误或系统异常');
        return Promise.reject(error);
    }
);
```

### 5.2 错误码映射
```javascript
// 错误码映射表
const errorCodeMap = {
    'A0201': '用户不存在',
    'A0210': '用户名或密码错误',
    'A1003': '登录已过期，请重新登录',
    'A1004': '令牌无效，请重新登录',
    'B0001': '系统错误，请联系管理员'
};

// 获取错误信息
function getErrorMessage(code, defaultMessage) {
    return errorCodeMap[code] || defaultMessage || '系统错误';
}
```

通过这套响应格式和异常处理体系，可以确保API接口的规范性、一致性和可维护性。