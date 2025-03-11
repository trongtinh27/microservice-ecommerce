package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.Order;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUserId(long id, long userId);

    Optional<Order> findByIdAndShopId(Long id, long shopId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.transactionId = :transactionId")
    List<Order> getOrderByTransactionId(@Param("transactionId") String transactionId);
//    List<Order> findAllByTransactionId(String transactionId);
}
