package com.auth.center.file.interfaces.controller;

import com.auth.center.file.application.service.FileApplicationService;
import com.auth.center.file.domain.entity.FileEntity;
import com.auth.center.file.domain.enums.FileCategory;
import com.auth.center.file.domain.enums.StorageType;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileApplicationService fileApplicationService;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public SingleResponse<FileEntity> uploadFile(@RequestBody FileEntity fileEntity) {
        return fileApplicationService.uploadFile(fileEntity);
    }
    
    /**
     * 获取文件信息
     */
    @GetMapping("/info/{fileId}")
    public SingleResponse<FileEntity> getFileInfo(@PathVariable String fileId) {
        return fileApplicationService.getFileInfo(fileId);
    }
    
    /**
     * 分页查询文件列表
     */
    @GetMapping
    public PageResponse<FileEntity> getFilePage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) FileCategory category,
            @RequestParam(required = false) StorageType storageType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return fileApplicationService.getFilePage(tenantId, fileName, category, storageType, page, size);
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public Response deleteFile(@PathVariable String fileId) {
        return fileApplicationService.deleteFile(fileId);
    }
    
    /**
     * 更新文件信息
     */
    @PutMapping("/{fileId}")
    public SingleResponse<FileEntity> updateFileInfo(@PathVariable String fileId, @RequestBody FileEntity fileInfo) {
        return fileApplicationService.updateFileInfo(fileId, fileInfo);
    }
    
    /**
     * 获取用户文件列表
     */
    @GetMapping("/user/{userId}")
    public List<FileEntity> getUserFiles(@RequestParam Long tenantId, @PathVariable Long userId) {
        return fileApplicationService.getUserFiles(tenantId, userId);
    }
}