package com.auth.center.auth.domain.service;

/**
 * 第三方登录服务接口
 */
public interface ThirdPartyAuthService {
    
    /**
     * 获取第三方登录授权地址
     *
     * @param thirdType 第三方类型
     * @param redirectUri 回调地址
     * @param state 状态参数
     * @return 授权地址
     */
    String getAuthorizeUrl(String thirdType, String redirectUri, String state);
    
    /**
     * 处理第三方登录回调
     *
     * @param thirdType 第三方类型
     * @param authCode 授权码
     * @param state 状态参数
     * @return 登录结果
     */
    com.auth.center.auth.domain.dto.LoginResult handleCallback(String thirdType, String authCode, String state);
    
    /**
     * 绑定第三方账号
     *
     * @param thirdType 第三方类型
     * @param userId 用户ID
     * @param authCode 授权码
     */
    void bindAccount(String thirdType, Long userId, String authCode);
    
    /**
     * 解绑第三方账号
     *
     * @param thirdType 第三方类型
     * @param userId 用户ID
     */
    void unbindAccount(String thirdType, Long userId);
    
    /**
     * 获取用户绑定的第三方账号列表
     *
     * @param userId 用户ID
     * @return 第三方账号列表
     */
    java.util.List<Object> getBindings(Long userId);
    
    /**
     * 获取第三方登录统计信息
     *
     * @return 统计信息
     */
    Object getStatistics();
}