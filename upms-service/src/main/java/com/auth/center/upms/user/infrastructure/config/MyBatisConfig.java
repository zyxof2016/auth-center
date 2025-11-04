package com.auth.center.user.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.auth.center.user.infrastructure.persistence")
public class MyBatisConfig {
    // 配置类，用于扫描Mapper接口
}