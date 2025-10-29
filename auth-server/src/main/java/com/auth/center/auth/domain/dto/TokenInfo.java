package com.auth.center.auth.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Token信息
 */
@Data
public class TokenInfo implements Serializable {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private Long userId;
    private String tokenType;
    private Integer expiresIn;
}