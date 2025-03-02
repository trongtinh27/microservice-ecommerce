package com.ecommerce.user_service.service;

import com.ecommerce.user_service.entity.RedisToken;
import com.ecommerce.user_service.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;
    private final StringRedisTemplate redisTemplate;
    public void save(RedisToken token) {
        redisTokenRepository.save(token);
    }

    public void remove(String id) {
        if(isExists(id)) redisTokenRepository.deleteById(id);
    }

    public void updateAccessToken(String id, String newAccessToken){
        if(isExists(id)) redisTemplate.opsForHash().put("RedisToken:"+id, "accessToken", newAccessToken);
    }

    public boolean isExists(String id) {
        return redisTokenRepository.existsById(id);
    }
}
