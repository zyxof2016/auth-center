package com.auth.center.file.infrastructure.repository;

import com.auth.center.file.domain.entity.FileEntity;
import com.auth.center.file.domain.enums.FileCategory;
import com.auth.center.file.domain.enums.StorageType;
import com.auth.center.file.domain.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 文件仓库实现类
 * 注意：这是一个简化的内存实现，实际项目中应该使用数据库存储
 */
@Repository
public class FileRepositoryImpl implements FileRepository {
    
    // 使用内存存储模拟数据库（实际项目中应该使用真实的数据库）
    private final ConcurrentHashMap<Long, FileEntity> fileStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FileEntity> fileIdIndex = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public FileEntity save(FileEntity fileEntity) {
        if (fileEntity.getId() == null) {
            fileEntity.setId(idGenerator.getAndIncrement());
        }
        fileStorage.put(fileEntity.getId(), fileEntity);
        fileIdIndex.put(fileEntity.getFileId(), fileEntity);
        return fileEntity;
    }
    
    @Override
    public Optional<FileEntity> findById(Long id) {
        return Optional.ofNullable(fileStorage.get(id));
    }
    
    @Override
    public Optional<FileEntity> findByFileId(String fileId) {
        return Optional.ofNullable(fileIdIndex.get(fileId));
    }
    
    @Override
    public Page<FileEntity> findByConditions(Long tenantId, String fileName, FileCategory category,
                                           StorageType storageType, Pageable pageable) {
        List<FileEntity> filteredFiles = fileStorage.values().stream()
                .filter(file -> file.getTenantId().equals(tenantId))
                .filter(file -> fileName == null || file.getFileName().contains(fileName))
                .filter(file -> category == null || file.getCategory().equals(category.getCode()))
                .filter(file -> storageType == null || file.getStorageType().equals(storageType.getCode()))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        long total = fileStorage.values().stream()
                .filter(file -> file.getTenantId().equals(tenantId))
                .filter(file -> fileName == null || file.getFileName().contains(fileName))
                .filter(file -> category == null || file.getCategory().equals(category.getCode()))
                .filter(file -> storageType == null || file.getStorageType().equals(storageType.getCode()))
                .count();
        
        return new PageImpl<>(filteredFiles, pageable, total);
    }
    
    @Override
    public List<FileEntity> findByUserId(Long tenantId, Long userId) {
        return fileStorage.values().stream()
                .filter(file -> file.getTenantId().equals(tenantId))
                .filter(file -> file.getUploadUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(FileEntity fileEntity) {
        fileStorage.remove(fileEntity.getId());
        fileIdIndex.remove(fileEntity.getFileId());
    }
}