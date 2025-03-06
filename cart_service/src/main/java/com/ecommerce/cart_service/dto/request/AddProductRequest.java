package com.ecommerce.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
public class AddProductRequest {
    @NotNull
    private String productId;
    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}
