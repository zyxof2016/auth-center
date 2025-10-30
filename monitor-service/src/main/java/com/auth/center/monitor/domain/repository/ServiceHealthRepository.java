package com.auth.center.monitor.domain.repository;

import com.auth.center.monitor.domain.entity.ServiceHealthEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 服务健康状态仓库接口
 */
public interface ServiceHealthRepository {
    
    /**
     * 保存服务健康状态
     */
    ServiceHealthEntity save(ServiceHealthEntity health);
    
    /**
     * 根据ID查找服务健康状态
     */
    Optional<ServiceHealthEntity> findById(Long id);
    
    /**
     * 查找所有服务健康状态
     */
    List<ServiceHealthEntity> findAll();
    
    /**
     * 根据服务名称和实例ID查找服务健康状态
     */
    Optional<ServiceHealthEntity> findByServiceNameAndInstanceId(String serviceName, String instanceId);
    
    /**
     * 根据服务名称查找服务健康状态列表
     */
    List<ServiceHealthEntity> findByServiceName(String serviceName);
    
    /**
     * 分页查询服务健康状态
     */
    Page<ServiceHealthEntity> findByConditions(String serviceName, String status, Pageable pageable);
}