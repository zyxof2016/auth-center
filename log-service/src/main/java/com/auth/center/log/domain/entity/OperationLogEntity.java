package com.auth.center.log.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@Document(indexName = "operation_log")
public class OperationLogEntity {
    
    /**
     * 日志ID
     */
    @Id
    private Long id;
    
    /**
     * 租户ID
     */
    @Field(type = FieldType.Long)
    private Long tenantId;
    
    /**
     * 用户ID
     */
    @Field(type = FieldType.Long)
    private Long userId;
    
    /**
     * 用户名
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String username;
    
    /**
     * 操作类型
     */
    @Field(type = FieldType.Keyword)
    private String operationType;
    
    /**
     * 操作模块
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String operationModule;
    
    /**
     * 操作描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String operationDesc;
    
    /**
     * 请求方法
     */
    @Field(type = FieldType.Keyword)
    private String requestMethod;
    
    /**
     * 请求URL
     */
    @Field(type = FieldType.Keyword)
    private String requestUrl;
    
    /**
     * 请求参数
     */
    @Field(type = FieldType.Text)
    private String requestParams;
    
    /**
     * 响应结果
     */
    @Field(type = FieldType.Text)
    private String responseResult;
    
    /**
     * IP地址
     */
    @Field(type = FieldType.Keyword)
    private String ipAddress;
    
    /**
     * 用户代理
     */
    @Field(type = FieldType.Text)
    private String userAgent;
    
    /**
     * 执行时间(毫秒)
     */
    @Field(type = FieldType.Long)
    private Long executeTime;
    
    /**
     * 状态
     */
    @Field(type = FieldType.Boolean)
    private Boolean status;
    
    /**
     * 错误信息
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String errorMessage;
    
    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdTime;
    
    /**
     * 创建操作日志
     */
    public static OperationLogEntity create(Long tenantId, Long userId, String username, String operationType,
                                          String operationModule, String operationDesc, String requestMethod,
                                          String requestUrl, String requestParams, String responseResult,
                                          String ipAddress, String userAgent, Long executeTime,
                                          Boolean status, String errorMessage) {
        OperationLogEntity operationLog = new OperationLogEntity();
        operationLog.tenantId = tenantId;
        operationLog.userId = userId;
        operationLog.username = username;
        operationLog.operationType = operationType;
        operationLog.operationModule = operationModule;
        operationLog.operationDesc = operationDesc;
        operationLog.requestMethod = requestMethod;
        operationLog.requestUrl = requestUrl;
        operationLog.requestParams = requestParams;
        operationLog.responseResult = responseResult;
        operationLog.ipAddress = ipAddress;
        operationLog.userAgent = userAgent;
        operationLog.executeTime = executeTime;
        operationLog.status = status;
        operationLog.errorMessage = errorMessage;
        operationLog.createdTime = LocalDateTime.now();
        return operationLog;
    }
}