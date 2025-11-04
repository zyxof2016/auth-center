package com.auth.center.upms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 统一权限管理服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UpmsServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UpmsServiceApplication.class, args);
    }
}