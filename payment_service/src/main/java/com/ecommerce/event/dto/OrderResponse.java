package com.ecommerce.event.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderResponse {
    private long orderId;
    private long userId;
    private String shippingAddress;
    private String status;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private Date createAt;
    private String transactionId;
    private List<OrderItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    static
    public class OrderItem {
        private String productId;
        private long shopId;
        private String productName;
        private String imageUrl;
        private int quantity;
        private BigDecimal price;
        private double discount;
    }
}
