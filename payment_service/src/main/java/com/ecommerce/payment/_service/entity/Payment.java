package com.ecommerce.payment._service.entity;

import com.ecommerce.payment._service.util.PaymentStatus;
import com.ecommerce.payment._service.util.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Data
@Table(name = "tbl_payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long orderId;
    private long userId;
    private long shopId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Số tiền thanh toán

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType; // Phương thức thanh toán (COD, VNPAY)
    @Column(nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

}
