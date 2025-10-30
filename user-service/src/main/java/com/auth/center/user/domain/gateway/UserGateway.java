package com.auth.center.user.domain.gateway;

import com.auth.center.user.domain.entity.User;

/**
 * 用户网关接口（防腐层）
 */
public interface UserGateway {
    
    /**
     * 保存用户
     *
     * @param user 用户实体
     * @return 保存后的用户
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    User findById(Long id);
    
    /**
     * 根据租户ID和用户名查找用户
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(Long tenantId, String username);
    
    /**
     * 检查用户名是否存在
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(Long tenantId, String username);
    
    /**
     * 更新用户
     *
     * @param user 用户实体
     * @return 更新后的用户
     */
    User update(User user);
    
    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     */
    void deleteById(Long id);
}