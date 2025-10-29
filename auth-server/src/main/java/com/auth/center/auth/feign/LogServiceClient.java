package com.auth.center.auth.feign;

import com.auth.center.auth.dto.LoginLogDTO;
import com.auth.center.common.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 日志服务Feign客户端 - 本地版本
 */
@FeignClient(name = "log-service", path = "/api/log", url = "${feign.client.url.log-service:http://localhost:8080}")
public interface LogServiceClient {

    /**
     * 记录登录日志
     */
    @PostMapping("/login")
    Response recordLoginLog(@RequestBody LoginLogDTO loginLogDTO);

    /**
     * 记录操作日志
     */
    @PostMapping("/operation")
    Response recordOperationLog(@RequestBody Object operationLogDTO);

    /**
     * 根据用户ID获取登录日志列表
     */
    @PostMapping("/login/list")
    Response getLoginLogsByUserId(@RequestParam("userId") Long userId,
                                @RequestParam("page") Integer page,
                                @RequestParam("size") Integer size);
}