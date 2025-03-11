package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.entity.Cart;
import com.ecommerce.cart_service.repository.RedisCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCartService {

    private final RedisCartRepository redisCartRepository;

    public void save(Cart cart) {
        redisCartRepository.save(cart);
    }

    public void remove(String id) {
        if(isExists(id)) redisCartRepository.deleteById(id);
    }

    public boolean isExists(String id) {
        return redisCartRepository.existsById(id);
    }

    public Cart get(String id) {
        return redisCartRepository.findById(id).orElse(null);
    }
}
