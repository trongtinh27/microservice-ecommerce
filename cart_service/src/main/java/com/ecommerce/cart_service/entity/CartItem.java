package com.ecommerce.cart_service.entity;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String productId;
    private String shopId;
    private String productName;
    private int quantity;
    private int maxQuantity;
    private BigDecimal price;
    private String image;
    private double discount;
    private boolean isChecked;
}
