package com.auth.center.auth.application.controller;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import com.auth.center.auth.domain.service.LoginStrategyFactory;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.dto.MultiResponse;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final LoginStrategyFactory loginStrategyFactory;
    private final ThirdPartyAuthService thirdPartyAuthService;
    
    /**
     * 密码登录接口
     */
    @PostMapping("/login/password")
    public SingleResponse<LoginResult> loginByPassword(@RequestBody LoginRequest request) {
        log.info("密码登录请求: {}", request.getLoginType());
        
        // 根据登录类型选择策略
        LoginStrategy strategy;
        switch (request.getLoginType()) {
            case USERNAME:
                strategy = loginStrategyFactory.getStrategy(LoginType.USERNAME);
                break;
            case PHONE_PASSWORD:
                strategy = loginStrategyFactory.getStrategy(LoginType.PHONE_PASSWORD);
                break;
            case EMAIL_PASSWORD:
                strategy = loginStrategyFactory.getStrategy(LoginType.EMAIL_PASSWORD);
                break;
            default:
                throw new IllegalArgumentException("不支持的密码登录类型: " + request.getLoginType());
        }
        
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 验证码登录接口
     */
    @PostMapping("/login/code")
    public SingleResponse<LoginResult> loginByCode(@RequestBody LoginRequest request) {
        log.info("验证码登录请求: {}", request.getPhone());
        
        LoginStrategy strategy = loginStrategyFactory.getStrategy(LoginType.PHONE_CODE);
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 第三方登录接口
     */
    @PostMapping("/login/third")
    public SingleResponse<LoginResult> loginByThirdParty(@RequestBody LoginRequest request) {
        log.info("第三方登录请求: {}", request.getThirdType());
        
        LoginStrategy strategy = loginStrategyFactory.getStrategy(LoginType.THIRD_PARTY);
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 发送登录验证码
     */
    @PostMapping("/login/send-code")
    public Response sendLoginCode(@RequestBody LoginRequest request) {
        log.info("发送登录验证码: {}", request.getReceiver());
        
        // 实现发送验证码逻辑
        sendVerificationCode(request.getReceiver(), "LOGIN");
        return Response.buildSuccess();
    }
    
    /**
     * 获取可用的第三方登录方式列表
     */
    @GetMapping("/third/providers")
    public MultiResponse<Object> getAvailableProviders() {
        log.info("获取可用的第三方登录方式列表");
        
        // 获取可用的第三方登录提供者
        var providers = thirdPartyAuthService.getAvailableProviders();
        
        // 构建响应数据
        List<Object> providerList = buildProvidersResponse(providers);
        return MultiResponse.of(providerList);
    }
    
    /**
     * 获取第三方登录授权地址
     */
    @GetMapping("/third/authorize-url")
    public SingleResponse<Map<String, String>> getAuthorizeUrl(@RequestParam String thirdType, 
                                  @RequestParam(required = false) String redirectUri,
                                  @RequestParam(required = false) String state) {
        log.info("获取第三方授权地址: {}", thirdType);
        
        // 使用默认回调地址或配置的回调地址
        String finalRedirectUri = redirectUri != null ? redirectUri : getDefaultRedirectUri();
        String finalState = state != null ? state : generateState();
        
        // 生成授权地址
        String authorizeUrl = thirdPartyAuthService.getAuthorizeUrl(thirdType, finalRedirectUri, finalState);
        
        Map<String, String> result = Map.of(
            "authorizeUrl", authorizeUrl,
            "thirdType", thirdType,
            "state", finalState
        );
        return SingleResponse.of(result);
    }
    
    /**
     * 第三方登录回调处理
     */
    @PostMapping("/third/callback")
    public SingleResponse<LoginResult> thirdPartyCallback(@RequestBody LoginRequest request) {
        log.info("第三方登录回调: {}", request.getThirdType());
        
        // 处理第三方登录回调
        LoginResult result = handleThirdPartyCallback(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 绑定第三方账号
     */
    @PostMapping("/third/bind")
    public Response bindThirdPartyAccount(@RequestBody LoginRequest request) {
        log.info("绑定第三方账号: {}", request.getThirdType());
        
        // 实现第三方账号绑定逻辑
        bindThirdParty(request);
        return Response.buildSuccess();
    }
    
    /**
     * 解绑第三方账号
     */
    @DeleteMapping("/third/unbind")
    public Response unbindThirdPartyAccount(@RequestBody LoginRequest request) {
        log.info("解绑第三方账号: {}", request.getThirdType());
        
        // 实现第三方账号解绑逻辑
        unbindThirdParty(request);
        return Response.buildSuccess();
    }
    
    /**
     * 获取用户绑定的第三方账号列表
     */
    @GetMapping("/third/bindings")
    public MultiResponse<Object> getThirdPartyBindings() {
        log.info("获取第三方账号绑定列表");
        
        // 实现获取绑定列表逻辑
        List<Object> bindings = getThirdPartyBindingsList();
        return MultiResponse.of(bindings);
    }
    
    /**
     * 获取第三方登录统计信息
     */
    @GetMapping("/third/statistics")
    public SingleResponse<Object> getThirdPartyStatistics() {
        log.info("获取第三方登录统计信息");
        
        Object statistics = thirdPartyAuthService.getProviderStatistics();
        return SingleResponse.of(statistics);
    }
    
    // 以下为辅助方法，需要具体实现
    
    private void sendVerificationCode(String receiver, String codeType) {
        // 实现发送验证码逻辑
        // 无返回值
    }
    
    private LoginResult handleThirdPartyCallback(LoginRequest request) {
        // 处理第三方登录回调
        // 返回登录结果
        return new LoginResult();
    }
    
    private void bindThirdParty(LoginRequest request) {
        // 实现第三方账号绑定逻辑
        // 无返回值
    }
    
    private void unbindThirdParty(LoginRequest request) {
        // 实现第三方账号解绑逻辑
        // 无返回值
    }
    
    private List<Object> getThirdPartyBindingsList() {
        // 实现获取绑定列表逻辑
        // 返回绑定列表
        return new ArrayList<>();
    }
    
    // 插拔式第三方登录相关辅助方法
    
    private List<Object> buildProvidersResponse(List<ThirdPartyProvider> providers) {
        // 构建第三方登录提供者响应数据
        List<Object> providerList = new ArrayList<>();
        
        for (ThirdPartyProvider provider : providers) {
            Map<String, Object> providerInfo = new HashMap<>();
            providerInfo.put("type", provider.getProviderType());
            providerInfo.put("name", provider.getProviderName());
            providerInfo.put("enabled", provider.isEnabled());
            providerInfo.put("configValid", provider.validateConfig());
            
            // 可以添加更多信息，如图标URL等
            providerList.add(providerInfo);
        }
        
        return providerList;
    }
    
    private String getDefaultRedirectUri() {
        // 从配置中获取默认回调地址
        return "http://localhost:8080/auth/callback";
    }
    
    private String generateState() {
        // 生成随机的state参数，防止CSRF攻击
        return "state_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}