package com.auth.center.log.domain.enums;

/**
 * 日志状态枚举
 */
public enum LogStatus {
    
    /**
     * 失败
     */
    FAILED(0, "失败"),
    
    /**
     * 成功
     */
    SUCCESS(1, "成功");
    
    private final int code;
    private final String description;
    
    LogStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static LogStatus fromCode(int code) {
        for (LogStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid log status code: " + code);
    }
}