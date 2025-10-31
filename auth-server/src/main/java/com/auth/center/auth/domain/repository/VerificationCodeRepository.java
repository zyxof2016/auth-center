package com.auth.center.auth.domain.repository;

import com.auth.center.auth.domain.entity.VerificationCode;
import java.util.Optional;

/**
 * 验证码仓库接口
 */
public interface VerificationCodeRepository {
    
    /**
     * 保存验证码
     */
    VerificationCode save(VerificationCode verificationCode);
    
    /**
     * 根据接收者和验证码类型查找最新的验证码
     */
    Optional<VerificationCode> findByReceiverAndCodeType(String receiver, String codeType);
    
    /**
     * 根据ID查找验证码
     */
    Optional<VerificationCode> findById(Long id);
    
    /**
     * 根据接收者、验证码类型和验证码值查找
     */
    Optional<VerificationCode> findByReceiverAndCodeTypeAndCode(String receiver, String codeType, String code);
}