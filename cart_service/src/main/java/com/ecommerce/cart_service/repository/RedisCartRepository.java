package com.ecommerce.cart_service.repository;

import com.ecommerce.cart_service.entity.Cart;
import org.springframework.data.repository.CrudRepository;


public interface RedisCartRepository extends CrudRepository<Cart, String> {
}
