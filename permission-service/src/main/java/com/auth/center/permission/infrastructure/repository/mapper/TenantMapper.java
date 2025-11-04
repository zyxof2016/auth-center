package com.auth.center.role.infrastructure.repository.mapper;

import com.auth.center.role.domain.entity.TenantEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 租户Mapper接口
 */
@Mapper
public interface TenantMapper {
    
    /**
     * 插入租户
     */
    @Insert("INSERT INTO sys_tenant(tenant_code, tenant_name, tenant_type, contact_person, contact_phone, email, address, status, expire_time, description, created_by, created_time, updated_by, updated_time) " +
            "VALUES(#{tenantCode}, #{tenantName}, #{tenantType}, #{contactPerson}, #{contactPhone}, #{email}, #{address}, #{status}, #{expireTime}, #{description}, #{createdBy}, #{createdTime}, #{updatedBy}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TenantEntity tenantEntity);
    
    /**
     * 根据ID更新租户
     */
    @Update("UPDATE sys_tenant SET tenant_name = #{tenantName}, tenant_type = #{tenantType}, contact_person = #{contactPerson}, contact_phone = #{contactPhone}, email = #{email}, address = #{address}, status = #{status}, expire_time = #{expireTime}, description = #{description}, updated_by = #{updatedBy}, updated_time = #{updatedTime} WHERE id = #{id}")
    void updateById(TenantEntity tenantEntity);
    
    /**
     * 根据ID查询租户
     */
    @Select("SELECT id, tenant_code, tenant_name, tenant_type, contact_person, contact_phone, email, address, status, expire_time, description, created_by, created_time, updated_by, updated_time FROM sys_tenant WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantCode", column = "tenant_code"),
            @Result(property = "tenantName", column = "tenant_name"),
            @Result(property = "tenantType", column = "tenant_type"),
            @Result(property = "contactPerson", column = "contact_person"),
            @Result(property = "contactPhone", column = "contact_phone"),
            @Result(property = "expireTime", column = "expire_time"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    TenantEntity selectById(Long id);
    
    /**
     * 根据租户编码查询租户
     */
    @Select("SELECT id, tenant_code, tenant_name, tenant_type, contact_person, contact_phone, email, address, status, expire_time, description, created_by, created_time, updated_by, updated_time FROM sys_tenant WHERE tenant_code = #{tenantCode}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tenantCode", column = "tenant_code"),
            @Result(property = "tenantName", column = "tenant_name"),
            @Result(property = "tenantType", column = "tenant_type"),
            @Result(property = "contactPerson", column = "contact_person"),
            @Result(property = "contactPhone", column = "contact_phone"),
            @Result(property = "expireTime", column = "expire_time"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "updatedBy", column = "updated_by"),
            @Result(property = "updatedTime", column = "updated_time")
    })
    TenantEntity selectByTenantCode(String tenantCode);
    
    /**
     * 根据ID删除租户
     */
    @Delete("DELETE FROM sys_tenant WHERE id = #{id}")
    void deleteById(Long id);
}