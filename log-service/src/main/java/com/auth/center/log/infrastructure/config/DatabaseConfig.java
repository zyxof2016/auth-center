package com.auth.center.log.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置
 */
@Configuration
@MapperScan("com.auth.center.log.infrastructure.persistence.mybatis.mapper")
public class DatabaseConfig {
}