package com.auth.center.user.infrastructure.persistence;

import com.auth.center.user.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户持久化映射器
 */
@Mapper
public interface UserMapper {
    
    /**
     * 插入用户
     */
    void insert(UserPO userPO);
    
    /**
     * 根据ID查询用户
     */
    UserPO selectById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    UserPO selectByUsername(@Param("tenantId") Long tenantId, @Param("username") String username);
    
    /**
     * 根据用户名统计数量
     */
    long countByUsername(@Param("tenantId") Long tenantId, @Param("username") String username);
    
    /**
     * 根据邮箱统计数量
     */
    long countByEmail(@Param("tenantId") Long tenantId, @Param("email") String email);
    
    /**
     * 根据手机号统计数量
     */
    long countByPhone(@Param("tenantId") Long tenantId, @Param("phone") String phone);
    
    /**
     * 根据条件统计用户数量
     */
    long countByConditions(@Param("tenantId") Long tenantId, 
                          @Param("username") String username,
                          @Param("realName") String realName,
                          @Param("status") Integer status);
    
    /**
     * 根据条件查询用户列表
     */
    List<UserPO> selectByConditions(@Param("tenantId") Long tenantId,
                                   @Param("username") String username,
                                   @Param("realName") String realName,
                                   @Param("status") Integer status,
                                   @Param("offset") long offset,
                                   @Param("limit") int limit);
    
    /**
     * 根据ID删除用户
     */
    void deleteById(Long id);
    
    /**
     * 根据ID更新用户
     */
    void updateById(UserPO userPO);
}