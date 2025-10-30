package com.auth.center.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 消息服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class MessageServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }
}