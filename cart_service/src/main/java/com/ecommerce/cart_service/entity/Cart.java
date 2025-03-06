package com.ecommerce.cart_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RedisHash("cart")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    @Id
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice;

    public void addItem(CartItem item){
        if(this.items == null) this.items = new ArrayList<>();
        this.items.add(item);
    }
}
