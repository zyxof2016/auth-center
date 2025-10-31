package com.auth.center.auth.interfaces.controller;

import com.auth.center.auth.application.service.LoginStrategyFactory;
import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.ThirdPartyAuthService;
import com.auth.center.auth.domain.service.UserAuthService;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.dto.MultiResponse;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final LoginStrategyFactory loginStrategyFactory;
    private final ThirdPartyAuthService thirdPartyAuthService;
    private final UserAuthService userAuthService;
    
    /**
     * 密码登录接口
     */
    @PostMapping("/login/password")
    public SingleResponse<LoginResult> loginByPassword(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        // 简化登录逻辑，暂时使用默认策略
        LoginType loginType = LoginType.USERNAME_PASSWORD;
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            loginType = LoginType.PHONE_PASSWORD;
        } else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            loginType = LoginType.EMAIL_PASSWORD;
        }
        
        com.auth.center.auth.domain.service.LoginStrategy strategy = loginStrategyFactory.getStrategy(loginType);
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 验证码登录接口
     */
    @PostMapping("/login/code")
    public SingleResponse<LoginResult> loginByCode(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        com.auth.center.auth.domain.service.LoginStrategy strategy = loginStrategyFactory.getStrategy(LoginType.PHONE_CODE);
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 第三方登录接口
     */
    @PostMapping("/login/third")
    public SingleResponse<LoginResult> loginByThirdParty(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        com.auth.center.auth.domain.service.LoginStrategy strategy = loginStrategyFactory.getStrategy(LoginType.THIRD_PARTY);
        LoginResult result = strategy.login(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 发送登录验证码
     */
    @PostMapping("/login/send-code")
    public Response sendLoginCode(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        // 验证参数
        if (request.getReceiver() == null || request.getReceiver().trim().isEmpty()) {
            throw new IllegalArgumentException("接收者不能为空");
        }
        
        // 检查发送频率
        if (!userAuthService.checkSendFrequency(request.getReceiver(), "LOGIN", 60)) {
            throw new IllegalArgumentException("发送验证码过于频繁，请稍后再试");
        }
        
        // 生成验证码
        String code = userAuthService.generateCode(request.getTenantId(), request.getReceiver(), "LOGIN", 5);
        
        // 这里应该调用实际的短信或邮件发送服务
        // 暂时只记录日志
        System.out.println("发送验证码: " + code + " 到 " + request.getReceiver());
        
        return Response.buildSuccess();
    }
    
    /**
     * 获取可用的第三方登录方式列表
     */
    @GetMapping("/third/providers")
    public MultiResponse<Object> getAvailableProviders() {
        // 简化日志记录
        
        // 暂时返回模拟数据
        List<Object> providerList = new ArrayList<>();
        Map<String, Object> providerInfo = new HashMap<>();
        providerInfo.put("type", "WECHAT");
        providerInfo.put("name", "微信登录");
        providerInfo.put("enabled", true);
        providerInfo.put("configValid", true);
        providerList.add(providerInfo);
        
        return MultiResponse.of(providerList);
    }
    
    /**
     * 获取第三方登录授权地址
     */
    @GetMapping("/third/authorize-url")
    public SingleResponse<Map<String, String>> getAuthorizeUrl(@RequestParam String thirdType, 
                                  @RequestParam(required = false) String redirectUri,
                                  @RequestParam(required = false) String state) {
        // 简化日志记录
        
        // 使用默认回调地址或配置的回调地址
        String finalRedirectUri = redirectUri != null ? redirectUri : getDefaultRedirectUri();
        String finalState = state != null ? state : generateState();
        
        // 生成授权地址
        String authorizeUrl = thirdPartyAuthService.getAuthorizeUrl(thirdType, finalRedirectUri, finalState);
        
        Map<String, String> result = new HashMap<>();
        result.put("authorizeUrl", authorizeUrl);
        result.put("thirdType", thirdType);
        result.put("state", finalState);
        return SingleResponse.of(result);
    }
    
    /**
     * 第三方登录回调处理
     */
    @PostMapping("/third/callback")
    public SingleResponse<LoginResult> thirdPartyCallback(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        // 处理第三方登录回调
        LoginResult result = handleThirdPartyCallback(request);
        return SingleResponse.of(result);
    }
    
    /**
     * 绑定第三方账号
     */
    @PostMapping("/third/bind")
    public Response bindThirdPartyAccount(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        // 实现第三方账号绑定逻辑
        bindThirdParty(request);
        return Response.buildSuccess();
    }
    
    /**
     * 解绑第三方账号
     */
    @DeleteMapping("/third/unbind")
    public Response unbindThirdPartyAccount(@RequestBody LoginRequest request) {
        // 简化日志记录
        
        // 实现第三方账号解绑逻辑
        unbindThirdParty(request);
        return Response.buildSuccess();
    }
    
    /**
     * 获取用户绑定的第三方账号列表
     */
    @GetMapping("/third/bindings")
    public MultiResponse<Object> getThirdPartyBindings() {
        // 简化日志记录
        
        // 实现获取绑定列表逻辑
        List<Object> bindings = getThirdPartyBindingsList();
        return MultiResponse.of(bindings);
    }
    
    /**
     * 获取第三方登录统计信息
     */
    @GetMapping("/third/statistics")
    public SingleResponse<Object> getThirdPartyStatistics() {
        // 简化日志记录
        
        // 获取统计信息
        Object statistics = thirdPartyAuthService.getStatistics();
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
        return thirdPartyAuthService.handleCallback(request.getThirdType(), request.getAuthCode(), request.getState());
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
    
    private List<Object> buildProvidersResponse(List<com.auth.center.auth.domain.service.ThirdPartyProvider> providers) {
        // 构建第三方登录提供者响应数据
        List<Object> providerList = new ArrayList<>();
        
        for (com.auth.center.auth.domain.service.ThirdPartyProvider provider : providers) {
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