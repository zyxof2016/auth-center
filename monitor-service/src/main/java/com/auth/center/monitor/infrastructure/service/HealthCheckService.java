package com.auth.center.monitor.infrastructure.service;

import com.auth.center.monitor.domain.entity.ServiceHealthEntity;
import com.auth.center.monitor.domain.repository.ServiceHealthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 服务健康检查服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {
    
    private final ServiceHealthRepository serviceHealthRepository;
    private final RestTemplate restTemplate;
    
    /**
     * 定时检查服务健康状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkServiceHealth() {
        try {
            // 这里应该从配置或注册中心获取服务列表
            // 为了演示，我们检查自身服务
            checkService("monitor-service", "instance-1", "http://localhost:8080/actuator/health");
            
            log.info("服务健康检查完成");
        } catch (Exception e) {
            log.error("服务健康检查时发生错误", e);
        }
    }
    
    /**
     * 检查单个服务的健康状态
     */
    private void checkService(String serviceName, String instanceId, String healthUrl) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 发送健康检查请求
            String response = restTemplate.getForObject(healthUrl, String.class);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 创建或更新服务健康状态
            ServiceHealthEntity health = serviceHealthRepository.findByServiceNameAndInstanceId(serviceName, instanceId)
                    .orElse(ServiceHealthEntity.create(1L, serviceName, instanceId, "HEALTHY", healthUrl, responseTime, null));
            
            health.update("HEALTHY", responseTime, null);
            serviceHealthRepository.save(health);
            
            log.debug("服务健康检查成功: 服务={}, 实例={}, 响应时间={}ms", serviceName, instanceId, responseTime);
        } catch (Exception e) {
            // 检查失败，记录错误信息
            ServiceHealthEntity health = serviceHealthRepository.findByServiceNameAndInstanceId(serviceName, instanceId)
                    .orElse(ServiceHealthEntity.create(1L, serviceName, instanceId, "ERROR", healthUrl, 0L, e.getMessage()));
            
            health.update("ERROR", 0L, e.getMessage());
            serviceHealthRepository.save(health);
            
            log.warn("服务健康检查失败: 服务={}, 实例={}, 错误={}", serviceName, instanceId, e.getMessage());
        }
    }
}