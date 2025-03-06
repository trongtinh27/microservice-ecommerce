package com.ecommerce.order_service.dto.request;


import com.ecommerce.order_service.util.PaymentType;
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
    private PaymentType paymentType;
}
