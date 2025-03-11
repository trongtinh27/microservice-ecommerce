package com.ecommerce.order_service.entity;


import com.ecommerce.order_service.util.OrderStatus;
import com.ecommerce.order_service.util.PaymentType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonSerialize
@Entity
@Table(name = "tbl_orders")
@NoArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    private long shopId;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.COD;

    private String transactionId;

    private String shippingAddress;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    public void addItems(OrderItem item){
        if(this.orderItems == null) this.orderItems = new ArrayList<>();
        item.setOrder(this);
        this.orderItems.add(item);
    }
}
