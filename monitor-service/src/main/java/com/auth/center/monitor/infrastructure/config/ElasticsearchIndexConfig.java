package com.auth.center.monitor.infrastructure.config;

import com.auth.center.monitor.domain.entity.AlertRecordEntity;
import com.auth.center.monitor.domain.entity.AlertRuleEntity;
import com.auth.center.monitor.domain.entity.ServiceHealthEntity;
import com.auth.center.monitor.domain.entity.SystemMetricsEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;

import javax.annotation.PostConstruct;

/**
 * Elasticsearch索引配置类
 */
@Configuration
public class ElasticsearchIndexConfig {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public ElasticsearchIndexConfig(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @PostConstruct
    public void initIndexes() {
        // 创建系统指标索引
        createIndexIfNotExists(SystemMetricsEntity.class);
        
        // 创建服务健康状态索引
        createIndexIfNotExists(ServiceHealthEntity.class);
        
        // 创建告警规则索引
        createIndexIfNotExists(AlertRuleEntity.class);
        
        // 创建告警记录索引
        createIndexIfNotExists(AlertRecordEntity.class);
    }
    
    private void createIndexIfNotExists(Class<?> clazz) {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(clazz);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping(clazz));
        }
    }
}