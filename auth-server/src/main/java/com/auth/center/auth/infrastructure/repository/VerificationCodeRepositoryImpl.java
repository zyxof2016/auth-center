package com.auth.center.auth.infrastructure.repository;

import com.auth.center.auth.domain.entity.VerificationCode;
import com.auth.center.auth.domain.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 验证码仓库实现类
 */
@Repository
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final RowMapper<VerificationCode> rowMapper = new VerificationCodeRowMapper();
    
    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        if (verificationCode.getId() == null) {
            // 插入新记录
            String sql = "INSERT INTO sys_verification_code (tenant_id, receiver, code_type, code, biz_id, expire_time, used, used_time, ip_address, send_status, send_time, created_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    verificationCode.getTenantId(),
                    verificationCode.getReceiver(),
                    verificationCode.getCodeType(),
                    verificationCode.getCode(),
                    verificationCode.getBizId(),
                    verificationCode.getExpireTime(),
                    verificationCode.getUsed() != null && verificationCode.getUsed() ? 1 : 0,
                    verificationCode.getUsedTime(),
                    verificationCode.getIpAddress(),
                    verificationCode.getSendStatus(),
                    verificationCode.getSendTime(),
                    verificationCode.getCreatedTime() != null ? verificationCode.getCreatedTime() : LocalDateTime.now());
            
            // 获取生成的ID
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            verificationCode.setId(id);
        } else {
            // 更新现有记录
            String sql = "UPDATE sys_verification_code SET tenant_id = ?, receiver = ?, code_type = ?, code = ?, biz_id = ?, expire_time = ?, used = ?, used_time = ?, ip_address = ?, send_status = ?, send_time = ?, created_time = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    verificationCode.getTenantId(),
                    verificationCode.getReceiver(),
                    verificationCode.getCodeType(),
                    verificationCode.getCode(),
                    verificationCode.getBizId(),
                    verificationCode.getExpireTime(),
                    verificationCode.getUsed() != null && verificationCode.getUsed() ? 1 : 0,
                    verificationCode.getUsedTime(),
                    verificationCode.getIpAddress(),
                    verificationCode.getSendStatus(),
                    verificationCode.getSendTime(),
                    verificationCode.getCreatedTime(),
                    verificationCode.getId());
        }
        return verificationCode;
    }
    
    @Override
    public Optional<VerificationCode> findByReceiverAndCodeType(String receiver, String codeType) {
        String sql = "SELECT * FROM sys_verification_code WHERE receiver = ? AND code_type = ? ORDER BY created_time DESC LIMIT 1";
        try {
            VerificationCode code = jdbcTemplate.queryForObject(sql, rowMapper, receiver, codeType);
            return Optional.ofNullable(code);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<VerificationCode> findById(Long id) {
        String sql = "SELECT * FROM sys_verification_code WHERE id = ?";
        try {
            VerificationCode code = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(code);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<VerificationCode> findByReceiverAndCodeTypeAndCode(String receiver, String codeType, String code) {
        String sql = "SELECT * FROM sys_verification_code WHERE receiver = ? AND code_type = ? AND code = ? ORDER BY created_time DESC LIMIT 1";
        try {
            VerificationCode verificationCode = jdbcTemplate.queryForObject(sql, rowMapper, receiver, codeType, code);
            return Optional.ofNullable(verificationCode);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * 验证码行映射器
     */
    private static class VerificationCodeRowMapper implements RowMapper<VerificationCode> {
        @Override
        public VerificationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
            VerificationCode code = new VerificationCode();
            code.setId(rs.getLong("id"));
            code.setTenantId(rs.getLong("tenant_id"));
            code.setReceiver(rs.getString("receiver"));
            code.setCodeType(rs.getString("code_type"));
            code.setCode(rs.getString("code"));
            code.setBizId(rs.getString("biz_id"));
            code.setExpireTime(rs.getTimestamp("expire_time") != null ? rs.getTimestamp("expire_time").toLocalDateTime() : null);
            code.setUsed(rs.getInt("used") == 1);
            code.setUsedTime(rs.getTimestamp("used_time") != null ? rs.getTimestamp("used_time").toLocalDateTime() : null);
            code.setIpAddress(rs.getString("ip_address"));
            code.setSendStatus(rs.getInt("send_status"));
            code.setSendTime(rs.getTimestamp("send_time") != null ? rs.getTimestamp("send_time").toLocalDateTime() : null);
            code.setCreatedTime(rs.getTimestamp("created_time") != null ? rs.getTimestamp("created_time").toLocalDateTime() : null);
            return code;
        }
    }
}