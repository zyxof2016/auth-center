package com.auth.center.user.application.service;

import com.auth.center.user.application.dto.UserDTO;
import com.auth.center.user.domain.entity.UserEntity;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.enums.UserType;
import com.auth.center.user.domain.repository.UserRepository;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 创建用户
     */
    public SingleResponse<UserDTO> createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getTenantId(), userDTO.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已存在
        if (userDTO.getEmail() != null && userRepository.existsByEmail(userDTO.getTenantId(), userDTO.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        
        // 检查手机号是否已存在
        if (userDTO.getPhone() != null && userRepository.existsByPhone(userDTO.getTenantId(), userDTO.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_EXISTS);
        }
        
        UserEntity userEntity = convertToEntity(userDTO);
        userEntity.setStatus(UserStatus.ENABLED);
        userEntity.setUserType(UserType.NORMAL);
        userEntity.setCreatedTime(LocalDateTime.now());
        
        // 加密密码
        if (userEntity.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }
        
        UserEntity savedEntity = userRepository.save(userEntity);
        return SingleResponse.of(convertToDTO(savedEntity));
    }
    
    /**
     * 更新用户
     */
    public SingleResponse<UserDTO> updateUser(Long id, UserDTO userDTO) {
        UserEntity existingEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 更新字段
        existingEntity.setRealName(userDTO.getRealName());
        existingEntity.setNickname(userDTO.getNickname());
        existingEntity.setAvatar(userDTO.getAvatar());
        existingEntity.setGender(userDTO.getGender());
        existingEntity.setBirthday(userDTO.getBirthday());
        existingEntity.setDescription(userDTO.getDescription());
        existingEntity.setUpdatedTime(LocalDateTime.now());
        
        UserEntity updatedEntity = userRepository.save(existingEntity);
        return SingleResponse.of(convertToDTO(updatedEntity));
    }
    
    /**
     * 获取用户详情
     */
    public SingleResponse<UserDTO> getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return SingleResponse.of(convertToDTO(userEntity));
    }
    
    /**
     * 分页查询用户列表
     */
    public PageResponse<UserDTO> getUserPage(Long tenantId, String username, String realName, 
                                           UserStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<UserEntity> userPage = userRepository.findByConditions(tenantId, username, realName, status, pageRequest);
        
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(userDTOs, userPage.getTotalElements(), page, size);
    }
    
    /**
     * 启用用户
     */
    public Response enableUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userEntity.setStatus(UserStatus.ENABLED);
        userEntity.setUpdatedTime(LocalDateTime.now());
        userRepository.save(userEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 禁用用户
     */
    public Response disableUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userEntity.setStatus(UserStatus.DISABLED);
        userEntity.setUpdatedTime(LocalDateTime.now());
        userRepository.save(userEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 重置用户密码
     */
    public Response resetPassword(Long id, String newPassword) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setPwdUpdateTime(LocalDateTime.now());
        userEntity.setUpdatedTime(LocalDateTime.now());
        userRepository.save(userEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 根据用户名查询用户
     */
    public SingleResponse<UserDTO> getUserByUsername(Long tenantId, String username) {
        UserEntity userEntity = userRepository.findByUsername(tenantId, username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return SingleResponse.of(convertToDTO(userEntity));
    }
    
    /**
     * 更新用户登录信息
     */
    public Response updateLoginInfo(Long id, String loginIp) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userEntity.setLastLoginTime(LocalDateTime.now());
        userEntity.setLastLoginIp(loginIp);
        userEntity.setLoginFailCount(0); // 重置登录失败次数
        userRepository.save(userEntity);
        
        return Response.buildSuccess();
    }
    
    /**
     * 增加登录失败次数
     */
    public Response increaseLoginFailCount(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userEntity.setLoginFailCount(userEntity.getLoginFailCount() + 1);
        userRepository.save(userEntity);
        
        return Response.buildSuccess();
    }
    
    private UserEntity convertToEntity(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setRealName(dto.getRealName());
        entity.setNickname(dto.getNickname());
        entity.setAvatar(dto.getAvatar());
        entity.setGender(dto.getGender());
        entity.setBirthday(dto.getBirthday());
        entity.setStatus(dto.getStatus());
        entity.setUserType(dto.getUserType());
        return entity;
    }
    
    private UserDTO convertToDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setRealName(entity.getRealName());
        dto.setNickname(entity.getNickname());
        dto.setAvatar(entity.getAvatar());
        dto.setGender(entity.getGender());
        dto.setBirthday(entity.getBirthday());
        dto.setStatus(entity.getStatus());
        dto.setUserType(entity.getUserType());
        dto.setLastLoginTime(entity.getLastLoginTime());
        dto.setLastLoginIp(entity.getLastLoginIp());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }
}