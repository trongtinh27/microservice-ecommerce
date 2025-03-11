package com.ecommerce.notification_service.service;


import com.ecommerce.notification_service.dto.request.EmailRequest;
import com.ecommerce.notification_service.dto.request.SendEmailRequest;
import com.ecommerce.notification_service.dto.response.EmailResponse;
import com.ecommerce.notification_service.repository.httpClient.EmailClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailClient emailClient;

    @Value("${email.api-key}")
    private String apiKey;
    @Value("${email.sender-email}")
    private String senderEmail;

    public EmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
        EmailRequest request = EmailRequest.builder()
                .sender(EmailRequest.Sender.builder()
                        .name("Ecommerce-ShopeeMini")
                        .email(senderEmail)
                        .build())
                .to(List.of(sendEmailRequest.getTo()))
                .subject(sendEmailRequest.getSubject())
                .htmlContent(sendEmailRequest.getHtmlContent())
                .build();

        try {
            return emailClient.sendEmail(apiKey, request);
        } catch (FeignException e) {
            throw new RuntimeException("Error when send email");
        }
    }

}
