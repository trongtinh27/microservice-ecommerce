package com.ecommerce.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderResponse {
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice;
}
