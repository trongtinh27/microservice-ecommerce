package com.ecommerce.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PermissionCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void savePermissionsToRedis(String userName, Set<String> permissions) {
        try {
            String key = "permissions:user:" + userName;
            String value = objectMapper.writeValueAsString(permissions);
            redisTemplate.opsForValue().set(key, value, 14, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi lưu permission vào Redis", e);
        }
    }

    public Set<String> getPermissionsFormRedis(String userName) {
        try {
            String key = "permissions:user:" + userName;
            String value = redisTemplate.opsForValue().get(key);
            return objectMapper.readValue(value, new TypeReference<Set<String>>() {});

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi đọc permission từ Redis", e);
        }
    }

    public void clearPermissionCache(String userName) {
        String key = "permissions:user:" + userName;
        redisTemplate.delete(key);
    }
}
