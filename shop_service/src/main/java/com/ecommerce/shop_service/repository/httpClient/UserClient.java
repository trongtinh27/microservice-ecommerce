package com.ecommerce.shop_service.repository.httpClient;

import com.ecommerce.security.AuthenticationRequestInterceptor;
import com.ecommerce.shop_service.dto.request.SignUpRequest;
import com.ecommerce.shop_service.dto.response.UserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${app.services.user}", configuration = {AuthenticationRequestInterceptor.class})
public interface UserClient {
    @PostMapping(value = "/seller-register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Long> sellerRegister(@RequestBody SignUpRequest request);

    @GetMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDetailResponse> getProfile();
}
