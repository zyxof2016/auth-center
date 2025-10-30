package com.auth.center.monitor.infrastructure.service;

import com.auth.center.monitor.domain.entity.SystemMetricsEntity;
import com.auth.center.monitor.domain.repository.SystemMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

/**
 * 系统指标收集服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsCollectionService {
    
    private final SystemMetricsRepository systemMetricsRepository;
    private final MetricsEndpoint metricsEndpoint;
    
    /**
     * 定时收集系统指标
     */
    @Scheduled(fixedRate = 30000) // 每30秒收集一次
    public void collectSystemMetrics() {
        try {
            // 获取JVM指标
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            
            // CPU使用率
            double cpuUsage = osBean.getSystemLoadAverage();
            
            // 内存使用情况
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();
            double memoryUsage = (double) heapUsed / heapMax;
            
            // 线程数
            int threadCount = threadBean.getThreadCount();
            
            // 创建系统指标实体
            SystemMetricsEntity metrics = SystemMetricsEntity.create(
                    1L, // 默认租户ID
                    "monitor-service", // 服务名称
                    "instance-1", // 实例ID
                    cpuUsage,
                    memoryUsage,
                    0.0, // 磁盘使用率需要通过其他方式获取
                    0.0, // 网络流入速率
                    0.0, // 网络流出速率
                    threadCount,
                    heapUsed,
                    memoryBean.getNonHeapMemoryUsage().getUsed(),
                    0L, // GC次数
                    0L  // GC时间
            );
            
            // 保存指标
            systemMetricsRepository.save(metrics);
            
            log.info("系统指标收集完成: CPU使用率={}, 内存使用率={}", cpuUsage, memoryUsage);
        } catch (Exception e) {
            log.error("收集系统指标时发生错误", e);
        }
    }
}