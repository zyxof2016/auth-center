package com.auth.center.file.domain.enums;

/**
 * 存储类型枚举
 */
public enum StorageType {
    
    /**
     * 阿里云OSS
     */
    OSS("oss", "阿里云OSS"),
    
    /**
     * MinIO
     */
    MINIO("minio", "MinIO"),
    
    /**
     * AWS S3
     */
    S3("s3", "AWS S3"),
    
    /**
     * Google Cloud Storage
     */
    GOOGLE("google", "Google Cloud Storage");
    
    private final String code;
    private final String description;
    
    StorageType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid storage type code: " + code);
    }
}