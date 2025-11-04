package com.auth.center.upms.permission.application.service;

import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.upms.permission.application.dto.RoleDTO;
import com.auth.center.upms.permission.domain.entity.RoleEntity;
import com.auth.center.upms.permission.domain.enums.RoleStatus;
import com.auth.center.upms.permission.domain.exception.RoleErrorCode;
import com.auth.center.upms.permission.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色应用服务
 */
@Service
@RequiredArgsConstructor
public class RoleApplicationService {
    
    private final RoleRepository roleRepository;
    
    /**
     * 创建角色
     */
    public SingleResponse<RoleDTO> createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        if (roleRepository.existsByRoleCode(roleDTO.getTenantId(), roleDTO.getRoleCode())) {
            throw new BusinessException(RoleErrorCode.ROLE_CODE_EXISTS);
        }
        
        RoleEntity roleEntity = convertToEntity(roleDTO);
        roleEntity.setStatus(RoleStatus.ENABLED);
        roleEntity.setCreatedTime(LocalDateTime.now());
        
        RoleEntity savedEntity = roleRepository.save(roleEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 更新角色
     */
    public SingleResponse<RoleDTO> updateRole(Long id, RoleDTO roleDTO) {
        RoleEntity existingEntity = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        // 验证系统角色不能修改
        existingEntity.validateSystemRoleModification(roleDTO.getRoleCode(), roleDTO.getRoleType());
        
        // 更新字段
        existingEntity.update(roleDTO.getRoleName(), roleDTO.getRoleType(), roleDTO.getDataScope(), roleDTO.getDescription());
        
        RoleEntity updatedEntity = roleRepository.save(existingEntity);
        return SingleResponse.of(convertToDTO(updatedEntity));
    }
    
    /**
     * 获取角色详情
     */
    public SingleResponse<RoleDTO> getRoleById(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        return SingleResponse.of(convertToDTO(roleEntity));
    }
    
    /**
     * 分页查询角色列表
     */
    public PageResponse<RoleDTO> getRolePage(Long tenantId, String roleName, RoleStatus status,
                                             int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<RoleEntity> rolePage = roleRepository.findByConditions(tenantId, roleName, status, pageRequest);
        List<RoleDTO> roleDTOs = rolePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResponse.of(roleDTOs, page, size, rolePage.getTotalElements());
    }
    
    /**
     * 启用角色
     */
    public Response enableRole(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        roleEntity.enable();
        roleRepository.save(roleEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 禁用角色
     */
    public Response disableRole(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        roleEntity.disable();
        roleRepository.save(roleEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 删除角色
     */
    public Response deleteRole(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        // 验证系统角色不能删除
        roleEntity.validateSystemRoleDeletion();
        roleRepository.delete(roleEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 根据角色编码查询角色
     */
    public SingleResponse<RoleDTO> getRoleByRoleCode(Long tenantId, String roleCode) {
        RoleEntity roleEntity = roleRepository.findByRoleCode(tenantId, roleCode)
                .orElseThrow(() -> new BusinessException(RoleErrorCode.ROLE_NOT_FOUND));
        return SingleResponse.of(convertToDTO(roleEntity));
    }
    
    /**
     * 获取用户拥有的角色列表
     */
    public SingleResponse<List<RoleDTO>> getUserRoles(Long tenantId, Long userId) {
        List<RoleEntity> roleEntities = roleRepository.findByUserId(tenantId, userId);
        List<RoleDTO> roleDTOs = roleEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return SingleResponse.of(roleDTOs);
    }
    
    private RoleEntity convertToEntity(RoleDTO dto) {
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setRoleCode(dto.getRoleCode());
        entity.setRoleName(dto.getRoleName());
        entity.setRoleType(dto.getRoleType());
        entity.setDataScope(dto.getDataScope());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
    
    private RoleDTO convertToDTO(RoleEntity entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setRoleCode(entity.getRoleCode());
        dto.setRoleName(entity.getRoleName());
        dto.setRoleType(entity.getRoleType());
        dto.setDataScope(entity.getDataScope());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }
}