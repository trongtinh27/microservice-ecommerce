package com.ecommerce.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
public class OrderResponse {
    private long orderId;
    private long userId;
    private String shippingAddress;
    private String status;
    private Date createAt;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String transactionId;
    private List<OrderItem> items;


    @Data
    @Builder
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
