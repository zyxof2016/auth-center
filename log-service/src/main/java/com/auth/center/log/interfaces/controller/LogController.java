package com.auth.center.log.interfaces.controller;

import com.auth.center.log.application.dto.LoginLogDTO;
import com.auth.center.log.application.dto.OperationLogDTO;
import com.auth.center.log.application.service.LogApplicationService;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 日志管理控制器
 */
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {
    
    private final LogApplicationService logApplicationService;
    
    /**
     * 记录操作日志
     */
    @PostMapping("/operation")
    public Response recordOperationLog(@RequestBody OperationLogDTO operationLogDTO) {
        return logApplicationService.recordOperationLog(operationLogDTO);
    }
    
    /**
     * 记录登录日志
     */
    @PostMapping("/login")
    public Response recordLoginLog(@RequestBody LoginLogDTO loginLogDTO) {
        return logApplicationService.recordLoginLog(loginLogDTO);
    }
    
    /**
     * 分页查询操作日志
     */
    @GetMapping("/operation")
    public PageResponse<OperationLogDTO> getOperationLogPage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return logApplicationService.getOperationLogPage(tenantId, username, operationType, status, 
                startTime, endTime, page, size);
    }
    
    /**
     * 分页查询登录日志
     */
    @GetMapping("/login")
    public PageResponse<LoginLogDTO> getLoginLogPage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String loginType,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return logApplicationService.getLoginLogPage(tenantId, username, loginType, status, 
                startTime, endTime, page, size);
    }
    
    /**
     * 统计操作日志数量
     */
    @GetMapping("/operation/count")
    public Long countOperationLogs(
            @RequestParam Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return logApplicationService.countOperationLogs(tenantId, startTime, endTime);
    }
    
    /**
     * 统计登录日志数量
     */
    @GetMapping("/login/count")
    public Long countLoginLogs(
            @RequestParam Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return logApplicationService.countLoginLogs(tenantId, startTime, endTime);
    }
}