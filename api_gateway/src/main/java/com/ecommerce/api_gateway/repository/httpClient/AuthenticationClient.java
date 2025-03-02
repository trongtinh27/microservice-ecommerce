package com.ecommerce.api_gateway.repository.httpClient;

import com.ecommerce.api_gateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<IntrospectResponse>> introspect(@RequestHeader("Authorization") String token);
}
