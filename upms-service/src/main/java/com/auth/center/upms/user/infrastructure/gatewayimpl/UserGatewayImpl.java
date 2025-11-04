package com.auth.center.user.infrastructure.gatewayimpl;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.gateway.UserGateway;
import com.auth.center.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户网关实现（防腐层）
 */
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {
    
    private final UserRepository userRepository;
    
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public User findByUsername(Long tenantId, String username) {
        return userRepository.findByUsername(tenantId, username);
    }
    
    @Override
    public boolean existsByUsername(Long tenantId, String username) {
        return userRepository.existsByUsername(tenantId, username);
    }
    
    @Override
    public User update(User user) {
        return userRepository.update(user);
    }
    
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}