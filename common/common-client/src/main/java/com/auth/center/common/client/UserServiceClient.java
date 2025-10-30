package com.auth.center.common.client;

import com.auth.center.common.client.dto.UserDTO;
import com.auth.center.common.client.dto.request.UserCreateRequest;
import com.auth.center.common.client.dto.response.Response;

import java.util.List;

/**
 * 用户服务接口定义
 */
public interface UserServiceClient {

    /**
     * 创建用户
     *
     * @param request 用户创建请求
     * @return 用户信息
     */
    Response<UserDTO> createUser(UserCreateRequest request);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    Response<UserDTO> getUserById(Long id);

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    Response<List<UserDTO>> getAllUsers();
}