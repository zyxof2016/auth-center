package com.auth.center.monitor.infrastructure.repository;

import com.auth.center.monitor.domain.entity.AlertRuleEntity;
import com.auth.center.monitor.domain.repository.AlertRuleRepository;
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
 * 告警规则仓库实现类
 */
@Repository
public class AlertRuleRepositoryImpl implements AlertRuleRepository {
    
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    
    public AlertRuleRepositoryImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }
    
    @Override
    public AlertRuleEntity save(AlertRuleEntity rule) {
        return elasticsearchTemplate.save(rule);
    }
    
    @Override
    public Optional<AlertRuleEntity> findById(Long id) {
        AlertRuleEntity entity = elasticsearchTemplate.get(String.valueOf(id), AlertRuleEntity.class);
        return Optional.ofNullable(entity);
    }
    
    @Override
    public List<AlertRuleEntity> findAll() {
        // 查询所有记录
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        
        List<SearchHit<AlertRuleEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRuleEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<AlertRuleEntity> findByRuleName(String ruleName) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(termQuery("ruleName", ruleName));
        
        List<SearchHit<AlertRuleEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRuleEntity.class)
                .getSearchHits();
        
        if (searchHits.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(searchHits.get(0).getContent());
    }
    
    @Override
    public List<AlertRuleEntity> findEnabledRules() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(termQuery("enabled", true));
        
        List<SearchHit<AlertRuleEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRuleEntity.class)
                .getSearchHits();
        
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<AlertRuleEntity> findByConditions(String ruleName, String ruleType, Boolean enabled, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery()
                        .filter(termQuery("ruleName", ruleName))
                        .filter(termQuery("ruleType", ruleType))
                        .filter(termQuery("enabled", enabled)))
                .withPageable(pageable);
        
        // 执行查询
        List<SearchHit<AlertRuleEntity>> searchHits = elasticsearchTemplate.search(queryBuilder.build(), AlertRuleEntity.class)
                .getSearchHits();
        
        // 转换为实体列表
        List<AlertRuleEntity> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        // 获取总数
        long total = elasticsearchTemplate.count(queryBuilder.build(), AlertRuleEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }
}