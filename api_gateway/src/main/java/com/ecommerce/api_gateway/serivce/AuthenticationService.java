package com.ecommerce.api_gateway.serivce;

import com.ecommerce.api_gateway.dto.response.IntrospectResponse;
import com.ecommerce.api_gateway.repository.httpClient.AuthenticationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationClient authenticationClient;

    public Mono<ResponseEntity<IntrospectResponse>> introspect(String token) {
        return authenticationClient.introspect(token);
    }
}
