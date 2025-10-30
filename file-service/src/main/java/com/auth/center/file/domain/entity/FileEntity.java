package com.auth.center.file.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 */
@Data
public class FileEntity {
    
    /**
     * 文件ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 文件唯一标识
     */
    private String fileId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 存储类型
     */
    private String storageType;
    
    /**
     * 存储桶名称
     */
    private String bucketName;
    
    /**
     * 对象键
     */
    private String objectKey;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件分类
     */
    private String category;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    /**
     * 上传用户ID
     */
    private Long uploadUserId;
    
    /**
     * 上传用户名
     */
    private String uploadUsername;
    
    /**
     * 文件元数据
     */
    private String metadata;
    
    /**
     * 文件描述
     */
    private String description;
    
    /**
     * 状态
     */
    private Boolean status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 创建文件信息
     */
    public static FileEntity create(Long tenantId, String fileName, Long fileSize, String fileType,
                                  String fileExtension, String storageType, String bucketName,
                                  String objectKey, String fileUrl, String category,
                                  Boolean isPublic, Long uploadUserId, String uploadUsername,
                                  String metadata, String description) {
        FileEntity file = new FileEntity();
        file.tenantId = tenantId;
        file.fileName = fileName;
        file.fileSize = fileSize;
        file.fileType = fileType;
        file.fileExtension = fileExtension;
        file.storageType = storageType;
        file.bucketName = bucketName;
        file.objectKey = objectKey;
        file.fileUrl = fileUrl;
        file.category = category;
        file.isPublic = isPublic;
        file.uploadUserId = uploadUserId;
        file.uploadUsername = uploadUsername;
        file.metadata = metadata;
        file.description = description;
        file.status = true;
        file.createdTime = LocalDateTime.now();
        file.updatedTime = LocalDateTime.now();
        return file;
    }
    
    /**
     * 更新文件信息
     */
    public void update(String fileName, String category, String description, Boolean isPublic) {
        this.fileName = fileName;
        this.category = category;
        this.description = description;
        this.isPublic = isPublic;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 删除文件
     */
    public void delete() {
        this.status = false;
        this.updatedTime = LocalDateTime.now();
    }
    
    /**
     * 获取文件访问URL
     */
    public String getAccessUrl() {
        return this.fileUrl;
    }
    
    /**
     * 检查文件是否公开
     */
    public boolean isPubliclyAccessible() {
        return this.isPublic != null && this.isPublic;
    }
}