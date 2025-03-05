package com.ecommerce.notification_service.controller;


import com.ecommerce.notification_service.dto.request.SendEmailRequest;
import com.ecommerce.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    ResponseEntity<?> sendEmail(@RequestBody SendEmailRequest request) {
        return ResponseEntity.ok(emailService.sendEmail(request));
    }



}
