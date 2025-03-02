package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.RedisToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
