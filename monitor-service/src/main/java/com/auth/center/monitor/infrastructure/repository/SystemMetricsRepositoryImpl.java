package com.auth.center.monitor.infrastructure.repository;

import com.auth.center.monitor.domain.entity.SystemMetricsEntity;
import com.auth.center.monitor.domain.repository.SystemMetricsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 系统指标仓库实现类
 */
@Repository
public class SystemMetricsRepositoryImpl implements SystemMetricsRepository {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public SystemMetricsRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @Override
    public SystemMetricsEntity save(SystemMetricsEntity metrics) {
        return elasticsearchTemplate.save(metrics);
    }
    
    @Override
    public Optional<SystemMetricsEntity> findById(Long id) {
        SystemMetricsEntity entity = elasticsearchTemplate.get(String.valueOf(id), SystemMetricsEntity.class);
        return Optional.ofNullable(entity);
    }
    
    @Override
    public Page<SystemMetricsEntity> findByConditions(String serviceName, String instanceId, 
                                                     LocalDateTime startTime, LocalDateTime endTime, 
                                                     Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("serviceName", serviceName))
                        .filter(termQuery("instanceId", instanceId))
                        .filter(rangeQuery("collectTime").gte(startTime).lte(endTime)))
                .withPageable(pageable);
        
        // 执行查询
        List<SearchHit<SystemMetricsEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), SystemMetricsEntity.class)
                .getSearchHits();
        
        // 转换为实体列表
        List<SystemMetricsEntity> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        // 获取总数
        long total = elasticsearchTemplate.count(queryBuilder.build(), SystemMetricsEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }
    
    @Override
    public List<SystemMetricsEntity> findLatestMetrics(String serviceName, String instanceId, int limit) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("serviceName", serviceName))
                        .filter(termQuery("instanceId", instanceId)))
                .withSort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "collectTime"))
                .withPageable(org.springframework.data.domain.PageRequest.of(0, limit));
        
        List<SearchHit<SystemMetricsEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), SystemMetricsEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}