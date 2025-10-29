package com.auth.center.auth.domain.service.impl;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.web.feign.LogServiceClient;
import com.auth.center.common.web.feign.UserServiceClient;
import com.auth.center.log.application.dto.LoginLogDTO;
import com.auth.center.user.application.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱密码登录策略
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailPasswordLoginStrategy implements LoginStrategy {
    
    private final UserServiceClient userServiceClient;
    private final LogServiceClient logServiceClient;
    
    @Override
    public LoginType getLoginType() {
        return LoginType.EMAIL_PASSWORD;
    }
    
    @Override
    public LoginResult login(LoginRequest request) {
        log.info("执行邮箱密码登录: {}", request.getEmail());
        
        // 1. 验证邮箱和密码
        validateEmailPassword(request);
        
        // 2. 查询用户信息（使用Feign客户端调用user-service）
        SingleResponse<UserDTO> userResponse = userServiceClient.getUserByEmail(1L, request.getEmail());
        if (!userResponse.isSuccess() || userResponse.getData() == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        UserDTO user = userResponse.getData();
        
        // 3. 验证用户密码
        SingleResponse<Boolean> passwordResponse = userServiceClient.validatePassword(user.getId(), request.getPassword());
        if (!passwordResponse.isSuccess() || !passwordResponse.getData()) {
            throw new IllegalArgumentException("密码错误");
        }
        
        // 4. 验证用户状态
        validateUserStatus(user);
        
        // 5. 生成令牌
        var tokens = generateTokens(user);
        
        // 6. 记录登录日志（使用Feign客户端调用log-service）
        recordLoginLog(user, LoginType.EMAIL_PASSWORD, request.getClientIp());
        
        // 7. 更新用户登录信息
        userServiceClient.updateLoginInfo(user.getId(), request.getClientIp());
        
        return buildLoginResult(tokens, user);
    }
    
    private void validateEmailPassword(LoginRequest request) {
        // 实现邮箱格式验证
        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        // 具体的密码验证逻辑
    }
    
    private boolean isValidEmail(String email) {
        // 简单的邮箱格式验证
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void validateUserStatus(UserDTO user) {
        // 实现用户状态验证逻辑
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalArgumentException("用户状态异常，无法登录");
        }
    }
    
    private Object generateTokens(UserDTO user) {
        // 实现令牌生成逻辑
        // 返回包含accessToken和refreshToken的对象
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(user));
        tokens.put("refreshToken", generateRefreshToken(user));
        return tokens;
    }
    
    private void recordLoginLog(UserDTO user, LoginType loginType, String clientIp) {
        // 实现登录日志记录逻辑
        try {
            // 创建登录日志DTO
            LoginLogDTO loginLog = new LoginLogDTO();
            loginLog.setUserId(user.getId());
            loginLog.setUsername(user.getUsername());
            loginLog.setLoginType(loginType.name());
            loginLog.setLoginIp(clientIp);
            loginLog.setLoginTime(new Date());
            loginLog.setStatus("SUCCESS");
            
            // 调用日志服务记录登录日志
            logServiceClient.recordLoginLog(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage());
            // 记录日志失败不影响主流程
        }
    }
    
    private LoginResult buildLoginResult(Object tokens, UserDTO user) {
        // 构建登录结果
        var result = new LoginResult();
        result.setSuccess(true);
        result.setAccessToken(((Map<String, String>) tokens).get("accessToken"));
        result.setRefreshToken(((Map<String, String>) tokens).get("refreshToken"));
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setLoginTime(new Date());
        return result;
    }
    
    private String generateAccessToken(UserDTO user) {
        // 生成访问令牌的逻辑
        return "access_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
    
    private String generateRefreshToken(UserDTO user) {
        // 生成刷新令牌的逻辑
        return "refresh_token_" + user.getId() + "_" + System.currentTimeMillis();
    }
}