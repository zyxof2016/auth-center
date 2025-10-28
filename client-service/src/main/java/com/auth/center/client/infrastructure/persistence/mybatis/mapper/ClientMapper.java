package com.auth.center.client.infrastructure.persistence.mybatis.mapper;

import com.auth.center.client.domain.entity.ClientEntity;
import com.auth.center.client.domain.enums.ClientStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 客户端MyBatis映射器
 */
@Mapper
public interface ClientMapper {
    
    /**
     * 插入客户端
     */
    int insert(ClientEntity clientEntity);
    
    /**
     * 更新客户端
     */
    int update(ClientEntity clientEntity);
    
    /**
     * 根据ID查询客户端
     */
    ClientEntity selectById(Long id);
    
    /**
     * 根据客户端ID查询客户端
     */
    ClientEntity selectByClientId(String clientId);
    
    /**
     * 检查客户端ID是否存在
     */
    boolean existsByClientId(String clientId);
    
    /**
     * 根据条件查询客户端列表
     */
    List<ClientEntity> selectByConditions(@Param("tenantId") Long tenantId, 
                                         @Param("clientName") String clientName, 
                                         @Param("status") ClientStatus status, 
                                         @Param("pageable") Pageable pageable);
    
    /**
     * 根据条件查询客户端总数
     */
    long countByConditions(@Param("tenantId") Long tenantId, 
                          @Param("clientName") String clientName, 
                          @Param("status") ClientStatus status);
    
    /**
     * 删除客户端
     */
    int deleteById(Long id);
}