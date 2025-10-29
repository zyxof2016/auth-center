package com.auth.center.common.dto;

import com.auth.center.common.exception.ErrorCode;
import java.util.List;

/**
 * 多条数据响应
 * 
 * @author auth-center
 */
public class MultiResponse<T> extends Response {

    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> data;

    /** 总记录数 */
    private int total;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "MultiResponse{" +
                "data=" + data +
                ", total=" + total +
                ", success=" + isSuccess() +
                ", errCode='" + getErrCode() + '\'' +
                ", errMessage='" + getErrMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    /**
     * 构建成功响应
     */
    public static <T> MultiResponse<T> buildSuccess() {
        MultiResponse<T> response = new MultiResponse<>();
        response.setSuccess(true);
        return response;
    }

    /**
     * 构建失败响应
     */
    public static <T> MultiResponse<T> buildFailure(String errCode, String errMessage) {
        MultiResponse<T> response = new MultiResponse<>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static <T> MultiResponse<T> buildFailure(ErrorCode errorCode) {
        return buildFailure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建包含数据的成功响应
     */
    public static <T> MultiResponse<T> of(List<T> data) {
        MultiResponse<T> response = new MultiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(data != null ? data.size() : 0);
        return response;
    }

    /**
     * 构建包含数据和总数的成功响应
     */
    public static <T> MultiResponse<T> of(List<T> data, int total) {
        MultiResponse<T> response = new MultiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTotal(total);
        return response;
    }
}