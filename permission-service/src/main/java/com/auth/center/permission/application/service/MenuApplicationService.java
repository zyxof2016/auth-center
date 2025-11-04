package com.auth.center.role.application.service;

import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.role.application.dto.MenuDTO;
import com.auth.center.role.domain.entity.MenuEntity;
import com.auth.center.role.domain.exception.TenantMenuErrorCode;
import com.auth.center.role.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单应用服务
 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {
    
    private final MenuRepository menuRepository;
    
    /**
     * 创建菜单
     */
    public SingleResponse<MenuDTO> createMenu(MenuDTO menuDTO) {
        // 检查菜单编码是否已存在
        if (menuRepository.existsByMenuCode(menuDTO.getTenantId(), menuDTO.getMenuCode())) {
            throw new BusinessException(TenantMenuErrorCode.MENU_CODE_EXISTS);
        }
        
        // 检查父菜单是否存在
        if (menuDTO.getParentId() != null && menuDTO.getParentId() > 0) {
            menuRepository.findById(menuDTO.getParentId())
                    .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.PARENT_MENU_NOT_FOUND));
        }
        
        MenuEntity menuEntity = convertToEntity(menuDTO);
        menuEntity.setCreatedTime(LocalDateTime.now());
        menuEntity.setUpdatedTime(LocalDateTime.now());
        
        MenuEntity savedEntity = menuRepository.save(menuEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 更新菜单
     */
    public SingleResponse<MenuDTO> updateMenu(Long id, MenuDTO menuDTO) {
        MenuEntity existingEntity = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.MENU_NOT_FOUND));
        
        // 检查不能将菜单设置为自己的子菜单
        if (id.equals(menuDTO.getParentId())) {
            throw new BusinessException(TenantMenuErrorCode.MENU_CANNOT_BE_CHILD_OF_ITSELF);
        }
        
        // 更新字段
        existingEntity.setMenuName(menuDTO.getMenuName());
        existingEntity.setMenuCode(menuDTO.getMenuCode());
        existingEntity.setParentId(menuDTO.getParentId());
        existingEntity.setMenuType(menuDTO.getMenuType());
        existingEntity.setPath(menuDTO.getPath());
        existingEntity.setComponent(menuDTO.getComponent());
        existingEntity.setPermission(menuDTO.getPermission());
        existingEntity.setIcon(menuDTO.getIcon());
        existingEntity.setSort(menuDTO.getSort());
        existingEntity.setVisible(menuDTO.getVisible());
        existingEntity.setStatus(menuDTO.getStatus());
        existingEntity.setDescription(menuDTO.getDescription());
        existingEntity.setUpdatedBy(menuDTO.getUpdatedBy());
        existingEntity.setUpdatedTime(LocalDateTime.now());
        
        MenuEntity savedEntity = menuRepository.save(existingEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 获取菜单详情
     */
    public SingleResponse<MenuDTO> getMenuById(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.MENU_NOT_FOUND));
        return SingleResponse.of(convertToDTO(menuEntity));
    }
    
    /**
     * 根据租户ID获取菜单列表
     */
    public SingleResponse<List<MenuDTO>> getMenuListByTenantId(Long tenantId) {
        List<MenuEntity> menuEntities = menuRepository.findByTenantId(tenantId);
        List<MenuDTO> menuDTOs = menuEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return SingleResponse.of(menuDTOs);
    }
    
    /**
     * 根据租户ID和角色ID获取菜单列表
     */
    public SingleResponse<List<MenuDTO>> getMenuListByTenantIdAndRoleId(Long tenantId, Long roleId) {
        List<MenuEntity> menuEntities = menuRepository.findByTenantIdAndRoleId(tenantId, roleId);
        List<MenuDTO> menuDTOs = menuEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return SingleResponse.of(menuDTOs);
    }
    
    /**
     * 删除菜单
     */
    public Response deleteMenu(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.MENU_NOT_FOUND));
        
        // 检查是否有子菜单
        long childCount = menuRepository.countByParentId(id);
        if (childCount > 0) {
            // 实际项目中可能需要级联删除或返回错误
            // 这里简化处理
        }
        
        menuRepository.deleteById(id);
        return Response.buildSuccess();
    }
    
    /**
     * 启用菜单
     */
    public Response enableMenu(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.MENU_NOT_FOUND));
        
        menuEntity.enable();
        menuRepository.save(menuEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 禁用菜单
     */
    public Response disableMenu(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TenantMenuErrorCode.MENU_NOT_FOUND));
        
        menuEntity.disable();
        menuRepository.save(menuEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 将MenuDTO转换为MenuEntity
     */
    private MenuEntity convertToEntity(MenuDTO menuDTO) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setTenantId(menuDTO.getTenantId());
        menuEntity.setMenuName(menuDTO.getMenuName());
        menuEntity.setMenuCode(menuDTO.getMenuCode());
        menuEntity.setParentId(menuDTO.getParentId());
        menuEntity.setMenuType(menuDTO.getMenuType());
        menuEntity.setPath(menuDTO.getPath());
        menuEntity.setComponent(menuDTO.getComponent());
        menuEntity.setPermission(menuDTO.getPermission());
        menuEntity.setIcon(menuDTO.getIcon());
        menuEntity.setSort(menuDTO.getSort());
        menuEntity.setVisible(menuDTO.getVisible());
        menuEntity.setStatus(menuDTO.getStatus());
        menuEntity.setDescription(menuDTO.getDescription());
        menuEntity.setCreatedBy(menuDTO.getCreatedBy());
        menuEntity.setUpdatedBy(menuDTO.getUpdatedBy());
        return menuEntity;
    }
    
    /**
     * 将MenuEntity转换为MenuDTO
     */
    private MenuDTO convertToDTO(MenuEntity menuEntity) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menuEntity.getId());
        menuDTO.setTenantId(menuEntity.getTenantId());
        menuDTO.setMenuName(menuEntity.getMenuName());
        menuDTO.setMenuCode(menuEntity.getMenuCode());
        menuDTO.setParentId(menuEntity.getParentId());
        menuDTO.setMenuType(menuEntity.getMenuType());
        menuDTO.setPath(menuEntity.getPath());
        menuDTO.setComponent(menuEntity.getComponent());
        menuDTO.setPermission(menuEntity.getPermission());
        menuDTO.setIcon(menuEntity.getIcon());
        menuDTO.setSort(menuEntity.getSort());
        menuDTO.setVisible(menuEntity.getVisible());
        menuDTO.setStatus(menuEntity.getStatus());
        menuDTO.setDescription(menuEntity.getDescription());
        menuDTO.setCreatedBy(menuEntity.getCreatedBy());
        menuDTO.setCreatedTime(menuEntity.getCreatedTime());
        menuDTO.setUpdatedBy(menuEntity.getUpdatedBy());
        menuDTO.setUpdatedTime(menuEntity.getUpdatedTime());
        return menuDTO;
    }
}