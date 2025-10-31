package com.auth.center.auth.infrastructure.client;

import com.auth.center.auth.domain.dto.UserInfoDTO;
import com.auth.center.common.dto.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    
    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    SingleResponse<UserInfoDTO> getUserByUsername(@RequestParam("tenantId") Long tenantId, @PathVariable("username") String username);
    
    /**
     * 根据邮箱获取用户信息
     */
    @GetMapping("/email/{email}")
    SingleResponse<UserInfoDTO> getUserByEmail(@RequestParam("tenantId") Long tenantId, @PathVariable("email") String email);
    
    /**
     * 根据手机号获取用户信息
     */
    @GetMapping("/phone/{phone}")
    SingleResponse<UserInfoDTO> getUserByPhone(@RequestParam("tenantId") Long tenantId, @PathVariable("phone") String phone);
}