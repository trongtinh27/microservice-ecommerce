package com.ecommerce.cart_service.entity;


import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItem implements Serializable {
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
