package com.auth.center.message.infrastructure.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 */
@Configuration
public class RocketMQConfig {
    
    /**
     * 配置RocketMQ生产者
     */
    @Bean
    public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(rocketMQProperties.getNameServer());
        producer.setProducerGroup(rocketMQProperties.getProducer().getGroup());
        return producer;
    }
    
    /**
     * 配置RocketMQ模板
     */
    @Bean
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer defaultMQProducer) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(defaultMQProducer);
        return rocketMQTemplate;
    }
}