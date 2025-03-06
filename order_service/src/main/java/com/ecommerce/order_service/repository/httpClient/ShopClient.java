package com.ecommerce.order_service.repository.httpClient;

import com.ecommerce.order_service.dto.response.ShopDetailResponse;
import com.ecommerce.security.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "shop-service", url = "${app.services.shop}", configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

    @GetMapping("/shop/")
    ShopDetailResponse getShop();
}
