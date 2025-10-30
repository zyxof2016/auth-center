package com.auth.center.monitor.infrastructure.repository;

import com.auth.center.monitor.domain.entity.ServiceHealthEntity;
import com.auth.center.monitor.domain.repository.ServiceHealthRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 服务健康状态仓库实现类
 */
@Repository
public class ServiceHealthRepositoryImpl implements ServiceHealthRepository {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public ServiceHealthRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @Override
    public ServiceHealthEntity save(ServiceHealthEntity health) {
        return elasticsearchTemplate.save(health);
    }
    
    @Override
    public Optional<ServiceHealthEntity> findById(Long id) {
        ServiceHealthEntity entity = elasticsearchTemplate.get(String.valueOf(id), ServiceHealthEntity.class);
        return Optional.ofNullable(entity);
    }
    
    @Override
    public List<ServiceHealthEntity> findAll() {
        // 查询所有记录
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        
        List<SearchHit<ServiceHealthEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ServiceHealthEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ServiceHealthEntity> findByServiceNameAndInstanceId(String serviceName, String instanceId) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("serviceName", serviceName))
                        .filter(termQuery("instanceId", instanceId)));
        
        List<SearchHit<ServiceHealthEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ServiceHealthEntity.class)
                .getSearchHits();
        
        if (searchHits.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(searchHits.get(0).getContent());
    }
    
    @Override
    public List<ServiceHealthEntity> findByServiceName(String serviceName) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(termQuery("serviceName", serviceName));
        
        List<SearchHit<ServiceHealthEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ServiceHealthEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<ServiceHealthEntity> findByConditions(String serviceName, String status, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("serviceName", serviceName))
                        .filter(termQuery("status", status)))
                .withPageable(pageable);
        
        // 执行查询
        List<SearchHit<ServiceHealthEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ServiceHealthEntity.class)
                .getSearchHits();
        
        // 转换为实体列表
        List<ServiceHealthEntity> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        // 获取总数
        long total = elasticsearchTemplate.count(queryBuilder.build(), ServiceHealthEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }
}