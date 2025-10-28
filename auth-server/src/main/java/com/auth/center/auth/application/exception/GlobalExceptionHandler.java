package com.auth.center.auth.application.exception;

import com.auth.center.common.dto.Response;
import com.auth.center.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response handleAuthException(AuthException e) {
        log.warn("认证异常: {} - {}", e.getErrorCode(), e.getErrorMessage());
        return Response.buildFailure(e.getErrorCode(), e.getErrorMessage());
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数验证异常: {}", e.getMessage());
        return Response.buildFailure(ErrorCode.PARAM_FORMAT_ERROR.getCode(), e.getMessage());
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return Response.buildFailure(ErrorCode.SYSTEM_ERROR);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Response.buildFailure(ErrorCode.SYSTEM_ERROR);
    }
}