package com.ecommerce.cart_service.dto.response;

import com.ecommerce.cart_service.entity.CartItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class CreateOrderResponse {
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice;
}
