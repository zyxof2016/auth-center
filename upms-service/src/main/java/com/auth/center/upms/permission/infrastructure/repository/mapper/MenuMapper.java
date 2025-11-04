package com.auth.center.role.infrastructure.repository.mapper;

import com.auth.center.role.domain.entity.MenuEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface MenuMapper {
    
    /**
     * 插入菜单
     */
    @Insert("INSERT INTO sys_menu(tenant_id, menu_name, menu_code, parent_id, menu_type, path, component, permission, icon, sort, visible, status, description, created_by, created_time, updated_by, updated_time) " +
            "VALUES(#{tenantId}, #{menuName}, #{menuCode}, #{parentId}, #{menuType}, #{path}, #{component}, #{permission}, #{icon}, #{sort}, #{visible}, #{status}, #{description}, #{createdBy}, #{createdTime}, #{updatedBy}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MenuEntity menuEntity);
    
    /**
     * 根据ID更新菜单
     */
    @Update("UPDATE sys_menu SET menu_name = #{menuName}, menu_code = #{menuCode}, parent_id = #{parentId}, menu_type = #{menuType}, path = #{path}, component = #{component}, permission = #{permission}, icon = #{icon}, sort = #{sort}, visible = #{visible}, status = #{status}, description = #{description}, updated_by = #{updatedBy}, updated_time = #{updatedTime} WHERE id = #{id}")
    void updateById(MenuEntity menuEntity);
    
    /**
     * 根据ID查询菜单
     */
    @Select("SELECT id, tenant_id, menu_name, menu_code, parent_id, menu_type, path, component, permission, icon, sort, visible, status, description, created_by, created_time, updated_by, updated_time FROM sys_menu WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "menuName", column = "menu_name"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "menuType", column = "menu_type"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    MenuEntity selectById(Long id);
    
    /**
     * 根据菜单编码查询菜单
     */
    @Select("SELECT id, tenant_id, menu_name, menu_code, parent_id, menu_type, path, component, permission, icon, sort, visible, status, description, created_by, created_time, updated_by, updated_time FROM sys_menu WHERE tenant_id = #{tenantId} AND menu_code = #{menuCode}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "menuName", column = "menu_name"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "menuType", column = "menu_type"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    MenuEntity selectByMenuCode(Long tenantId, String menuCode);
    
    /**
     * 根据父菜单ID查询子菜单列表
     */
    @Select("SELECT id, tenant_id, menu_name, menu_code, parent_id, menu_type, path, component, permission, icon, sort, visible, status, description, created_by, created_time, updated_by, updated_time FROM sys_menu WHERE tenant_id = #{tenantId} AND parent_id = #{parentId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "menuName", column = "menu_name"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "menuType", column = "menu_type"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<MenuEntity> selectByParentId(Long tenantId, Long parentId);
    
    /**
     * 根据租户ID查询所有菜单
     */
    @Select("SELECT id, tenant_id, menu_name, menu_code, parent_id, menu_type, path, component, permission, icon, sort, visible, status, description, created_by, created_time, updated_by, updated_time FROM sys_menu WHERE tenant_id = #{tenantId} ORDER BY sort")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "menuName", column = "menu_name"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "menuType", column = "menu_type"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<MenuEntity> selectByTenantId(Long tenantId);
    
    /**
     * 根据租户ID和角色ID查询菜单列表
     */
    @Select("SELECT m.id, m.tenant_id, m.menu_name, m.menu_code, m.parent_id, m.menu_type, m.path, m.component, m.permission, m.icon, m.sort, m.visible, m.status, m.description, m.created_by, m.created_time, m.updated_by, m.updated_time " +
            "FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE m.tenant_id = #{tenantId} AND rm.role_id = #{roleId} " +
            "ORDER BY m.sort")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantId", column = "tenant_id"),
            @Result(property = "menuName", column = "menu_name"),
            @Result(property = "menuCode", column = "menu_code"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "menuType", column = "menu_type"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    List<MenuEntity> selectByTenantIdAndRoleId(Long tenantId, Long roleId);
    
    /**
     * 根据ID删除菜单
     */
    @Delete("DELETE FROM sys_menu WHERE id = #{id}")
    void deleteById(Long id);
    
    /**
     * 根据父菜单ID统计子菜单数量
     */
    @Select("SELECT COUNT(*) FROM sys_menu WHERE parent_id = #{parentId}")
    long countByParentId(Long parentId);
}