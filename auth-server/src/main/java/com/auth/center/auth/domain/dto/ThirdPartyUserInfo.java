package com.auth.center.auth.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方用户信息
 */
@Data
public class ThirdPartyUserInfo implements Serializable {
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String source;
    private String sourceId;
    private Object rawUserInfo;
}