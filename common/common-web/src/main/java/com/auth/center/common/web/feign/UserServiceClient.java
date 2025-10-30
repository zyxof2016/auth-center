package com.auth.center.common.web.feign;

import com.auth.center.common.dto.UserDTO;
import com.auth.center.common.dto.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务Feign客户端 - 公共版本
 */
@FeignClient(name = "user-service", path = "/api/user", url = "${feign.client.url.user-service:http://localhost:8080}")
public interface UserServiceClient {
    
    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    SingleResponse<UserDTO> getUserByUsername(@RequestParam("tenantId") Long tenantId, @PathVariable("username") String username);
    
    /**
     * 根据手机号查询用户
     */
    @GetMapping("/phone/{phone}")
    SingleResponse<UserDTO> getUserByPhone(@RequestParam("tenantId") Long tenantId, @PathVariable("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     */
    @GetMapping("/email/{email}")
    SingleResponse<UserDTO> getUserByEmail(@RequestParam("tenantId") Long tenantId, @PathVariable("email") String email);
    
    /**
     * 根据用户ID查询用户
     */
    @GetMapping("/{userId}")
    SingleResponse<UserDTO> getUserById(@RequestParam("tenantId") Long tenantId, @PathVariable("userId") Long userId);
}