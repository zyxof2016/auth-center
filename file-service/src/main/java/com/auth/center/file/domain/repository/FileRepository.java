package com.auth.center.file.domain.repository;

import com.auth.center.file.domain.entity.FileEntity;
import com.auth.center.file.domain.enums.FileCategory;
import com.auth.center.file.domain.enums.StorageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 文件仓库接口
 */
public interface FileRepository {
    
    /**
     * 保存文件信息
     */
    FileEntity save(FileEntity fileEntity);
    
    /**
     * 根据ID查找文件信息
     */
    Optional<FileEntity> findById(Long id);
    
    /**
     * 根据文件唯一标识查找文件信息
     */
    Optional<FileEntity> findByFileId(String fileId);
    
    /**
     * 根据条件分页查询文件
     */
    Page<FileEntity> findByConditions(Long tenantId, String fileName, FileCategory category,
                                    StorageType storageType, Pageable pageable);
    
    /**
     * 根据用户ID查询文件列表
     */
    List<FileEntity> findByUserId(Long tenantId, Long userId);
    
    /**
     * 删除文件信息
     */
    void delete(FileEntity fileEntity);
}