package com.auth.center.auth.domain.service;

import com.auth.center.auth.domain.dto.UserInfoDTO;
import com.auth.center.auth.domain.entity.VerificationCode;
import com.auth.center.auth.domain.enums.UserStatus;
import com.auth.center.auth.domain.repository.VerificationCodeRepository;
import com.auth.center.auth.infrastructure.client.UserServiceClient;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.CommonErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 用户认证服务类
 */
@Service
public class UserAuthService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    
    /**
     * 根据用户名查找用户
     */
    public UserInfoDTO getUserByUsername(Long tenantId, String username) {
        SingleResponse<UserInfoDTO> response = userServiceClient.getUserByUsername(tenantId, username);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    /**
     * 根据邮箱查找用户
     */
    public UserInfoDTO getUserByEmail(Long tenantId, String email) {
        SingleResponse<UserInfoDTO> response = userServiceClient.getUserByEmail(tenantId, email);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    /**
     * 根据手机号查找用户
     */
    public UserInfoDTO getUserByPhone(Long tenantId, String phone) {
        SingleResponse<UserInfoDTO> response = userServiceClient.getUserByPhone(tenantId, phone);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    /**
     * 验证用户密码
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 检查用户状态
     */
    public void checkUserStatus(UserInfoDTO user) {
        if (user.getStatus() == UserStatus.DISABLED.getCode()) {
            throw new BusinessException(CommonErrorCode.USER_ACCOUNT_INVALID, "用户账户已作废");
        }
        
        if (user.getStatus() == UserStatus.LOCKED.getCode()) {
            throw new BusinessException(CommonErrorCode.USER_ACCOUNT_LOCKED, "用户账户被锁定");
        }
    }
    
    /**
     * 检查并锁定用户（如果失败次数过多）
     */
    public void checkAndLockUser(UserInfoDTO user, int maxFailCount) {
        if (user.getLoginFailCount() != null && user.getLoginFailCount() >= maxFailCount) {
            // 这里应该调用用户服务的锁定用户接口
            // 暂时只抛出异常
            throw new BusinessException(CommonErrorCode.USER_ACCOUNT_LOCKED, "用户账户已被锁定");
        }
    }
    
    /**
     * 生成验证码
     *
     * @param tenantId 租户ID
     * @param receiver 接收者（手机号/邮箱）
     * @param codeType 验证码类型
     * @param expireMinutes 过期分钟数
     * @return 生成的验证码
     */
    public String generateCode(Long tenantId, String receiver, String codeType, int expireMinutes) {
        // 生成6位随机数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // 创建验证码实体
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTenantId(tenantId);
        verificationCode.setReceiver(receiver);
        verificationCode.setCodeType(codeType);
        verificationCode.setCode(code);
        verificationCode.setUsed(false);
        verificationCode.setSendStatus(1); // 发送成功
        verificationCode.setSendTime(LocalDateTime.now());
        verificationCode.setCreatedTime(LocalDateTime.now());
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
        
        // 保存到数据库
        verificationCodeRepository.save(verificationCode);
        
        return code;
    }
    
    /**
     * 验证验证码
     *
     * @param receiver 接收者（手机号/邮箱）
     * @param codeType 验证码类型
     * @param code 验证码
     * @return 验证结果
     */
    public boolean verifyCode(String receiver, String codeType, String code) {
        // 查找验证码
        VerificationCode verificationCode = verificationCodeRepository
                .findByReceiverAndCodeTypeAndCode(receiver, codeType, code)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CAPTCHA_INVALID, "验证码无效"));
        
        // 检查验证码是否过期
        if (verificationCode.isExpired()) {
            throw new BusinessException(CommonErrorCode.CAPTCHA_EXPIRED, "验证码已过期");
        }
        
        // 检查验证码是否已使用
        if (verificationCode.getUsed() != null && verificationCode.getUsed()) {
            throw new BusinessException(CommonErrorCode.CAPTCHA_INVALID, "验证码已使用");
        }
        
        // 标记为已使用
        verificationCode.markAsUsed();
        verificationCodeRepository.save(verificationCode);
        
        return true;
    }
    
    /**
     * 检查发送频率限制
     *
     * @param receiver 接收者（手机号/邮箱）
     * @param codeType 验证码类型
     * @param intervalSeconds 间隔秒数
     * @return 是否允许发送
     */
    public boolean checkSendFrequency(String receiver, String codeType, int intervalSeconds) {
        return verificationCodeRepository
                .findByReceiverAndCodeType(receiver, codeType)
                .map(code -> {
                    // 检查距离上次发送的时间间隔
                    LocalDateTime lastSendTime = code.getSendTime();
                    if (lastSendTime != null) {
                        return LocalDateTime.now().isAfter(lastSendTime.plusSeconds(intervalSeconds));
                    }
                    return true;
                })
                .orElse(true); // 如果没有记录，允许发送
    }
}