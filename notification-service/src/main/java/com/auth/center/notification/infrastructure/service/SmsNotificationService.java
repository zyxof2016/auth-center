package com.auth.center.notification.infrastructure.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.auth.center.notification.domain.entity.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信通知发送服务
 */
@Slf4j
@Service
public class SmsNotificationService implements NotificationSendService {
    
    @Value("${sms.aliyun.access-key-id}")
    private String accessKeyId;
    
    @Value("${sms.aliyun.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${sms.aliyun.sign-name}")
    private String signName;
    
    private IAcsClient client;
    
    public SmsNotificationService() {
        // 初始化阿里云短信客户端
    }
    
    @Override
    public boolean sendNotification(NotificationEntity notification) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            client = new DefaultAcsClient(profile);
    
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", notification.getReceiver());
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", notification.getTemplateId());
            request.putQueryParameter("TemplateParam", notification.getTemplateParams());
    
            CommonResponse response = client.getCommonResponse(request);
            boolean success = response.getHttpResponse().isSuccess();
            
            if (success) {
                log.info("短信通知发送成功: notificationId={}, receiver={}", notification.getId(), notification.getReceiver());
            } else {
                log.error("短信通知发送失败: notificationId={}, receiver={}, response={}", 
                        notification.getId(), notification.getReceiver(), response.getData());
            }
            
            return success;
        } catch (ServerException e) {
            log.error("短信通知发送失败: notificationId={}, receiver={}, error={}", 
                    notification.getId(), notification.getReceiver(), e.getMessage(), e);
            return false;
        } catch (ClientException e) {
            log.error("短信通知发送失败: notificationId={}, receiver={}, error={}", 
                    notification.getId(), notification.getReceiver(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean retrySendNotification(NotificationEntity notification) {
        return sendNotification(notification);
    }
}