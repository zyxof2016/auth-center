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
}