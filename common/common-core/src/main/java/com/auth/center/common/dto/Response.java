package com.auth.center.common.dto;

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
    private String errCode;

    /** 错误信息 */
    private String errMessage;

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

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
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
                ", errCode='" + errCode + '\'' +
                ", errMessage='" + errMessage + '\'' +
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
    public static Response buildFailure(String errCode, String errMessage) {
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static Response buildFailure(ErrorCode errorCode) {
        return buildFailure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建失败响应（使用错误码枚举和自定义消息）
     */
    public static Response buildFailure(ErrorCode errorCode, String customMessage) {
        return buildFailure(errorCode.getCode(), customMessage);
    }
}