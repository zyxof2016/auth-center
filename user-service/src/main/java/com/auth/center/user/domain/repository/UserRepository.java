package com.auth.center.user.domain.repository;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户仓库接口
 */
public interface UserRepository {
    
    /**
     * 保存用户
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(Long tenantId, String username);
    
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
    Page<User> findByConditions(Long tenantId, String username, String realName, UserStatus status, Pageable pageable);
    
    /**
     * 删除用户
     */
    void deleteById(Long id);
    
    /**
     * 更新用户
     */
    User update(User user);
}