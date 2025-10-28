package com.auth.center.common.exception;

/**
 * 业务异常类
 * 
 * @author auth-center
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private String errCode;

    /** 错误信息 */
    private String errMessage;

    public BusinessException() {
        super();
    }

    public BusinessException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
        this.errCode = ErrorCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errCode = errorCode.getCode();
        this.errMessage = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errCode = errorCode.getCode();
        this.errMessage = customMessage;
    }

    public BusinessException(String errMessage, Throwable cause) {
        super(errMessage, cause);
        this.errMessage = errMessage;
        this.errCode = ErrorCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(String errCode, String errMessage, Throwable cause) {
        super(errMessage, cause);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errCode = errorCode.getCode();
        this.errMessage = errorCode.getMessage();
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

    @Override
    public String toString() {
        return "BusinessException{" +
                "errCode='" + errCode + '\'' +
                ", errMessage='" + errMessage + '\'' +
                "}";
    }

    /**
     * 创建业务异常
     */
    public static BusinessException of(String errMessage) {
        return new BusinessException(errMessage);
    }

    /**
     * 创建业务异常
     */
    public static BusinessException of(String errCode, String errMessage) {
        return new BusinessException(errCode, errMessage);
    }

    /**
     * 创建业务异常
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * 创建业务异常
     */
    public static BusinessException of(ErrorCode errorCode, String customMessage) {
        return new BusinessException(errorCode, customMessage);
    }
}