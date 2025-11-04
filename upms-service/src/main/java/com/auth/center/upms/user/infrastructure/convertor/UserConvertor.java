package com.auth.center.user.infrastructure.convertor;

import com.auth.center.user.domain.entity.User;
import com.auth.center.user.infrastructure.persistence.po.UserPO;
import org.springframework.stereotype.Component;

/**
 * 用户实体转换器（防腐层）
 */
@Component
public class UserConvertor {
    
    /**
     * 将领域实体转换为持久化对象
     *
     * @param user 领域实体
     * @return 持久化对象
     */
    public UserPO toPO(User user) {
        if (user == null) {
            return null;
        }

        UserPO userPO = new UserPO();
        userPO.setId(user.getId());
        userPO.setTenantId(user.getTenantId());
        userPO.setUsername(user.getUsername());
        userPO.setPassword(user.getPassword());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setRealName(user.getRealName());
        userPO.setNickname(user.getNickname());
        userPO.setAvatar(user.getAvatar());
        userPO.setGender(user.getGender());
        userPO.setBirthday(user.getBirthday());
        userPO.setStatus(user.getStatus() != null ? user.getStatus().getCode() : null);
        userPO.setUserType(user.getUserType() != null ? user.getUserType().getCode() : null);
        userPO.setLastLoginTime(user.getLastLoginTime());
        userPO.setLastLoginIp(user.getLastLoginIp());
        userPO.setLoginFailCount(user.getLoginFailCount());
        userPO.setPwdUpdateTime(user.getPwdUpdateTime());
        userPO.setCreatedBy(user.getCreatedBy());
        userPO.setCreatedTime(user.getCreatedTime());
        userPO.setUpdatedBy(user.getUpdatedBy());
        userPO.setUpdatedTime(user.getUpdatedTime());

        return userPO;
    }

    /**
     * 将持久化对象转换为领域实体
     *
     * @param userPO 持久化对象
     * @return 领域实体
     */
    public User toDomain(UserPO userPO) {
        if (userPO == null) {
            return null;
        }

        User user = new User();
        user.setId(userPO.getId());
        user.setTenantId(userPO.getTenantId());
        user.setUsername(userPO.getUsername());
        user.setPassword(userPO.getPassword());
        user.setEmail(userPO.getEmail());
        user.setPhone(userPO.getPhone());
        user.setRealName(userPO.getRealName());
        user.setNickname(userPO.getNickname());
        user.setAvatar(userPO.getAvatar());
        user.setGender(userPO.getGender());
        user.setBirthday(userPO.getBirthday());
        // 需要根据code转换为枚举
        user.setStatus(userPO.getStatus() != null ? com.auth.center.user.domain.enums.UserStatus.fromCode(userPO.getStatus()) : null);
        user.setUserType(userPO.getUserType() != null ? com.auth.center.user.domain.enums.UserType.fromCode(userPO.getUserType()) : null);
        user.setLastLoginTime(userPO.getLastLoginTime());
        user.setLastLoginIp(userPO.getLastLoginIp());
        user.setLoginFailCount(userPO.getLoginFailCount());
        user.setPwdUpdateTime(userPO.getPwdUpdateTime());
        user.setCreatedBy(userPO.getCreatedBy());
        user.setCreatedTime(userPO.getCreatedTime());
        user.setUpdatedBy(userPO.getUpdatedBy());
        user.setUpdatedTime(userPO.getUpdatedTime());

        return user;
    }
}