package com.ecommerce.order_service.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Data
@Getter
@Validated
public class OrderRequest {
    @NotNull
    private String shippingAddress;
}
