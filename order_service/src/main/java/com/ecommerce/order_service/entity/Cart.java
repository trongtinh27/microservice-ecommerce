package com.ecommerce.order_service.entity;


import com.ecommerce.order_service.dto.response.CartItem;
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
@ToString
public class Cart implements Serializable {
    @Id
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice;

}
