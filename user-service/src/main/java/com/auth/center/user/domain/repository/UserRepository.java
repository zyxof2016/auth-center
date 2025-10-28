package com.auth.center.user.domain.repository;

import com.auth.center.user.domain.entity.UserEntity;
import com.auth.center.user.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 用户仓库接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     */
    UserEntity save(UserEntity userEntity);
    
    /**
     * 根据ID查找用户
     */
    Optional<UserEntity> findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<UserEntity> findByUsername(Long tenantId, String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(Long tenantId, String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(Long tenantId, String email);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(Long tenantId, String phone);
    
    /**
     * 根据条件分页查询用户
     */
    Page<UserEntity> findByConditions(Long tenantId, String username, String realName, UserStatus status, Pageable pageable);
    
    /**
     * 删除用户
     */
    void delete(UserEntity userEntity);
}