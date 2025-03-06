package com.ecommerce.order_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private long orderId;
    private long userId;
    private String shippingAddress;
    private String status;
    private BigDecimal totalPrice;
    private String transactionId;
    private List<OrderItem> items;


    @Data
    @Builder
    static
    public class OrderItem {
        private String productId;
        private long shopId;
        private String productName;
        private int quantity;
        private BigDecimal price;
        private double discount;
    }
}
