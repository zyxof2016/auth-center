package com.auth.center.client.domain.entity;

import com.auth.center.client.domain.enums.ClientStatus;
import com.auth.center.client.domain.enums.ClientType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户端实体
 */
@Data
public class ClientEntity {
    
    /**
     * 客户端ID
     */
    private Long id;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 客户端标识
     */
    private String clientId;
    
    /**
     * 客户端密钥
     */
    private String clientSecret;
    
    /**
     * 客户端名称
     */
    private String clientName;
    
    /**
     * 客户端类型
     */
    private ClientType clientType;
    
    /**
     * 授权类型
     */
    private String authorizedGrantTypes;
    
    /**
     * 重定向URI
     */
    private String redirectUris;
    
    /**
     * 权限范围
     */
    private String scope;
    
    /**
     * 访问令牌有效期(秒)
     */
    private Integer accessTokenValidity;
    
    /**
     * 刷新令牌有效期(秒)
     */
    private Integer refreshTokenValidity;
    
    /**
     * 是否自动授权
     */
    private Boolean autoApprove;
    
    /**
     * 状态
     */
    private ClientStatus status;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}