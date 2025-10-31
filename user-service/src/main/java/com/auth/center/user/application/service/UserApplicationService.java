package com.auth.center.user.application.service;

import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.exception.BusinessException;
import com.auth.center.common.exception.CommonErrorCode;
import com.auth.center.user.application.dto.UserDTO;
import com.auth.center.user.domain.entity.User;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.user.domain.repository.UserRepository;
import com.auth.center.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务（符合COLA架构规范）
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 创建用户
     */
    public SingleResponse<UserDTO> createUser(UserDTO userDTO) {
        try {
            // 将DTO转换为领域实体
            User user = convertToDomain(userDTO);
            
            // 通过领域服务创建用户
            User createdUser = userDomainService.createUser(user);
            
            // 将领域实体转换为DTO返回
            return SingleResponse.of(convertToDTO(createdUser));
        } catch (IllegalStateException e) {
            throw new BusinessException(CommonErrorCode.USER_INPUT_ERROR, e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    public SingleResponse<UserDTO> updateUser(Long id, UserDTO userDTO) {
        // 从领域服务获取用户
        User existingUser = userDomainService.getUserById(id);
        if (existingUser == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        // 更新用户信息
        existingUser.updateInfo(userDTO.getEmail(), userDTO.getPhone(), userDTO.getRealName());
        existingUser.setNickname(userDTO.getNickname());
        existingUser.setAvatar(userDTO.getAvatar());
        existingUser.setGender(userDTO.getGender());
        existingUser.setBirthday(userDTO.getBirthday());
        existingUser.setUpdatedTime(LocalDateTime.now());
        
        // 通过领域服务更新用户
        User updatedUser = userDomainService.updateUserInfo(existingUser);
        return SingleResponse.of(convertToDTO(updatedUser));
    }
    
    /**
     * 获取用户详情
     */
    public SingleResponse<UserDTO> getUserById(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        return SingleResponse.of(convertToDTO(user));
    }
    
    /**
     * 根据邮箱查询用户
     */
    public SingleResponse<UserDTO> getUserByEmail(Long tenantId, String email) {
        User user = userRepository.findByEmail(tenantId, email);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        return SingleResponse.of(convertToDTO(user));
    }
    
    /**
     * 根据手机号查询用户
     */
    public SingleResponse<UserDTO> getUserByPhone(Long tenantId, String phone) {
        User user = userRepository.findByPhone(tenantId, phone);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        return SingleResponse.of(convertToDTO(user));
    }
    
    /**
     * 分页查询用户列表
     */
    public PageResponse<UserDTO> getUserPage(Long tenantId, String username, String realName, 
                                           UserStatus status, int page, int size) {
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        
        // 通过领域服务获取分页数据
        Page<User> userPage = userDomainService.getUserPage(tenantId, username, realName, status, pageRequest);
        
        // 转换为DTO列表
        List<UserDTO> userDTOList = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return PageResponse.of(userDTOList, userPage.getNumber() + 1, userPage.getSize(), 
                              userPage.getTotalElements());
    }
    
    /**
     * 启用用户
     */
    public Response enableUser(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.unlock(); // 解锁用户
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 禁用用户
     */
    public Response disableUser(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.lock(); // 锁定用户
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 锁定用户
     */
    public Response lockUser(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.lock(); // 锁定用户
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 重置用户密码
     */
    public Response resetPassword(Long id, String newPassword) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        // 验证密码强度
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException(CommonErrorCode.PARAM_FORMAT_ERROR, "密码长度不能少于6位");
        }
        
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }

    /**
     * 根据用户名查询用户
     */
    public SingleResponse<UserDTO> getUserByUsername(Long tenantId, String username) {
        User user = userRepository.findByUsername(tenantId, username);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        return SingleResponse.of(convertToDTO(user));
    }
    
    /**
     * 更新用户登录信息
     */
    public Response updateLoginInfo(Long id, String loginIp) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.recordLogin(loginIp);
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 增加登录失败次数
     */
    public Response increaseLoginFailCount(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.incrementLoginFailCount();
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 重置登录失败次数
     */
    public Response resetLoginFailCount(Long id) {
        User user = userDomainService.getUserById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.USER_NOT_EXIST);
        }
        
        user.resetLoginFailCount();
        userDomainService.updateUserInfo(user);
        
        return Response.buildSuccess();
    }
    
    /**
     * 将DTO转换为领域实体
     *
     * @param dto 用户DTO
     * @return 领域实体
     */
    private User convertToDomain(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setTenantId(dto.getTenantId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRealName(dto.getRealName());
        user.setNickname(dto.getNickname());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setBirthday(dto.getBirthday());
        user.setStatus(dto.getStatus());
        user.setUserType(dto.getUserType());
        return user;
    }
    
    /**
     * 将领域实体转换为DTO
     *
     * @param entity 领域实体
     * @return 用户DTO
     */
    private UserDTO convertToDTO(User entity) {
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