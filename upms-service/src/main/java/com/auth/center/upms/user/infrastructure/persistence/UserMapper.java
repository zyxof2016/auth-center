package com.auth.center.user.infrastructure.persistence;

import com.auth.center.user.infrastructure.persistence.po.UserPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户持久化映射器
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

}