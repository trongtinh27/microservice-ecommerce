package com.ecommerce.shop_service.repository.httpClient;


import com.ecommerce.security.AuthenticationRequestInterceptor;
import com.ecommerce.shop_service.dto.request.DeleteProductRequest;
import com.ecommerce.shop_service.dto.request.EditProductRequest;
import com.ecommerce.shop_service.dto.request.ProductRequest;
import com.ecommerce.shop_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${app.services.product}", configuration = {AuthenticationRequestInterceptor.class})

public interface ProductClient {
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request);

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ProductResponse editProduct(@RequestBody EditProductRequest request);

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String deleteProduct(@RequestBody DeleteProductRequest request);


}
