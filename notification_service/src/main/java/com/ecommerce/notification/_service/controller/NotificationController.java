package com.ecommerce.notification._service.controller;


import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.notification._service.dto.request.EmailRequest;
import com.ecommerce.notification._service.dto.request.SendEmailRequest;
import com.ecommerce.notification._service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    @KafkaListener(topics = "shop-created")
    public void listen(NotificationEvent notificationEvent) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", notificationEvent.getData().get("userName"));
        properties.put("shopName", notificationEvent.getData().get("shopName"));
        properties.put("registrationDate", notificationEvent.getData().get("createdAt"));
        properties.put("shopDescription", notificationEvent.getData().get("description"));

        Context context = new Context();
        context.setVariables(properties);

        String html = templateEngine.process("shop-created", context);

        SendEmailRequest request = SendEmailRequest.builder()
                .to(EmailRequest.Recipient.builder()
                        .name(notificationEvent.getData().get("userName").toString())
                        .email(notificationEvent.getRecipient())
                        .build())
                .subject("Shop created successfully")
                .htmlContent(html)
                .build();

        emailService.sendEmail(request);
    }
}
