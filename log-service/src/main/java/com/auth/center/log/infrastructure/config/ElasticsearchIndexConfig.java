package com.auth.center.log.infrastructure.config;

import com.auth.center.log.domain.entity.LoginLogEntity;
import com.auth.center.log.domain.entity.OperationLogEntity;
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
        // 创建登录日志索引
        createIndexIfNotExists(LoginLogEntity.class);
        
        // 创建操作日志索引
        createIndexIfNotExists(OperationLogEntity.class);
    }
    
    private void createIndexIfNotExists(Class<?> clazz) {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(clazz);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping(clazz));
        }
    }
}