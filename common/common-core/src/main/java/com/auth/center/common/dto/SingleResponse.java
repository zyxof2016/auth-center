package com.auth.center.common.dto;

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
                ", errCode='" + getErrCode() + '\'' +
                ", errMessage='" + getErrMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    /**
     * 构建成功响应
     */
    public static <T> SingleResponse<T> buildSuccess() {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(true);
        return response;
    }

    /**
     * 构建失败响应
     */
    public static <T> SingleResponse<T> buildFailure(String errCode, String errMessage) {
        SingleResponse<T> response = new SingleResponse<>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static <T> SingleResponse<T> buildFailure(ErrorCode errorCode) {
        return buildFailure(errorCode.getCode(), errorCode.getMessage());
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