package com.auth.center.common.exception;

/**
 * 错误码接口
 */
public interface ErrorCode {
    /**
     * 错误码
     *
     * @return
     */
    String getCode();

    /**
     * 错误信息
     *
     * @return
     */
    String getMessage();
}
