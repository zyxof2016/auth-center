package com.auth.center.role.infrastructure.repository;

import com.auth.center.role.domain.entity.RoleMenuEntity;
import com.auth.center.role.domain.repository.RoleMenuRepository;
import com.auth.center.role.infrastructure.repository.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联仓储实现
 */
@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {
    
    private final RoleMenuMapper roleMenuMapper;
    
    @Override
    public RoleMenuEntity save(RoleMenuEntity roleMenuEntity) {
        if (roleMenuEntity.getId() == null) {
            // 新增
            roleMenuMapper.insert(roleMenuEntity);
        } else {
            // 更新
            roleMenuMapper.updateById(roleMenuEntity);
        }
        return roleMenuEntity;
    }
    
    @Override
    public void deleteByRoleId(Long roleId) {
        roleMenuMapper.deleteByRoleId(roleId);
    }
    
    @Override
    public List<Long> findMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }
    
    @Override
    public List<RoleMenuEntity> findByRoleId(Long roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
    
    @Override
    public void batchSave(List<RoleMenuEntity> roleMenuEntities) {
        if (roleMenuEntities != null && !roleMenuEntities.isEmpty()) {
            roleMenuMapper.batchInsert(roleMenuEntities);
        }
    }
    
    @Override
    public void deleteByRoleIdAndMenuIds(Long roleId, List<Long> menuIds) {
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMenuMapper.deleteByRoleIdAndMenuIds(roleId, menuIds);
        }
    }
}