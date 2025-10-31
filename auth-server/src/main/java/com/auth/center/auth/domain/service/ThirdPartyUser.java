package com.auth.center.auth.domain.service;

import lombok.Data;

/**
 * 第三方用户信息
 */
@Data
public class ThirdPartyUser {
    
    /**
     * 第三方用户唯一标识
     */
    private String thirdId;
    
    /**
     * 第三方UnionID（微信专用）
     */
    private String thirdUnionId;
    
    /**
     * 第三方昵称
     */
    private String thirdNickname;
    
    /**
     * 第三方头像
     */
    private String thirdAvatar;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌过期时间
     */
    private Long expireTime;
    
    /**
     * 第三方类型
     */
    private String thirdType;
}