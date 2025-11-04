package com.auth.center.upms.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 客户端管理服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ClientServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }
}