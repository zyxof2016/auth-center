package com.auth.center.file.infrastructure.service;

import com.auth.center.file.domain.enums.StorageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file, String bucketName, String objectKey);
    
    /**
     * 上传文件
     */
    String uploadFile(InputStream inputStream, String fileName, String bucketName, String objectKey);
    
    /**
     * 下载文件
     */
    InputStream downloadFile(String bucketName, String objectKey);
    
    /**
     * 删除文件
     */
    void deleteFile(String bucketName, String objectKey);
    
    /**
     * 获取文件访问URL
     */
    String getFileUrl(String bucketName, String objectKey);
    
    /**
     * 获取存储类型
     */
    StorageType getStorageType();
}