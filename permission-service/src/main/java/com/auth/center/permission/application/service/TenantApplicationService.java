package com.auth.center.role.application.service;

import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.role.application.dto.TenantDTO;
import com.auth.center.role.domain.entity.TenantEntity;
import com.auth.center.role.domain.exception.TenantMenuErrorCode;
import com.auth.center.role.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户应用服务
 */
@Service
@RequiredArgsConstructor
public class TenantApplicationService {
    
    private final TenantRepository tenantRepository;
    
    /**
     * 创建租户
     */
    public SingleResponse<TenantDTO> createTenant(TenantDTO tenantDTO) {
        // 检查租户编码是否已存在
        if (tenantRepository.existsByTenantCode(tenantDTO.getTenantCode())) {
            throw new BusinessException(TenantMenuErrorCode.TENANT_CODE_EXISTS);
        }
        
        TenantEntity tenantEntity = convertToEntity(tenantDTO);
        tenantEntity.setCreatedTime(LocalDateTime.now());
        tenantEntity.setUpdatedTime(LocalDateTime.now());
        
        TenantEntity savedEntity = tenantRepository.save(tenantEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 更新租户
     */
    public SingleResponse<TenantDTO> updateTenant(Long id, TenantDTO tenantDTO) {
        TenantEntity existingEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.TENANT_NOT_FOUND));
        
        // 更新字段
        existingEntity.setTenantName(tenantDTO.getTenantName());
        existingEntity.setTenantType(tenantDTO.getTenantType());
        existingEntity.setContactPerson(tenantDTO.getContactPerson());
        existingEntity.setContactPhone(tenantDTO.getContactPhone());
        existingEntity.setEmail(tenantDTO.getEmail());
        existingEntity.setAddress(tenantDTO.getAddress());
        existingEntity.setStatus(tenantDTO.getStatus());
        existingEntity.setExpireTime(tenantDTO.getExpireTime());
        existingEntity.setDescription(tenantDTO.getDescription());
        existingEntity.setUpdatedBy(tenantDTO.getUpdatedBy());
        existingEntity.setUpdatedTime(LocalDateTime.now());
        
        TenantEntity savedEntity = tenantRepository.save(existingEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 获取租户详情
     */
    public SingleResponse<TenantDTO> getTenantById(Long id) {
        TenantEntity tenantEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.TENANT_NOT_FOUND));
        return SingleResponse.of(convertToDTO(tenantEntity));
    }
    
    /**
     * 分页查询租户列表
     */
    public PageResponse<TenantDTO> getTenantList(String tenantName, Integer status, int page, int size) {
        // 这里简化实现，实际项目中需要根据条件查询
        Page<TenantEntity> tenantPage = null; // 实际实现需要查询数据库
        List<TenantDTO> tenantDTOs = null; // 实际实现需要转换
        
        return PageResponse.of(tenantDTOs, 0, page, size);
    }
    
    /**
     * 删除租户
     */
    public Response deleteTenant(Long id) {
        TenantEntity tenantEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.TENANT_NOT_FOUND));
        
        tenantRepository.deleteById(id);
        return Response.buildSuccess();
    }
    
    /**
     * 启用租户
     */
    public Response enableTenant(Long id) {
        TenantEntity tenantEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.TENANT_NOT_FOUND));
        
        tenantEntity.enable();
        tenantRepository.save(tenantEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 禁用租户
     */
    public Response disableTenant(Long id) {
        TenantEntity tenantEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.TENANT_NOT_FOUND));
        
        tenantEntity.disable();
        tenantRepository.save(tenantEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 将TenantDTO转换为TenantEntity
     */
    private TenantEntity convertToEntity(TenantDTO tenantDTO) {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenantCode(tenantDTO.getTenantCode());
        tenantEntity.setTenantName(tenantDTO.getTenantName());
        tenantEntity.setTenantType(tenantDTO.getTenantType());
        tenantEntity.setContactPerson(tenantDTO.getContactPerson());
        tenantEntity.setContactPhone(tenantDTO.getContactPhone());
        tenantEntity.setEmail(tenantDTO.getEmail());
        tenantEntity.setAddress(tenantDTO.getAddress());
        tenantEntity.setStatus(tenantDTO.getStatus());
        tenantEntity.setExpireTime(tenantDTO.getExpireTime());
        tenantEntity.setDescription(tenantDTO.getDescription());
        tenantEntity.setCreatedBy(tenantDTO.getCreatedBy());
        tenantEntity.setUpdatedBy(tenantDTO.getUpdatedBy());
        return tenantEntity;
    }
    
    /**
     * 将TenantEntity转换为TenantDTO
     */
    private TenantDTO convertToDTO(TenantEntity tenantEntity) {
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setId(tenantEntity.getId());
        tenantDTO.setTenantCode(tenantEntity.getTenantCode());
        tenantDTO.setTenantName(tenantEntity.getTenantName());
        tenantDTO.setTenantType(tenantEntity.getTenantType());
        tenantDTO.setContactPerson(tenantEntity.getContactPerson());
        tenantDTO.setContactPhone(tenantEntity.getContactPhone());
        tenantDTO.setEmail(tenantEntity.getEmail());
        tenantDTO.setAddress(tenantEntity.getAddress());
        tenantDTO.setStatus(tenantEntity.getStatus());
        tenantDTO.setExpireTime(tenantEntity.getExpireTime());
        tenantDTO.setDescription(tenantEntity.getDescription());
        tenantDTO.setCreatedBy(tenantEntity.getCreatedBy());
        tenantDTO.setCreatedTime(tenantEntity.getCreatedTime());
        tenantDTO.setUpdatedBy(tenantEntity.getUpdatedBy());
        tenantDTO.setUpdatedTime(tenantEntity.getUpdatedTime());
        return tenantDTO;
    }
}