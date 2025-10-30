package com.auth.center.common.dto;

import com.auth.center.common.exception.CommonErrorCode;

import java.io.Serializable;

/**
 * 基础响应类
 * 
 * @author auth-center
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    private boolean success;

    /** 错误码 */
    private String code;

    /** 错误信息 */
    private String message;

    /** 时间戳 */
    private long timestamp;

    public Response() {
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * 构建成功响应
     */
    public static Response buildSuccess() {
        Response response = new Response();
        response.setSuccess(true);
        return response;
    }

    /**
     * 构建失败响应
     */
    public static Response buildFailure(String code, String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static Response buildFailure(CommonErrorCode errorCode) {
        return buildFailure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建失败响应（使用错误码枚举和自定义消息）
     */
    public static Response buildFailure(CommonErrorCode errorCode, String customMessage) {
        return buildFailure(errorCode.getCode(), customMessage);
    }
}