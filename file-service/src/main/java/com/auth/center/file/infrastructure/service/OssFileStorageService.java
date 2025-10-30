package com.auth.center.file.infrastructure.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.auth.center.file.domain.enums.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里云OSS文件存储服务实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "oss")
public class OssFileStorageService implements FileStorageService {
    
    @Value("${file.storage.oss.endpoint}")
    private String endpoint;
    
    @Value("${file.storage.oss.access-key}")
    private String accessKey;
    
    @Value("${file.storage.oss.secret-key}")
    private String secretKey;
    
    @Value("${file.storage.oss.bucket-name}")
    private String defaultBucketName;
    
    private OSS ossClient;
    
    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        
        // 确保默认存储桶存在
        // 在实际应用中，需要创建存储桶
    }
    
    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            ossClient.putObject(bucketName, objectKey, file.getInputStream());
            
            return getFileUrl(bucketName, objectKey);
        } catch (Exception e) {
            log.error("上传文件到OSS失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            ossClient.putObject(bucketName, objectKey, inputStream);
            
            return getFileUrl(bucketName, objectKey);
        } catch (Exception e) {
            log.error("上传文件到OSS失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public InputStream downloadFile(String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            return ossClient.getObject(bucketName, objectKey).getObjectContent();
        } catch (Exception e) {
            log.error("从OSS下载文件失败", e);
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    @Override
    public void deleteFile(String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            ossClient.deleteObject(bucketName, objectKey);
        } catch (Exception e) {
            log.error("从OSS删除文件失败", e);
            throw new RuntimeException("文件删除失败", e);
        }
    }
    
    @Override
    public String getFileUrl(String bucketName, String objectKey) {
        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = defaultBucketName;
        }
        
        return "https://" + bucketName + "." + endpoint + "/" + objectKey;
    }
    
    @Override
    public StorageType getStorageType() {
        return StorageType.OSS;
    }
}