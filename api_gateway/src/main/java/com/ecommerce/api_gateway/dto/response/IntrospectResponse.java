package com.ecommerce.api_gateway.dto.response;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
@Getter
public class IntrospectResponse {
    private boolean isValid;
    private List<String> permissions;
}
