package com.auth.center.log.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@Document(indexName = "login_log")
public class LoginLogEntity {
    
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
     * 登录类型
     */
    @Field(type = FieldType.Keyword)
    private String loginType;
    
    /**
     * 登录IP
     */
    @Field(type = FieldType.Keyword)
    private String loginIp;
    
    /**
     * 登录地点
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String loginLocation;
    
    /**
     * 用户代理
     */
    @Field(type = FieldType.Text)
    private String userAgent;
    
    /**
     * 状态
     */
    @Field(type = FieldType.Boolean)
    private Boolean status;
    
    /**
     * 失败原因
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String failReason;
    
    /**
     * 登录时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime loginTime;
    
    /**
     * 创建登录日志
     */
    public static LoginLogEntity create(Long tenantId, Long userId, String username, String loginType, 
                                      String loginIp, String loginLocation, String userAgent, 
                                      Boolean status, String failReason) {
        LoginLogEntity loginLog = new LoginLogEntity();
        loginLog.tenantId = tenantId;
        loginLog.userId = userId;
        loginLog.username = username;
        loginLog.loginType = loginType;
        loginLog.loginIp = loginIp;
        loginLog.loginLocation = loginLocation;
        loginLog.userAgent = userAgent;
        loginLog.status = status;
        loginLog.failReason = failReason;
        loginLog.loginTime = LocalDateTime.now();
        return loginLog;
    }
}