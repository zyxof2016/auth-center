package com.auth.center.role.infrastructure.repository;

import com.auth.center.role.domain.entity.MenuEntity;
import com.auth.center.role.domain.repository.MenuRepository;
import com.auth.center.role.infrastructure.repository.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 菜单仓储实现
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {
    
    private final MenuMapper menuMapper;
    
    @Override
    public MenuEntity save(MenuEntity menuEntity) {
        if (menuEntity.getId() == null) {
            // 新增
            menuMapper.insert(menuEntity);
        } else {
            // 更新
            menuMapper.updateById(menuEntity);
        }
        return menuEntity;
    }
    
    @Override
    public Optional<MenuEntity> findById(Long id) {
        return Optional.ofNullable(menuMapper.selectById(id));
    }
    
    @Override
    public Optional<MenuEntity> findByMenuCode(Long tenantId, String menuCode) {
        return Optional.ofNullable(menuMapper.selectByMenuCode(tenantId, menuCode));
    }
    
    @Override
    public boolean existsByMenuCode(Long tenantId, String menuCode) {
        return menuMapper.selectByMenuCode(tenantId, menuCode) != null;
    }
    
    @Override
    public List<MenuEntity> findByParentId(Long tenantId, Long parentId) {
        return menuMapper.selectByParentId(tenantId, parentId);
    }
    
    @Override
    public List<MenuEntity> findByTenantId(Long tenantId) {
        return menuMapper.selectByTenantId(tenantId);
    }
    
    @Override
    public List<MenuEntity> findByTenantIdAndRoleId(Long tenantId, Long roleId) {
        return menuMapper.selectByTenantIdAndRoleId(tenantId, roleId);
    }
    
    @Override
    public void deleteById(Long id) {
        menuMapper.deleteById(id);
    }
    
    @Override
    public long countByParentId(Long parentId) {
        return menuMapper.countByParentId(parentId);
    }
}