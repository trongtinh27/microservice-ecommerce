package com.ecommerce.order_service.repository;


import com.ecommerce.order_service.entity.Cart;
import org.springframework.data.repository.CrudRepository;


public interface RedisCartRepository extends CrudRepository<Cart, String> {
}
