package com.auth.center.message.infrastructure.listener;

import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.message.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 消息监听器
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "${rocketmq.consumer-topic:common-topic}", 
                        consumerGroup = "${rocketmq.consumer-group:message-service-consumer}",
                        nameServer = "${rocketmq.name-server:localhost:9876}")
@RequiredArgsConstructor
public class MessageListenerService implements RocketMQListener<String> {
    
    private final MessageRepository messageRepository;
    
    @Override
    public void onMessage(String message) {
        log.info("接收到消息: {}", message);
        
        // 这里应该根据消息内容查找对应的MessageEntity
        // 简单实现：假设消息内容中包含消息ID信息
        // 在实际应用中，可能需要根据业务场景调整
        processMessage(message);
    }
    
    /**
     * 处理接收到的消息
     */
    private void processMessage(String messageContent) {
        try {
            // 实际业务处理逻辑
            // 这里可以根据业务需求处理消息
            log.info("处理消息内容: {}", messageContent);
            
            // 查找并更新消息状态为消费成功
            // 注意：这只是一个示例，实际应用中需要根据消息的KEYS或其他标识来查找对应的消息记录
            // 在这里，我们简化处理，不实际查找消息记录
            
            // 模拟处理成功
            log.info("消息处理成功: {}", messageContent);
        } catch (Exception e) {
            log.error("消息处理失败: {}", messageContent, e);
            
            // 更新消息状态为消费失败
            // 这里需要根据实际的消息ID来更新消息状态
        }
    }
}