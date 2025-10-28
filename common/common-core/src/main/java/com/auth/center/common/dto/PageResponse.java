package com.auth.center.common.dto;

import java.util.List;

/**
 * 分页数据响应
 * 
 * @author auth-center
 */
public class PageResponse<T> extends Response {

    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> data;

    /** 当前页码 */
    private int pageNum;

    /** 每页大小 */
    private int pageSize;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private int pages;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "data=" + data +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", pages=" + pages +
                ", success=" + isSuccess() +
                ", errCode='" + getErrCode() + '\'' +
                ", errMessage='" + getErrMessage() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    /**
     * 构建成功响应
     */
    public static <T> PageResponse<T> buildSuccess() {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        return response;
    }

    /**
     * 构建失败响应
     */
    public static <T> PageResponse<T> buildFailure(String errCode, String errMessage) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    /**
     * 构建失败响应（使用错误码枚举）
     */
    public static <T> PageResponse<T> buildFailure(ErrorCode errorCode) {
        return buildFailure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 构建分页响应
     */
    public static <T> PageResponse<T> of(List<T> data, int pageNum, int pageSize, long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotal(total);
        response.setPages((int) Math.ceil((double) total / pageSize));
        return response;
    }

    /**
     * 构建空分页响应
     */
    public static <T> PageResponse<T> empty(int pageNum, int pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotal(0);
        response.setPages(0);
        return response;
    }
}