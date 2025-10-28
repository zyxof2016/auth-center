package com.auth.center.monitor.interfaces.controller;

import com.auth.center.monitor.application.service.MonitorApplicationService;
import com.auth.center.monitor.domain.entity.*;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控管理控制器
 */
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final MonitorApplicationService monitorApplicationService;
    
    /**
     * 记录系统指标
     */
    @PostMapping("/metrics")
    public Response recordSystemMetrics(@RequestBody SystemMetricsEntity metrics) {
        return monitorApplicationService.recordSystemMetrics(metrics);
    }
    
    /**
     * 更新服务健康状态
     */
    @PostMapping("/health")
    public Response updateServiceHealth(@RequestBody ServiceHealthEntity health) {
        return monitorApplicationService.updateServiceHealth(health);
    }
    
    /**
     * 创建告警规则
     */
    @PostMapping("/alert/rule")
    public Response createAlertRule(@RequestBody AlertRuleEntity rule) {
        return monitorApplicationService.createAlertRule(rule);
    }
    
    /**
     * 记录告警
     */
    @PostMapping("/alert/record")
    public Response recordAlert(@RequestBody AlertRecordEntity alert) {
        return monitorApplicationService.recordAlert(alert);
    }
    
    /**
     * 获取系统指标历史数据
     */
    @GetMapping("/metrics/history")
    public PageResponse<SystemMetricsEntity> getSystemMetricsHistory(
            @RequestParam String serviceName,
            @RequestParam(required = false) String instanceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return monitorApplicationService.getSystemMetricsHistory(serviceName, instanceId, startTime, endTime, page, size);
    }
    
    /**
     * 获取服务健康状态列表
     */
    @GetMapping("/health/list")
    public List<ServiceHealthEntity> getServiceHealthList() {
        return monitorApplicationService.getServiceHealthList();
    }
    
    /**
     * 获取活跃告警列表
     */
    @GetMapping("/alert/active")
    public List<AlertRecordEntity> getActiveAlerts() {
        return monitorApplicationService.getActiveAlerts();
    }
    
    /**
     * 处理告警
     */
    @PutMapping("/alert/{alertId}/handle")
    public Response handleAlert(@PathVariable Long alertId, 
                               @RequestParam String handler,
                               @RequestParam(required = false) String remark) {
        return monitorApplicationService.handleAlert(alertId, handler, remark);
    }
}