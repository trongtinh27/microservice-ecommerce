package com.ecommerce.payment._service.dto.request;

import com.ecommerce.payment._service.util.PaymentType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@Data
public class PaymentRequest {
    private List<PaymentItem> items;
    private String transactionId;
    private PaymentType paymentType;
    private Long totalAmount;
    private String orderType;


    @Builder
    @Getter
    @Setter
    @Data
    static
    public class PaymentItem {
        private long orderId;
        private long shopId;
        private long userId;
        private BigDecimal amount;
    }
}
