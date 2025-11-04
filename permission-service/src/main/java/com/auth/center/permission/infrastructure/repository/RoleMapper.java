package com.auth.center.role.infrastructure.repository;

import com.auth.center.role.domain.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
public interface RoleMapper extends BaseMapper<RoleEntity> {
    
    /**
     * 根据用户ID查询用户拥有的角色
     */
    List<RoleEntity> selectByUserId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}