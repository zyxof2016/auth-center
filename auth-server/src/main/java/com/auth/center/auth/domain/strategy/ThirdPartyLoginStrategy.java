package com.auth.center.auth.domain.strategy;

import com.auth.center.auth.domain.dto.LoginRequest;
import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.auth.domain.dto.UserInfoDTO;
import com.auth.center.auth.domain.enums.LoginType;
import com.auth.center.auth.domain.service.LoginStrategy;
import com.auth.center.auth.domain.service.ThirdPartyProvider;
import com.auth.center.auth.domain.service.ThirdPartyUser;
import com.auth.center.auth.domain.service.UserAuthService;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.CommonErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录策略
 */
@Component
public class ThirdPartyLoginStrategy implements LoginStrategy {
    
    @Autowired
    private UserAuthService userAuthService;
    
    @Autowired
    private com.auth.center.auth.infrastructure.util.JwtUtil jwtUtil;
    
    private final Map<String, ThirdPartyProvider> providers = new HashMap<>();
    
    public ThirdPartyLoginStrategy(java.util.List<ThirdPartyProvider> providerList) {
        for (ThirdPartyProvider provider : providerList) {
            providers.put(provider.getProviderType(), provider);
        }
    }
    
    @Override
    public LoginResult login(LoginRequest request) {
        // 验证参数
        if (request.getThirdType() == null || request.getThirdType().trim().isEmpty()) {
            throw new BusinessException(CommonErrorCode.REQUIRED_PARAM_MISSING, "第三方登录类型不能为空");
        }
        
        if (request.getAuthCode() == null || request.getAuthCode().trim().isEmpty()) {
            throw new BusinessException(CommonErrorCode.REQUIRED_PARAM_MISSING, "授权码不能为空");
        }
        
        // 获取第三方登录提供者
        ThirdPartyProvider provider = providers.get(request.getThirdType().toUpperCase());
        if (provider == null || !provider.isEnabled()) {
            throw new BusinessException(CommonErrorCode.PARAM_FORMAT_ERROR, "不支持的第三方登录类型或已禁用");
        }
        
        // 处理第三方回调
        ThirdPartyUser thirdPartyUser = provider.handleCallback(request.getAuthCode(), request.getState());
        if (thirdPartyUser == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST, "第三方登录失败");
        }
        
        // 检查该第三方用户是否已经绑定了系统用户
        UserInfoDTO user = findUserByThirdPartyId(request.getTenantId(), request.getThirdType(), thirdPartyUser.getThirdId());
        
        if (user == null) {
            // 如果没有绑定，创建新用户或提示绑定
            // 这里简化处理，直接创建新用户
            user = createUserFromThirdParty(thirdPartyUser, request.getTenantId());
        }
        
        // 检查用户状态
        userAuthService.checkUserStatus(user);
        
        // 创建登录结果
        return createLoginResult(user, request.getRememberMe());
    }
    
    @Override
    public String getLoginType() {
        return LoginType.THIRD_PARTY.getCode();
    }
    
    @Override
    public boolean isAvailable() {
        return !providers.isEmpty();
    }
    
    /**
     * 根据第三方ID查找用户
     */
    private UserInfoDTO findUserByThirdPartyId(Long tenantId, String thirdType, String thirdId) {
        // 这里应该通过第三方用户表查找对应的系统用户
        // 暂时返回null
        return null;
    }
    
    /**
     * 从第三方用户信息创建系统用户
     */
    private UserInfoDTO createUserFromThirdParty(ThirdPartyUser thirdPartyUser, Long tenantId) {
        // 这里应该创建新用户
        // 暂时返回null
        return null;
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