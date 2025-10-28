package com.auth.center.file.application.service;

import com.auth.center.file.domain.entity.FileEntity;
import com.auth.center.file.domain.enums.FileCategory;
import com.auth.center.file.domain.enums.StorageType;
import com.auth.center.file.domain.repository.FileRepository;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件应用服务
 */
@Service
@RequiredArgsConstructor
public class FileApplicationService {
    
    private final FileRepository fileRepository;
    
    /**
     * 上传文件
     */
    public SingleResponse<FileEntity> uploadFile(FileEntity fileEntity) {
        // 生成文件唯一标识
        fileEntity.setFileId(UUID.randomUUID().toString());
        fileEntity.setCreatedTime(LocalDateTime.now());
        fileEntity.setUpdatedTime(LocalDateTime.now());
        fileEntity.setStatus(true);
        
        FileEntity savedFile = fileRepository.save(fileEntity);
        return SingleResponse.of(savedFile);
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
        
        return PageResponse.of(filePage.getContent(), filePage.getTotalElements(), page, size);
    }
    
    /**
     * 删除文件
     */
    public Response deleteFile(String fileId) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        fileRepository.delete(fileEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 更新文件信息
     */
    public SingleResponse<FileEntity> updateFileInfo(String fileId, FileEntity fileInfo) {
        FileEntity fileEntity = fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        fileEntity.setFileName(fileInfo.getFileName());
        fileEntity.setCategory(fileInfo.getCategory());
        fileEntity.setDescription(fileInfo.getDescription());
        fileEntity.setIsPublic(fileInfo.getIsPublic());
        fileEntity.setUpdatedTime(LocalDateTime.now());
        
        FileEntity updatedFile = fileRepository.save(fileEntity);
        return SingleResponse.of(updatedFile);
    }
    
    /**
     * 获取用户文件列表
     */
    public List<FileEntity> getUserFiles(Long tenantId, Long userId) {
        return fileRepository.findByUserId(tenantId, userId);
    }
}