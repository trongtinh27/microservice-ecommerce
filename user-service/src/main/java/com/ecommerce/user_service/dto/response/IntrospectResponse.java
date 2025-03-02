package com.ecommerce.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class IntrospectResponse {
    private boolean isValid;
    private List<String> permissions;
}
