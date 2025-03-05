package com.ecommerce.notification_service.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailRequest {
    EmailRequest.Recipient to;
    String subject;
    String htmlContent;
}
