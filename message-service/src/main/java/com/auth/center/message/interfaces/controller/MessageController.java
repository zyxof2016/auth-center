package com.auth.center.message.interfaces.controller;

import com.auth.center.message.application.service.MessageApplicationService;
import com.auth.center.message.domain.entity.MessageEntity;
import com.auth.center.message.domain.enums.MessageStatus;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息管理控制器
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageApplicationService messageApplicationService;
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public SingleResponse<MessageEntity> sendMessage(@RequestBody MessageEntity message) {
        return messageApplicationService.sendMessage(message);
    }
    
    /**
     * 更新消息状态
     */
    @PutMapping("/{messageId}/status")
    public Response updateMessageStatus(@PathVariable Long messageId, 
                                      @RequestParam MessageStatus status,
                                      @RequestParam(required = false) String errorMessage) {
        return messageApplicationService.updateMessageStatus(messageId, status, errorMessage);
    }
    
    /**
     * 获取待处理消息
     */
    @GetMapping("/pending")
    public List<MessageEntity> getPendingMessages(@RequestParam String topic,
                                                @RequestParam(defaultValue = "10") int maxCount) {
        return messageApplicationService.getPendingMessages(topic, maxCount);
    }
    
    /**
     * 分页查询消息列表
     */
    @GetMapping
    public PageResponse<MessageEntity> getMessagePage(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) MessageStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return messageApplicationService.getMessagePage(topic, status, startTime, endTime, page, size);
    }
    
    /**
     * 重试失败消息
     */
    @PostMapping("/retry")
    public Response retryFailedMessages(@RequestParam String topic) {
        return messageApplicationService.retryFailedMessages(topic);
    }
    
    /**
     * 删除过期消息
     */
    @DeleteMapping("/expired")
    public Response deleteExpiredMessages(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime expireTime) {
        return messageApplicationService.deleteExpiredMessages(expireTime);
    }
    
    /**
     * 根据键查找消息
     */
    @GetMapping("/key/{keys}")
    public SingleResponse<MessageEntity> getMessageByKey(@PathVariable String keys) {
        return messageApplicationService.getMessageByKey(keys);
    }
}