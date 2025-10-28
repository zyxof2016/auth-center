package com.auth.center.auth.domain.service;

/**
 * 第三方认证服务接口
 */
public interface ThirdPartyAuthService {
    
    /**
     * 获取第三方登录授权地址
     * @param thirdType 第三方类型
     * @param redirectUri 回调地址
     * @param state 状态参数
     * @return 授权地址
     */
    String getAuthorizeUrl(String thirdType, String redirectUri, String state);
    
    /**
     * 处理第三方登录回调
     * @param thirdType 第三方类型
     * @param code 授权码
     * @param state 状态参数
     * @return 用户信息
     */
    Object handleCallback(String thirdType, String code, String state);
    
    /**
     * 绑定第三方账号
     * @param userId 用户ID
     * @param thirdType 第三方类型
     * @param code 授权码
     * @param state 状态参数
     * @return 绑定结果
     */
    boolean bindThirdPartyAccount(Long userId, String thirdType, String code, String state);
    
    /**
     * 解绑第三方账号
     * @param userId 用户ID
     * @param thirdType 第三方类型
     * @return 解绑结果
     */
    boolean unbindThirdPartyAccount(Long userId, String thirdType);
    
    /**
     * 获取用户绑定的第三方账号列表
     * @param userId 用户ID
     * @return 绑定列表
     */
    Object getThirdPartyBindings(Long userId);
}