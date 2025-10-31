package com.auth.center.auth.domain.strategy;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.dto.UserInfoDTO;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import com.auth.center.auth.domain.service.UserAuthService;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.CommonErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 手机号密码登录策略
 */
@Component
public class PhonePasswordLoginStrategy implements LoginStrategy {
    
    @Autowired
    private UserAuthService userAuthService;
    
    @Autowired
    private com.auth.center.auth.infrastructure.util.JwtUtil jwtUtil;
    
    @Override
    public LoginResult login(LoginRequest request) {
        // 验证参数
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BusinessException(CommonErrorCode.REQUIRED_PARAM_MISSING, "手机号不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException(CommonErrorCode.REQUIRED_PARAM_MISSING, "密码不能为空");
        }
        
        // 验证图形验证码（如果需要）
        if (request.getCaptcha() != null && !request.getCaptcha().trim().isEmpty()) {
            userAuthService.verifyCode(request.getCaptchaId(), "CAPTCHA", request.getCaptcha());
        }
        
        // 根据手机号查找用户
        UserInfoDTO user = userAuthService.getUserByPhone(request.getTenantId(), request.getPhone());
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USERNAME_OR_PASSWORD_ERROR, "手机号或密码错误");
        }
        
        // 检查用户状态
        userAuthService.checkUserStatus(user);
        
        // 验证密码
        if (!userAuthService.verifyPassword(request.getPassword(), user.getPassword())) {
            // 检查是否需要锁定账户
            userAuthService.checkAndLockUser(user, 5);
            
            throw new BusinessException(CommonErrorCode.USERNAME_OR_PASSWORD_ERROR, "手机号或密码错误");
        }
        
        // 创建登录结果
        return createLoginResult(user, request.getRememberMe());
    }
    
    @Override
    public String getLoginType() {
        return LoginType.PHONE_PASSWORD.getCode();
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    /**
     * 创建登录结果
     */
    private LoginResult createLoginResult(UserInfoDTO user, Boolean rememberMe) {
        LoginResult result = new LoginResult();
        result.setUserInfo(convertToUserInfo(user));
        
        // 生成访问令牌
        String accessToken = jwtUtil.generateToken(user.getUsername());
        result.setAccessToken(accessToken);
        
        // 生成刷新令牌（这里简化处理，实际应该生成不同的token）
        String refreshToken = jwtUtil.generateToken(user.getUsername());
        result.setRefreshToken(refreshToken);
        
        result.setTokenType("bearer");
        result.setExpiresIn(jwtUtil.getExpiration().intValue());
        result.setLoginTime(java.time.LocalDateTime.now());
        return result;
    }
    
    /**
     * 转换用户信息
     */
    private LoginResult.UserInfo convertToUserInfo(UserInfoDTO user) {
        LoginResult.UserInfo userInfo = new LoginResult.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserType(user.getUserType());
        // 这里应该从角色服务获取真实的角色和权限
        userInfo.setRoles(new String[]{"USER"});
        userInfo.setPermissions(new String[]{"read"});
        return userInfo;
    }
}