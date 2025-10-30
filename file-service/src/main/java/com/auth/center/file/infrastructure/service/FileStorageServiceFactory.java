package com.auth.center.file.infrastructure.service;

import com.auth.center.file.domain.enums.StorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件存储服务工厂
 */
@Component
public class FileStorageServiceFactory {
    
    private final Map<StorageType, FileStorageService> storageServices;
    
    @Autowired
    public FileStorageServiceFactory(List<FileStorageService> services) {
        this.storageServices = services.stream()
                .collect(Collectors.toMap(FileStorageService::getStorageType, service -> service));
    }
    
    /**
     * 根据存储类型获取文件存储服务
     */
    public FileStorageService getFileStorageService(StorageType storageType) {
        return storageServices.get(storageType);
    }
    
    /**
     * 根据存储类型代码获取文件存储服务
     */
    public FileStorageService getFileStorageService(String storageTypeCode) {
        for (StorageType type : StorageType.values()) {
            if (type.getCode().equals(storageTypeCode)) {
                return storageServices.get(type);
            }
        }
        throw new IllegalArgumentException("Unsupported storage type: " + storageTypeCode);
    }
}