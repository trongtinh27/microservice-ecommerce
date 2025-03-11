package com.ecommerce.product_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class DeleteProductRequest {
    @NotNull
    private String productId;
    @NotNull
    private String shopId;
}
