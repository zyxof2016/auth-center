package com.auth.center.auth.config;

import com.auth.center.auth.domain.dto.LoginResult;
import com.auth.center.common.dto.SingleResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.auth.dto.LoginLogDTO;
import com.auth.center.auth.dto.UserDTO;
import com.auth.center.auth.feign.LogServiceClient;
import com.auth.center.auth.feign.UserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

/**
 * Feign客户端模拟配置，用于解决循环依赖问题
 */
@Configuration
public class FeignClientMockConfig {

    /**
     * 模拟UserServiceClient实现
     */
    @Bean
    @Primary
    public UserServiceClient userServiceClient() {
        return new UserServiceClient() {
            @Override
            public SingleResponse<UserDTO> getUserById(Long userId) {
                UserDTO user = new UserDTO();
                SingleResponse<UserDTO> response = new SingleResponse<>();
                response.setData(user);
                response.setSuccess(true);
                return response;
            }

            @Override
            public SingleResponse<UserDTO> getUserByUsername(Long tenantId, String username) {
                UserDTO user = new UserDTO();
                SingleResponse<UserDTO> response = new SingleResponse<>();
                response.setData(user);
                response.setSuccess(true);
                return response;
            }

            @Override
            public SingleResponse<UserDTO> getUserByPhone(Long tenantId, String phone) {
                UserDTO user = new UserDTO();
                SingleResponse<UserDTO> response = new SingleResponse<>();
                response.setData(user);
                response.setSuccess(true);
                return response;
            }

            @Override
            public SingleResponse<UserDTO> getUserByEmail(Long tenantId, String email) {
                UserDTO user = new UserDTO();
                SingleResponse<UserDTO> response = new SingleResponse<>();
                response.setData(user);
                response.setSuccess(true);
                return response;
            }

            @Override
            public SingleResponse<Boolean> validatePassword(Long userId, String password) {
                SingleResponse<Boolean> response = new SingleResponse<>();
                response.setData(true);
                response.setSuccess(true);
                return response;
            }

            @Override
            public Response updateLoginInfo(Long id, String loginIp) {
                // 模拟更新登录信息
                return Response.buildSuccess();
            }
        };
    }

    /**
     * 模拟LogServiceClient实现
     */
    @Bean
    @Primary
    public LogServiceClient logServiceClient() {
        return new LogServiceClient() {
            @Override
            public Response recordLoginLog(LoginLogDTO loginLogDTO) {
                // 模拟记录登录日志
                System.out.println("Mock: 记录登录日志");
                return Response.buildSuccess();
            }

            @Override
            public Response recordOperationLog(Object operationLogDTO) {
                // 模拟记录操作日志
                return Response.buildSuccess();
            }

            @Override
            public Response getLoginLogsByUserId(Long userId, Integer page, Integer size) {
                // 模拟获取登录日志列表
                return Response.buildSuccess();
            }
        };
    }
}