package com.auth.center.monitor.infrastructure.service;

import com.auth.center.monitor.domain.entity.AlertRecordEntity;
import com.auth.center.monitor.domain.entity.AlertRuleEntity;
import com.auth.center.monitor.domain.entity.SystemMetricsEntity;
import com.auth.center.monitor.domain.repository.AlertRecordRepository;
import com.auth.center.monitor.domain.repository.AlertRuleRepository;
import com.auth.center.monitor.domain.repository.SystemMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警评估服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEvaluationService {
    
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRecordRepository alertRecordRepository;
    private final SystemMetricsRepository systemMetricsRepository;
    
    /**
     * 定时评估告警规则
     */
    @Scheduled(fixedRate = 60000) // 每分钟评估一次
    public void evaluateAlertRules() {
        try {
            // 获取所有启用的告警规则
            List<AlertRuleEntity> rules = alertRuleRepository.findEnabledRules();
            
            for (AlertRuleEntity rule : rules) {
                evaluateRule(rule);
            }
            
            log.info("告警规则评估完成，共评估{}条规则", rules.size());
        } catch (Exception e) {
            log.error("评估告警规则时发生错误", e);
        }
    }
    
    /**
     * 评估单个告警规则
     */
    private void evaluateRule(AlertRuleEntity rule) {
        try {
            // 获取最新的系统指标
            List<SystemMetricsEntity> latestMetrics = systemMetricsRepository.findLatestMetrics(
                    "monitor-service", "instance-1", 1);
            
            if (latestMetrics.isEmpty()) {
                return;
            }
            
            SystemMetricsEntity metrics = latestMetrics.get(0);
            
            // 根据指标名称获取对应的值
            Double metricValue = getMetricValue(metrics, rule.getMetricName());
            if (metricValue == null) {
                return;
            }
            
            // 检查是否触发告警
            if (isAlertTriggered(metricValue, rule.getThreshold(), rule.getOperator())) {
                // 创建告警记录
                AlertRecordEntity alert = AlertRecordEntity.create(
                        rule.getTenantId(),
                        rule.getId(),
                        "指标告警: " + rule.getRuleName(),
                        "指标 " + rule.getMetricName() + " 当前值 " + metricValue + " 超过阈值 " + rule.getThreshold(),
                        rule.getSeverity(),
                        "monitor-service",
                        "instance-1",
                        metricValue
                );
                
                // 保存告警记录
                alertRecordRepository.save(alert);
                
                log.warn("触发告警: 规则={}, 指标={}, 当前值={}, 阈值={}", 
                        rule.getRuleName(), rule.getMetricName(), metricValue, rule.getThreshold());
            }
        } catch (Exception e) {
            log.error("评估告警规则时发生错误: 规则ID={}", rule.getId(), e);
        }
    }
    
    /**
     * 根据指标名称获取指标值
     */
    private Double getMetricValue(SystemMetricsEntity metrics, String metricName) {
        switch (metricName) {
            case "cpuUsage":
                return metrics.getCpuUsage();
            case "memoryUsage":
                return metrics.getMemoryUsage();
            case "threadCount":
                return (double) metrics.getThreadCount();
            case "heapMemoryUsed":
                return (double) metrics.getHeapMemoryUsed();
            default:
                return null;
        }
    }
    
    /**
     * 检查是否触发告警
     */
    private boolean isAlertTriggered(Double currentValue, Double threshold, String operator) {
        switch (operator) {
            case ">":
                return currentValue > threshold;
            case ">=":
                return currentValue >= threshold;
            case "<":
                return currentValue < threshold;
            case "<=":
                return currentValue <= threshold;
            case "==":
                return Math.abs(currentValue - threshold) < 0.0001;
            case "!=":
                return Math.abs(currentValue - threshold) >= 0.0001;
            default:
                return false;
        }
    }
}