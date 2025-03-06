package com.ecommerce.api_gateway.config;

import com.ecommerce.api_gateway.serivce.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import com.ecommerce.api_gateway.dto.response.ErrorResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;

    private final String[] WHITE_LIST = {"/auth/register",
            "/auth/login",
            "/auth/logout",
            "/auth/refresh",
            "/seller/register",
            "/email/send"
    };

    private static final Map<String, String> PERMISSION_MAP = Map.ofEntries(
            Map.entry("/user/.*", "UPDATE_PROFILE"),
            Map.entry("/seller/orders", "MANAGE_OWN_PRODUCTS"),
            Map.entry("/seller/orders/.*", "MANAGE_OWN_PRODUCTS"),
            Map.entry("/seller/products", "MANAGE_OWN_PRODUCTS"),
            Map.entry("/seller/products/.*", "MANAGE_OWN_PRODUCTS"),
            Map.entry("/order/.*", "CHECKOUT"),
            Map.entry("/cart", "ADD_TO_CART"),
            Map.entry("/cart/.*", "ADD_TO_CART"),
            Map.entry("/admin/.*", "MANAGE_USERS"),
            Map.entry("/super-admin/admins", "EDIT_ADMIN"),
            Map.entry("/super-admin/admins/.*", "EDIT_ADMIN")

    );
    @Value("${app.api-prefix}")
    private String api_prefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("------------AuthenticationFilter-------------");
        if(isPublicEndpoint(exchange.getRequest())){
            return chain.filter(exchange);
        }

        // Get Token form header
        List<String> header = exchange.getRequest().getHeaders().get(AUTHORIZATION);
        if(CollectionUtils.isEmpty(header)) return filterError(exchange.getResponse(), "Unauthenticated");

        final String token = header.get(0).substring("Bearer ".length());

        return authenticationService.introspect(token).flatMap(introspectResponse -> {
            if (Objects.requireNonNull(introspectResponse.getBody()).isValid()) {
                String path = exchange.getRequest().getPath().toString();
                List<String> permissions = introspectResponse.getBody().getPermissions();
                if(permissions.isEmpty() || !hasAccess(path, permissions)) return filterError(exchange.getResponse(), "Forbidden");

                return chain.filter(exchange);
            }
                return filterError(exchange.getResponse(), "Unauthenticated");
        }).onErrorResume(throwable -> filterError(exchange.getResponse(), "Unauthenticated"));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return Arrays.stream(WHITE_LIST)
                .anyMatch(string -> request.getURI().getPath().matches(api_prefix + string));
    }

    private boolean hasAccess(String path, List<String> permissions) {
        return PERMISSION_MAP.entrySet().stream()
                .anyMatch(entry -> path.matches(api_prefix+entry.getKey()) && permissions.contains(entry.getValue()));
    }

    private Mono<Void> filterError(ServerHttpResponse response, String message) {
        // Xử lý khi Token lỗi
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath("AuthenticationFilter");
        errorResponse.setStatus(FORBIDDEN.value());
        errorResponse.setError(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(message);

        String body = null;
        try {
            body = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);;

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
