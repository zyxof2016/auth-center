package com.auth.center.log.application.service;

import com.auth.center.log.application.dto.LoginLogDTO;
import com.auth.center.log.application.dto.OperationLogDTO;
import com.auth.center.log.domain.entity.LoginLogEntity;
import com.auth.center.log.domain.entity.OperationLogEntity;
import com.auth.center.log.domain.repository.LoginLogRepository;
import com.auth.center.log.domain.repository.OperationLogRepository;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志应用服务
 */
@Service
@RequiredArgsConstructor
public class LogApplicationService {
    
    private final OperationLogRepository operationLogRepository;
    private final LoginLogRepository loginLogRepository;
    
    /**
     * 记录操作日志
     */
    public Response recordOperationLog(OperationLogDTO operationLogDTO) {
        OperationLogEntity operationLogEntity = convertToOperationEntity(operationLogDTO);
        operationLogEntity.setCreatedTime(LocalDateTime.now());
        operationLogRepository.save(operationLogEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 记录登录日志
     */
    public Response recordLoginLog(LoginLogDTO loginLogDTO) {
        LoginLogEntity loginLogEntity = convertToLoginEntity(loginLogDTO);
        loginLogEntity.setLoginTime(LocalDateTime.now());
        loginLogRepository.save(loginLogEntity);
        return Response.buildSuccess();
    }
    
    /**
     * 分页查询操作日志
     */
    public PageResponse<OperationLogDTO> getOperationLogPage(Long tenantId, String username, 
                                                            String operationType, Boolean status,
                                                            LocalDateTime startTime, LocalDateTime endTime,
                                                            int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<OperationLogEntity> logPage = operationLogRepository.findByConditions(
                tenantId, username, operationType, status, startTime, endTime, pageRequest);
        
        List<OperationLogDTO> logDTOs = logPage.getContent().stream()
                .map(this::convertToOperationDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(logDTOs, logPage.getTotalElements(), page, size);
    }
    
    /**
     * 分页查询登录日志
     */
    public PageResponse<LoginLogDTO> getLoginLogPage(Long tenantId, String username, 
                                                    String loginType, Boolean status,
                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                    int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<LoginLogEntity> logPage = loginLogRepository.findByConditions(
                tenantId, username, loginType, status, startTime, endTime, pageRequest);
        
        List<LoginLogDTO> logDTOs = logPage.getContent().stream()
                .map(this::convertToLoginDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(logDTOs, logPage.getTotalElements(), page, size);
    }
    
    /**
     * 统计操作日志数量
     */
    public Long countOperationLogs(Long tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogRepository.countByTimeRange(tenantId, startTime, endTime);
    }
    
    /**
     * 统计登录日志数量
     */
    public Long countLoginLogs(Long tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        return loginLogRepository.countByTimeRange(tenantId, startTime, endTime);
    }
    
    private OperationLogEntity convertToOperationEntity(OperationLogDTO dto) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setUserId(dto.getUserId());
        entity.setUsername(dto.getUsername());
        entity.setOperationType(dto.getOperationType());
        entity.setOperationModule(dto.getOperationModule());
        entity.setOperationDesc(dto.getOperationDesc());
        entity.setRequestMethod(dto.getRequestMethod());
        entity.setRequestUrl(dto.getRequestUrl());
        entity.setIpAddress(dto.getIpAddress());
        entity.setExecuteTime(dto.getExecuteTime());
        entity.setStatus(dto.getStatus());
        entity.setErrorMessage(dto.getErrorMessage());
        return entity;
    }
    
    private LoginLogEntity convertToLoginEntity(LoginLogDTO dto) {
        LoginLogEntity entity = new LoginLogEntity();
        entity.setId(dto.getId());
        entity.setTenantId(dto.getTenantId());
        entity.setUserId(dto.getUserId());
        entity.setUsername(dto.getUsername());
        entity.setLoginType(dto.getLoginType());
        entity.setLoginIp(dto.getLoginIp());
        entity.setLoginLocation(dto.getLoginLocation());
        entity.setStatus(dto.getStatus());
        entity.setFailReason(dto.getFailReason());
        return entity;
    }
    
    private OperationLogDTO convertToOperationDTO(OperationLogEntity entity) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setOperationType(entity.getOperationType());
        dto.setOperationModule(entity.getOperationModule());
        dto.setOperationDesc(entity.getOperationDesc());
        dto.setRequestMethod(entity.getRequestMethod());
        dto.setRequestUrl(entity.getRequestUrl());
        dto.setIpAddress(entity.getIpAddress());
        dto.setExecuteTime(entity.getExecuteTime());
        dto.setStatus(entity.getStatus());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setCreatedTime(entity.getCreatedTime());
        return dto;
    }
    
    private LoginLogDTO convertToLoginDTO(LoginLogEntity entity) {
        LoginLogDTO dto = new LoginLogDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setLoginType(entity.getLoginType());
        dto.setLoginIp(entity.getLoginIp());
        dto.setLoginLocation(entity.getLoginLocation());
        dto.setStatus(entity.getStatus());
        dto.setFailReason(entity.getFailReason());
        dto.setLoginTime(entity.getLoginTime());
        return dto;
    }
}