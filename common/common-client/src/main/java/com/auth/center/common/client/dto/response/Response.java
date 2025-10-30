package com.auth.center.common.client.dto.response;

import lombok.Data;

/**
 * 响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Response<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 构造成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应结果
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    /**
     * 构造失败响应
     *
     * @param code    响应码
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应结果
     */
    public static <T> Response<T> failure(String code, String message) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}