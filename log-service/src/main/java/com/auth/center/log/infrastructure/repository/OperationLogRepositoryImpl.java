package com.auth.center.log.infrastructure.repository;

import com.auth.center.log.domain.entity.OperationLogEntity;
import com.auth.center.log.domain.repository.OperationLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 操作日志仓库实现类
 */
@Repository
public class OperationLogRepositoryImpl implements OperationLogRepository {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public OperationLogRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @Override
    public OperationLogEntity save(OperationLogEntity operationLogEntity) {
        return elasticsearchTemplate.save(operationLogEntity);
    }
    
    @Override
    public Page<OperationLogEntity> findByConditions(Long tenantId, String username, String operationType, 
                                                   Boolean status, LocalDateTime startTime, LocalDateTime endTime, 
                                                   Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .must(termQuery("tenantId", tenantId))
                        .filter(rangeQuery("createdTime").gte(startTime).lte(endTime))
                        .filter(termQuery("username", username))
                        .filter(termQuery("operationType", operationType))
                        .filter(termQuery("status", status)))
                .withPageable(pageable);
        
        // 执行查询
        List<SearchHit<OperationLogEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), OperationLogEntity.class)
                .getSearchHits();
        
        // 转换为实体列表
        List<OperationLogEntity> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        // 获取总数
        long total = elasticsearchTemplate.count(queryBuilder.build(), OperationLogEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }
    
    @Override
    public Long countByTimeRange(Long tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .must(termQuery("tenantId", tenantId))
                        .filter(rangeQuery("createdTime").gte(startTime).lte(endTime)));
        
        return elasticsearchTemplate.count(queryBuilder.build(), OperationLogEntity.class);
    }
}