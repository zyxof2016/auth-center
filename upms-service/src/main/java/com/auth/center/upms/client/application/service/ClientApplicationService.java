package com.auth.center.upms.client.application.service;

import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.upms.client.application.dto.ClientDTO;
import com.auth.center.upms.client.domain.entity.ClientEntity;
import com.auth.center.upms.client.domain.enums.ClientStatus;
import com.auth.center.upms.client.domain.exception.ClientErrorCode;
import com.auth.center.upms.client.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端应用服务
 */
@Service
@RequiredArgsConstructor
public class ClientApplicationService {
    
    private final ClientRepository clientRepository;
    
    /**
     * 创建客户端
     */
    public SingleResponse<ClientDTO> createClient(ClientDTO clientDTO) {
        // 检查客户端ID是否已存在
        if (clientRepository.existsByClientId(clientDTO.getClientId())) {
            throw new BusinessException(ClientErrorCode.CLIENT_ID_EXISTS);
        }
        
        ClientEntity clientEntity = convertToEntity(clientDTO);
        clientEntity.setStatus(ClientStatus.ENABLED);
        clientEntity.setCreatedTime(LocalDateTime.now());
        
        ClientEntity savedEntity = clientRepository.save(clientEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 更新客户端
     */
    public SingleResponse<ClientDTO> updateClient(Long id, ClientDTO clientDTO) {
        ClientEntity existingEntity = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        
        // 更新字段
        existingEntity.setClientName(clientDTO.getClientName());
        existingEntity.setClientType(clientDTO.getClientType());
        existingEntity.setAuthorizedGrantTypes(clientDTO.getAuthorizedGrantTypes());
        existingEntity.setRedirectUris(clientDTO.getRedirectUris());
        existingEntity.setScope(clientDTO.getScope());
        existingEntity.setAccessTokenValidity(clientDTO.getAccessTokenValidity());
        existingEntity.setRefreshTokenValidity(clientDTO.getRefreshTokenValidity());
        existingEntity.setAutoApprove(clientDTO.getAutoApprove());
        existingEntity.setDescription(clientDTO.getDescription());
        existingEntity.setUpdatedTime(LocalDateTime.now());
        
        ClientEntity updatedEntity = clientRepository.save(existingEntity);
        return SingleResponse.of(convertToDTO(updatedEntity));
    }
    
    /**
     * 获取客户端详情
     */
    public SingleResponse<ClientDTO> getClientById(Long id) {
        ClientEntity clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        return SingleResponse.of(convertToDTO(clientEntity));
    }
    
    /**
     * 分页查询客户端列表
     */
    public PageResponse<ClientDTO> getClientPage(Long tenantId, String clientName, ClientStatus status, 
                                               int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ClientEntity> clientPage = clientRepository.findByConditions(tenantId, clientName, status, pageRequest);
        
        List<ClientDTO> clientDTOs = clientPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(clientDTOs, page, size, clientPage.getTotalElements());
    }
    
    /**
     * 启用客户端
     */
    public Response enableClient(Long id) {
        ClientEntity clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        
        clientEntity.setStatus(ClientStatus.ENABLED);
        clientEntity.setUpdatedTime(LocalDateTime.now());
        clientRepository.save(clientEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 禁用客户端
     */
    public Response disableClient(Long id) {
        ClientEntity clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        
        clientEntity.setStatus(ClientStatus.DISABLED);
        clientEntity.setUpdatedTime(LocalDateTime.now());
        clientRepository.save(clientEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 删除客户端
     */
    public Response deleteClient(Long id) {
        ClientEntity clientEntity = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        
        clientRepository.delete(clientEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 根据客户端ID获取客户端信息
     */
    public SingleResponse<ClientDTO> getClientByClientId(String clientId) {
        ClientEntity clientEntity = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new BusinessException(ClientErrorCode.CLIENT_NOT_FOUND));
        return SingleResponse.of(convertToDTO(clientEntity));
    }
    
    private ClientEntity convertToEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setClientId(dto.getClientId());
        entity.setClientName(dto.getClientName());
        entity.setClientType(dto.getClientType());
        entity.setAuthorizedGrantTypes(dto.getAuthorizedGrantTypes());
        entity.setRedirectUris(dto.getRedirectUris());
        entity.setScope(dto.getScope());
        entity.setAccessTokenValidity(dto.getAccessTokenValidity());
        entity.setRefreshTokenValidity(dto.getRefreshTokenValidity());
        entity.setAutoApprove(dto.getAutoApprove());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
    
    private ClientDTO convertToDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setClientId(entity.getClientId());
        dto.setClientName(entity.getClientName());
        dto.setClientType(entity.getClientType());
        dto.setAuthorizedGrantTypes(entity.getAuthorizedGrantTypes());
        dto.setRedirectUris(entity.getRedirectUris());
        dto.setScope(entity.getScope());
        dto.setAccessTokenValidity(entity.getAccessTokenValidity());
        dto.setRefreshTokenValidity(entity.getRefreshTokenValidity());
        dto.setAutoApprove(entity.getAutoApprove());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }
}