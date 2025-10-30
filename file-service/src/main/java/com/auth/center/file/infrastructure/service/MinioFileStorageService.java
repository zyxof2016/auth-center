package com.auth.center.file.infrastructure.service;

import com.auth.center.file.domain.enums.StorageType;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MinIO文件存储服务实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "minio")
public class MinioFileStorageService implements FileStorageService {
    
    @Value("${file.storage.minio.endpoint}")
    private String endpoint;
    
    @Value("${file.storage.minio.access-key}")
    private String accessKey;
    
    @Value("${file.storage.minio.secret-key}")
    private String secretKey;
    
    @Value("${file.storage.minio.bucket-name}")
    private String defaultBucketName;
    
    private MinioClient minioClient;
    
    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        
        // 确保默认存储桶存在
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(defaultBucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(defaultBucketName).build());
            }
        } catch (Exception e) {
            log.error("初始化MinIO存储桶失败", e);
        }
    }
    
    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            return getFileUrl(bucketName, objectKey);
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
            
            return getFileUrl(bucketName, objectKey);
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public InputStream downloadFile(String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.error("从MinIO下载文件失败", e);
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    @Override
    public void deleteFile(String bucketName, String objectKey) {
        try {
            if (bucketName == null || bucketName.isEmpty()) {
                bucketName = defaultBucketName;
            }
            
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.error("从MinIO删除文件失败", e);
            throw new RuntimeException("文件删除失败", e);
        }
    }
    
    @Override
    public String getFileUrl(String bucketName, String objectKey) {
        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = defaultBucketName;
        }
        
        return endpoint + "/" + bucketName + "/" + objectKey;
    }
    
    @Override
    public StorageType getStorageType() {
        return StorageType.MINIO;
    }
}