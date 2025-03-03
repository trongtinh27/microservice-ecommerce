package com.ecommerce.user_service.service;


import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
public interface JwtService {
    String generateToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);

}
