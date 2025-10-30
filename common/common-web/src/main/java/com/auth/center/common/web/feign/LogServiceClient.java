package com.auth.center.common.web.feign;

import com.auth.center.common.dto.LoginLogDTO;
import com.auth.center.common.dto.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 日志服务Feign客户端 - 公共版本
 */
@FeignClient(name = "log-service", path = "/api/log", url = "${feign.client.url.log-service:http://localhost:8080}")
public interface LogServiceClient {
    
    /**
     * 记录登录日志
     */
    @PostMapping("/login")
    SingleResponse<Boolean> recordLoginLog(@RequestBody LoginLogDTO loginLogDTO);
}