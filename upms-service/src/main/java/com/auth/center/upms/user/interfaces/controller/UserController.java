package com.auth.center.user.interfaces.controller;

import com.auth.center.user.application.dto.UserDTO;
import com.auth.center.user.application.service.UserApplicationService;
import com.auth.center.user.domain.enums.UserStatus;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    /**
     * 创建用户
     */
    @PostMapping
    public SingleResponse<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return userApplicationService.createUser(userDTO);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public SingleResponse<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userApplicationService.updateUser(id, userDTO);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUserById(@PathVariable Long id) {
        return userApplicationService.getUserById(id);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public PageResponse<UserDTO> getUserPage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userApplicationService.getUserPage(tenantId, username, realName, status, page, size);
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    public Response enableUser(@PathVariable Long id) {
        return userApplicationService.enableUser(id);
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    public Response disableUser(@PathVariable Long id) {
        return userApplicationService.disableUser(id);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public Response resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        return userApplicationService.resetPassword(id, newPassword);
    }
    
    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public SingleResponse<UserDTO> getUserByUsername(@RequestParam Long tenantId, @PathVariable String username) {
        return userApplicationService.getUserByUsername(tenantId, username);
    }
    
    /**
     * 根据邮箱查询用户
     */
    @GetMapping("/email/{email}")
    public SingleResponse<UserDTO> getUserByEmail(@RequestParam Long tenantId, @PathVariable String email) {
        return userApplicationService.getUserByEmail(tenantId, email);
    }
    
    /**
     * 根据手机号查询用户
     */
    @GetMapping("/phone/{phone}")
    public SingleResponse<UserDTO> getUserByPhone(@RequestParam Long tenantId, @PathVariable String phone) {
        return userApplicationService.getUserByPhone(tenantId, phone);
    }
    
    /**
     * 更新用户登录信息
     */
    @PutMapping("/{id}/login-info")
    public Response updateLoginInfo(@PathVariable Long id, @RequestParam String loginIp) {
        return userApplicationService.updateLoginInfo(id, loginIp);
    }
}