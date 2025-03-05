package com.ecommerce.notification_service.repository.httpClient;

import com.ecommerce.notification_service.dto.request.EmailRequest;
import com.ecommerce.notification_service.dto.response.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "email-service", url = "https://api.brevo.com/v3")
public interface EmailClient {

    @PostMapping("/smtp/email")
    EmailResponse sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailRequest request);
}
