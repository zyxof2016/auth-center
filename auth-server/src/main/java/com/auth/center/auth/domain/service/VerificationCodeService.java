package com.auth.center.auth.domain.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    
    /**
     * 发送验证码
     * @param receiver 接收者（手机号/邮箱）
     * @param codeType 验证码类型
     * @param ipAddress 请求IP
     * @return 业务ID
     */
    String sendCode(String receiver, String codeType, String ipAddress);
    
    /**
     * 验证验证码
     * @param receiver 接收者
     * @param code 验证码
     * @param codeType 验证码类型
     * @param bizId 业务ID
     * @return 是否验证成功
     */
    boolean verifyCode(String receiver, String code, String codeType, String bizId);
    
    /**
     * 标记验证码为已使用
     * @param bizId 业务ID
     */
    void markCodeAsUsed(String bizId);
    
    /**
     * 检查验证码发送频率
     * @param receiver 接收者
     * @param ipAddress 请求IP
     * @return 是否可以发送
     */
    boolean canSendCode(String receiver, String ipAddress);
}