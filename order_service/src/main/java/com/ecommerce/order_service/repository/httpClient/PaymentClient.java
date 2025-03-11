package com.ecommerce.order_service.repository.httpClient;

import com.ecommerce.order_service.dto.request.PaymentRequest;
import com.ecommerce.security.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${app.services.payment}", configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {
    @PostMapping("/vn-pay")
    String pay(@RequestBody PaymentRequest paymentRequest);
}
