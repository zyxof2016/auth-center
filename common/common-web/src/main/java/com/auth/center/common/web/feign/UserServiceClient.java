package com.auth.center.common.web.feign;

import com.auth.center.common.dto.SingleResponse;
import com.auth.center.user.application.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/user")
public interface UserServiceClient {
    
    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    SingleResponse<UserDTO> getUserByUsername(@PathVariable("username") String username);
    
    /**
     * 根据手机号查询用户
     */
    @GetMapping("/phone/{phone}")
    SingleResponse<UserDTO> getUserByPhone(@PathVariable("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     */
    @GetMapping("/email/{email}")
    SingleResponse<UserDTO> getUserByEmail(@PathVariable("email") String email);
    
    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/{userId}")
    SingleResponse<UserDTO> getUserById(@PathVariable("userId") Long userId);
    
    /**
     * 验证用户密码
     */
    @PostMapping("/validate-password")
    SingleResponse<Boolean> validatePassword(@RequestParam("userId") Long userId, 
                                            @RequestParam("password") String password);
    
    /**
     * 更新用户登录信息
     */
    @PutMapping("/{userId}/login-info")
    SingleResponse<Void> updateLoginInfo(@PathVariable("userId") Long userId,
                                       @RequestParam("lastLoginIp") String lastLoginIp,
                                       @RequestParam("lastLoginTime") String lastLoginTime);
}