package com.auth.center.monitor.infrastructure.repository;

import com.auth.center.monitor.domain.entity.AlertRecordEntity;
import com.auth.center.monitor.domain.repository.AlertRecordRepository;
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
 * 告警记录仓库实现类
 */
@Repository
public class AlertRecordRepositoryImpl implements AlertRecordRepository {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public AlertRecordRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @Override
    public AlertRecordEntity save(AlertRecordEntity alert) {
        return elasticsearchTemplate.save(alert);
    }
    
    @Override
    public Optional<AlertRecordEntity> findById(Long id) {
        AlertRecordEntity entity = elasticsearchTemplate.get(String.valueOf(id), AlertRecordEntity.class);
        return Optional.ofNullable(entity);
    }
    
    @Override
    public List<AlertRecordEntity> findActiveAlerts() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(termQuery("status", "ACTIVE"));
        
        List<SearchHit<AlertRecordEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRecordEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertRecordEntity> findByServiceName(String serviceName) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(termQuery("serviceName", serviceName));
        
        List<SearchHit<AlertRecordEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRecordEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertRecordEntity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(rangeQuery("alertTime").gte(startTime).lte(endTime));
        
        List<SearchHit<AlertRecordEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRecordEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<AlertRecordEntity> findByConditions(String serviceName, String severity, String status, 
                                                  LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("serviceName", serviceName))
                        .filter(termQuery("severity", severity))
                        .filter(termQuery("status", status))
                        .filter(rangeQuery("alertTime").gte(startTime).lte(endTime)))
                .withPageable(pageable);
        
        // 执行查询
        List<SearchHit<AlertRecordEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRecordEntity.class)
                .getSearchHits();
        
        // 转换为实体列表
        List<AlertRecordEntity> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        // 获取总数
        long total = elasticsearchTemplate.count(queryBuilder.build(), AlertRecordEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }
}