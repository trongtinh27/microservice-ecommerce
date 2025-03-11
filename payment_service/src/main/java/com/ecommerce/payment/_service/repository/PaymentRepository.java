package com.ecommerce.payment._service.repository;

import com.ecommerce.payment._service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndTransactionId(long id, String transactionId);

    List<Payment> findAllByTransactionId(String transactionId);
}
