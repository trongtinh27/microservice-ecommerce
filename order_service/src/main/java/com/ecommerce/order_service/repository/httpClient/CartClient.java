package com.ecommerce.order_service.repository.httpClient;

import com.ecommerce.order_service.dto.response.CreateOrderResponse;
import com.ecommerce.security.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart-service", url = "${app.services.cart}", configuration = {AuthenticationRequestInterceptor.class})
public interface CartClient {

    @GetMapping(value = "/createOrder")
    CreateOrderResponse createOrder();
}
