package com.auth.center.user.infrastructure.repository;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.repository.UserRepository;
import com.auth.center.user.infrastructure.convertor.UserConvertor;
import com.auth.center.user.infrastructure.persistence.UserMapper;
import com.auth.center.user.infrastructure.persistence.po.UserPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户仓库实现类
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserMapper userMapper;
    private final UserConvertor userConvertor;
    
    @Override
    public User save(User user) {
        UserPO userPO = userConvertor.toPO(user);
        userMapper.insert(userPO);
        return userConvertor.toDomain(userPO);
    }
    
    @Override
    public User findById(Long id) {
        UserPO userPO = userMapper.selectById(id);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public User findByUsername(Long tenantId, String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getUsername, username);
        UserPO userPO = userMapper.selectOne(wrapper);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public User findByEmail(Long tenantId, String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getEmail, email);
        UserPO userPO = userMapper.selectOne(wrapper);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public User findByPhone(Long tenantId, String phone) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getPhone, phone);
        UserPO userPO = userMapper.selectOne(wrapper);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public boolean existsByUsername(Long tenantId, String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(Long tenantId, String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByPhone(Long tenantId, String phone) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId).eq(UserPO::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public org.springframework.data.domain.Page<User> findByConditions(Long tenantId, String username, String realName,
                                       UserStatus status, Pageable pageable) {
        // 构建分页对象
        IPage<UserPO> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getTenantId, tenantId);
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(UserPO::getUsername, username);
        }
        
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(UserPO::getRealName, realName);
        }
        
        if (status != null) {
            wrapper.eq(UserPO::getStatus, status.getCode());
        }
        
        // 执行分页查询
        IPage<UserPO> result = userMapper.selectPage(page, wrapper);
        
        // 转换为领域实体列表
        List<User> userList = result.getRecords().stream()
                .map(userConvertor::toDomain)
                .collect(Collectors.toList());
        
        // 返回Spring Data Page对象
        return new org.springframework.data.domain.PageImpl<>(userList, pageable, result.getTotal());
    }
    
    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
    
    @Override
    public User update(User user) {
        UserPO userPO = userConvertor.toPO(user);
        userMapper.updateById(userPO);
        return userConvertor.toDomain(userPO);
    }
}