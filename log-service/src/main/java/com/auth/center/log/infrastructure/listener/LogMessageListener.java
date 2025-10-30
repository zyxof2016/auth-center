package com.auth.center.log.infrastructure.listener;

import com.alibaba.fastjson.JSON;
import com.auth.center.log.application.dto.LoginLogDTO;
import com.auth.center.log.application.dto.OperationLogDTO;
import com.auth.center.log.application.service.LogApplicationService;
import com.auth.center.common.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 日志消息监听器
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "log-topic", consumerGroup = "log-service-consumer")
@RequiredArgsConstructor
public class LogMessageListener implements RocketMQListener<String> {
    
    private final LogApplicationService logApplicationService;
    
    @Override
    public void onMessage(String message) {
        try {
            // 解析消息并记录日志
            LogMessage logMessage = JSON.parseObject(message, LogMessage.class);
            
            if ("operation".equals(logMessage.getType())) {
                // 处理操作日志
                OperationLogDTO operationLogDTO = JSON.parseObject(logMessage.getData(), OperationLogDTO.class);
                Response response = logApplicationService.recordOperationLog(operationLogDTO);
                if (!response.isSuccess()) {
                    log.error("记录操作日志失败: {}", response.getMessage());
                }
            } else if ("login".equals(logMessage.getType())) {
                // 处理登录日志
                LoginLogDTO loginLogDTO = JSON.parseObject(logMessage.getData(), LoginLogDTO.class);
                Response response = logApplicationService.recordLoginLog(loginLogDTO);
                if (!response.isSuccess()) {
                    log.error("记录登录日志失败: {}", response.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("处理日志消息失败: {}", message, e);
        }
    }
    
    /**
     * 日志消息封装类
     */
    public static class LogMessage {
        private String type;  // 日志类型: operation, login
        private String data;  // 日志数据JSON字符串
        
        // getters and setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getData() {
            return data;
        }
        
        public void setData(String data) {
            this.data = data;
        }
    }
}