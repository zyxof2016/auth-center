package com.auth.center.user.domain.service;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.repository.UserRepository;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务
 */
@Service
@RequiredArgsConstructor
public class UserDomainService {
    
    private final UserRepository userRepository;
    
    /**
     * 创建用户
     *
     * @param user 用户实体
     * @return 创建后的用户
     */
    public User createUser(User user) {
        // 验证用户信息
        user.validate();
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getTenantId(), user.getUsername())) {
            throw new BusinessException(CommonErrorCode.USER_ALREADY_EXIST);
        }
        
        // 设置创建时间
        user.setCreatedTime(java.time.LocalDateTime.now());
        user.setUpdatedTime(java.time.LocalDateTime.now());
        
        // 保存用户
        return userRepository.save(user);
    }
    
    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名和租户ID获取用户
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @return 用户实体
     */
    public User getUserByUsername(Long tenantId, String username) {
        return userRepository.findByUsername(tenantId, username);
    }
    
    /**
     * 分页查询用户
     *
     * @param tenantId 租户ID
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param status 用户状态（可选）
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    public Page<User> getUserPage(Long tenantId, String username, String realName, 
                                 UserStatus status, Pageable pageable) {
        return userRepository.findByConditions(tenantId, username, realName, status, pageable);
    }
    
    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 更新后的用户
     */
    public User updateUserInfo(User user) {
        user.setUpdatedTime(java.time.LocalDateTime.now());
        return userRepository.update(user);
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}