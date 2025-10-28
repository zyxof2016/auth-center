package com.auth.center.client.infrastructure.persistence.mybatis.repository;

import com.auth.center.client.domain.entity.ClientEntity;
import com.auth.center.client.domain.enums.ClientStatus;
import com.auth.center.client.domain.repository.ClientRepository;
import com.auth.center.client.infrastructure.persistence.mybatis.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 客户端仓库MyBatis实现
 */
@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
    
    private final ClientMapper clientMapper;
    
    @Override
    public ClientEntity save(ClientEntity clientEntity) {
        if (clientEntity.getId() == null) {
            clientMapper.insert(clientEntity);
        } else {
            clientMapper.update(clientEntity);
        }
        return clientEntity;
    }
    
    @Override
    public Optional<ClientEntity> findById(Long id) {
        return Optional.ofNullable(clientMapper.selectById(id));
    }
    
    @Override
    public Optional<ClientEntity> findByClientId(String clientId) {
        return Optional.ofNullable(clientMapper.selectByClientId(clientId));
    }
    
    @Override
    public boolean existsByClientId(String clientId) {
        return clientMapper.existsByClientId(clientId);
    }
    
    @Override
    public Page<ClientEntity> findByConditions(Long tenantId, String clientName, ClientStatus status, Pageable pageable) {
        List<ClientEntity> content = clientMapper.selectByConditions(tenantId, clientName, status, pageable);
        long total = clientMapper.countByConditions(tenantId, clientName, status);
        return new PageImpl<>(content, pageable, total);
    }
    
    @Override
    public void delete(ClientEntity clientEntity) {
        clientMapper.deleteById(clientEntity.getId());
    }
}