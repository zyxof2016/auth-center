package com.auth.center.role.infrastructure.repository.mapper;

import com.auth.center.role.domain.entity.RoleMenuEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色菜单关联Mapper接口
 */
@Mapper
public interface RoleMenuMapper {
    
    /**
     * 插入角色菜单关联
     */
    @Insert("INSERT INTO sys_role_menu(tenant_id, role_id, menu_id, created_by, created_time) " +
            "VALUES(#{tenantId}, #{roleId}, #{menuId}, #{createdBy}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RoleMenuEntity roleMenuEntity);
    
    /**
     * 根据ID更新角色菜单关联
     */
    @Update("UPDATE sys_role_menu SET updated_by = #{updatedBy}, updated_time = #{updatedTime} WHERE id = #{id}")
    void updateById(RoleMenuEntity roleMenuEntity);
    
    /**
     * 根据角色ID删除关联
     */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);
    
    /**
     * 根据角色ID查询菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
    
    /**
     * 根据角色ID查询关联列表
     */
    @Select("SELECT id, tenant_id, role_id, menu_id, created_by, created_time FROM sys_role_menu WHERE role_id = #{roleId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "menuId", column = "menu_id"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time")
    })
    List<RoleMenuEntity> selectByRoleId(Long roleId);
    
    /**
     * 批量插入角色菜单关联
     */
    void batchInsert(@Param("roleMenuList") List<RoleMenuEntity> roleMenuList);
    
    /**
     * 根据角色ID和菜单ID列表删除关联
     */
    @Delete("<script>" +
            "DELETE FROM sys_role_menu WHERE role_id = #{roleId} AND menu_id IN " +
            "<foreach collection='menuIds' item='menuId' open='(' separator=',' close=')'>" +
            "#{menuId}" +
            "</foreach>" +
            "</script>")
    void deleteByRoleIdAndMenuIds(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}