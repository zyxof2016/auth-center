package com.auth.center.common.dto;

import com.auth.center.common.exception.CommonErrorCode;

/**
 * 单条数据响应
 * 
 * @author auth-center
 */
public class SingleResponse<T> extends Response {

    private static final long serialVersionUID = 1L;

    /** 数据 */
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SingleResponse{" +
                "data=" + data +
                ", success=" + isSuccess() +
                ", errCode='" + getCode() + '\'' +
                ", errMessage='" + getMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    /**
     * 构建成功响应
     */
    public static <T> SingleResponse<T> buildSingleSuccess() {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(true);
        return response;
    }

    /**
     * 构建失败响应
     */
    public static <T> SingleResponse<T> buildSingleFailure(String errCode, String errMessage) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(false);
        response.setCode(errCode);
        response.setMessage(errMessage);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static <T> SingleResponse<T> buildSingleFailure(CommonErrorCode errorCode) {
        return buildSingleFailure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建包含数据的成功响应
     */
    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
}