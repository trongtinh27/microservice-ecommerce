package com.ecommerce.user_service.entity;

import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RedisHash("RedisToken")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisToken implements Serializable {
    private String id;
    private String accessToken;
    private String refreshToken;
//    private String resetToken;
    @TimeToLive // TTL tính bằng giây
    private Long expiration; // Số giây token sẽ tồn tại
}