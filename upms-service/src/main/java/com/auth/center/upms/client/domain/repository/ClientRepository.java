package com.auth.center.client.domain.repository;

import com.auth.center.client.domain.entity.ClientEntity;
import com.auth.center.client.domain.enums.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 客户端仓库接口
 */
public interface ClientRepository {
    
    /**
     * 保存客户端
     */
    ClientEntity save(ClientEntity clientEntity);
    
    /**
     * 根据ID查找客户端
     */
    Optional<ClientEntity> findById(Long id);
    
    /**
     * 根据客户端ID查找客户端
     */
    Optional<ClientEntity> findByClientId(String clientId);
    
    /**
     * 检查客户端ID是否存在
     */
    boolean existsByClientId(String clientId);
    
    /**
     * 根据条件分页查询客户端
     */
    Page<ClientEntity> findByConditions(Long tenantId, String clientName, ClientStatus status, Pageable pageable);
    
    /**
     * 删除客户端
     */
    void delete(ClientEntity clientEntity);
}