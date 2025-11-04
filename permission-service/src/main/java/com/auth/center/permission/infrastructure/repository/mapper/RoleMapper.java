package com.auth.center.role.infrastructure.repository.mapper;

import com.auth.center.role.domain.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
public interface RoleMapper extends BaseMapper<RoleEntity> {
    
    /**
     * 根据用户ID查询用户拥有的角色
     */
    @Select("SELECT r.* FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE r.tenant_id = #{tenantId} AND ur.user_id = #{userId} AND r.status = 1")
    List<RoleEntity> selectByUserId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);
}