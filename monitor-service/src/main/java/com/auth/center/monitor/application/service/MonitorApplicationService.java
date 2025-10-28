package com.auth.center.monitor.application.service;

import com.auth.center.monitor.domain.entity.*;
import com.auth.center.monitor.domain.repository.*;
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
 * 监控应用服务
 */
@Service
@RequiredArgsConstructor
public class MonitorApplicationService {
    
    private final SystemMetricsRepository systemMetricsRepository;
    private final ServiceHealthRepository serviceHealthRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRecordRepository alertRecordRepository;
    
    /**
     * 记录系统指标
     */
    public Response recordSystemMetrics(SystemMetricsEntity metrics) {
        metrics.setCollectTime(LocalDateTime.now());
        systemMetricsRepository.save(metrics);
        return Response.buildSuccess();
    }
    
    /**
     * 更新服务健康状态
     */
    public Response updateServiceHealth(ServiceHealthEntity health) {
        health.setLastCheckTime(LocalDateTime.now());
        serviceHealthRepository.save(health);
        return Response.buildSuccess();
    }
    
    /**
     * 创建告警规则
     */
    public Response createAlertRule(AlertRuleEntity rule) {
        rule.setCreatedTime(LocalDateTime.now());
        rule.setUpdatedTime(LocalDateTime.now());
        alertRuleRepository.save(rule);
        return Response.buildSuccess();
    }
    
    /**
     * 记录告警
     */
    public Response recordAlert(AlertRecordEntity alert) {
        alert.setAlertTime(LocalDateTime.now());
        alertRecordRepository.save(alert);
        return Response.buildSuccess();
    }
    
    /**
     * 获取系统指标历史数据
     */
    public PageResponse<SystemMetricsEntity> getSystemMetricsHistory(
            String serviceName, String instanceId, LocalDateTime startTime, LocalDateTime endTime,
            int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<SystemMetricsEntity> metricsPage = systemMetricsRepository.findByConditions(
                serviceName, instanceId, startTime, endTime, pageRequest);
        
        return PageResponse.of(metricsPage.getContent(), metricsPage.getTotalElements(), page, size);
    }
    
    /**
     * 获取服务健康状态列表
     */
    public List<ServiceHealthEntity> getServiceHealthList() {
        return serviceHealthRepository.findAll();
    }
    
    /**
     * 获取活跃告警列表
     */
    public List<AlertRecordEntity> getActiveAlerts() {
        return alertRecordRepository.findActiveAlerts();
    }
    
    /**
     * 处理告警
     */
    public Response handleAlert(Long alertId, String handler, String remark) {
        AlertRecordEntity alert = alertRecordRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("告警记录不存在"));
        
        alert.setStatus("HANDLED");
        alert.setHandler(handler);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleRemark(remark);
        alertRecordRepository.save(alert);
        
        return Response.buildSuccess();
    }
}