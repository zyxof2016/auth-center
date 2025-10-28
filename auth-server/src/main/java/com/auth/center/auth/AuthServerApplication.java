package com.auth.center.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}