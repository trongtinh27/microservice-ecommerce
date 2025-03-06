package com.ecommerce.cart_service.repository.httpClient;

import com.ecommerce.cart_service.dto.response.ProductResponse;
import com.ecommerce.security.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product}", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponse getProducts(@PathVariable("id") String productId);
}
