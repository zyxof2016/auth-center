package com.auth.center.file.application.service;

import com.auth.center.file.domain.entity.FileEntity;
import com.auth.center.file.domain.enums.FileCategory;
import com.auth.center.file.domain.enums.StorageType;
import com.auth.center.file.domain.repository.FileRepository;
import com.auth.center.file.infrastructure.service.FileStorageService;
import com.auth.center.file.infrastructure.service.FileStorageServiceFactory;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 文件应用服务
 */
@Service
@RequiredArgsConstructor
public class FileApplicationService {
    
    private final FileRepository fileRepository;
    private final FileStorageServiceFactory fileStorageServiceFactory;
    
    /**
     * 上传文件
     */
    public SingleResponse<FileEntity> uploadFile(MultipartFile file, Long tenantId, Long userId, 
                                               String username, String category, Boolean isPublic,
                                               String storageTypeCode) {
        try {
            // 生成文件唯一标识
            String fileId = UUID.randomUUID().toString();
            String objectKey = tenantId + "/" + userId + "/" + fileId + "_" + file.getOriginalFilename();
            
            // 获取文件存储服务
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService(storageTypeCode);
            
            // 上传文件到存储服务
            String fileUrl = storageService.uploadFile(file, null, objectKey);
            
            // 创建文件实体
            FileEntity fileEntity = FileEntity.create(
                    tenantId,
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType(),
                    getFileExtension(file.getOriginalFilename()),
                    storageTypeCode,
                    null, // bucketName
                    objectKey,
                    fileUrl,
                    category,
                    isPublic,
                    userId,
                    username,
                    null, // metadata
                    null  // description
            );
            fileEntity.setFileId(fileId);
            
            FileEntity savedFile = fileRepository.save(fileEntity);
            return SingleResponse.of(savedFile);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    /**
     * 获取文件信息
     */
    public SingleResponse<FileEntity> getFileInfo(String fileId) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        return SingleResponse.of(fileEntity);
    }
    
    /**
     * 分页查询文件列表
     */
    public PageResponse<FileEntity> getFilePage(Long tenantId, String fileName, FileCategory category,
                                              StorageType storageType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<FileEntity> filePage = fileRepository.findByConditions(
                tenantId, fileName, category, storageType, pageRequest);
        
        return PageResponse.of(filePage.getContent(), page, size, filePage.getTotalElements());
    }
    
    /**
     * 删除文件
     */
    public Response deleteFile(String fileId) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        try {
            // 从存储服务删除文件
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService(fileEntity.getStorageType());
            storageService.deleteFile(fileEntity.getBucketName(), fileEntity.getObjectKey());
            
            // 从数据库删除文件信息
            fileEntity.delete();
            fileRepository.save(fileEntity);
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
        
        return Response.buildSuccess();
    }
    
    /**
     * 更新文件信息
     */
    public SingleResponse<FileEntity> updateFileInfo(String fileId, String fileName, String category, 
                                                   String description, Boolean isPublic) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        fileEntity.update(fileName, category, description, isPublic);
        FileEntity updatedFile = fileRepository.save(fileEntity);
        return SingleResponse.of(updatedFile);
    }
    
    /**
     * 获取用户文件列表
     */
    public List<FileEntity> getUserFiles(Long tenantId, Long userId) {
        return fileRepository.findByUserId(tenantId, userId);
    }
    
    /**
     * 下载文件
     */
    public byte[] downloadFile(String fileId) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        try {
            FileStorageService storageService = fileStorageServiceFactory.getFileStorageService(fileEntity.getStorageType());
            InputStream inputStream = storageService.downloadFile(fileEntity.getBucketName(), fileEntity.getObjectKey());
            return inputStreamToByteArray(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    /**
     * 获取文件访问URL
     */
    public String getFileAccessUrl(String fileId) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        return fileEntity.getAccessUrl();
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 将InputStream转换为byte数组
     */
    private byte[] inputStreamToByteArray(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}