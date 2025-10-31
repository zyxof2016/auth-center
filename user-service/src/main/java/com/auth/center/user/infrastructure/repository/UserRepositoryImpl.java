package com.auth.center.user.infrastructure.repository;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.repository.UserRepository;
import com.auth.center.user.infrastructure.convertor.UserConvertor;
import com.auth.center.user.infrastructure.persistence.UserMapper;
import com.auth.center.user.infrastructure.persistence.po.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        UserPO userPO = userMapper.selectByUsername(tenantId, username);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public User findByEmail(Long tenantId, String email) {
        UserPO userPO = userMapper.selectByEmail(tenantId, email);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public User findByPhone(Long tenantId, String phone) {
        UserPO userPO = userMapper.selectByPhone(tenantId, phone);
        return userPO != null ? userConvertor.toDomain(userPO) : null;
    }
    
    @Override
    public boolean existsByUsername(Long tenantId, String username) {
        return userMapper.countByUsername(tenantId, username) > 0;
    }
    
    @Override
    public boolean existsByEmail(Long tenantId, String email) {
        return userMapper.countByEmail(tenantId, email) > 0;
    }
    
    @Override
    public boolean existsByPhone(Long tenantId, String phone) {
        return userMapper.countByPhone(tenantId, phone) > 0;
    }
    
    @Override
    public Page<User> findByConditions(Long tenantId, String username, String realName,
                                       UserStatus status, Pageable pageable) {
        // 构建查询条件
        long total = userMapper.countByConditions(tenantId, username, realName, 
                status != null ? status.getCode() : null);
        
        if (total == 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        // 分页查询
        List<UserPO> userPOList = userMapper.selectByConditions(
                tenantId, username, realName, status != null ? status.getCode() : null, 
                pageable.getOffset(), pageable.getPageSize());
        
        // 转换为领域实体列表
        List<User> userList = userPOList.stream()
                .map(userConvertor::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(userList, pageable, total);
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