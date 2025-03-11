package com.ecommerce.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3); // Retry tối đa 3 lần
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
