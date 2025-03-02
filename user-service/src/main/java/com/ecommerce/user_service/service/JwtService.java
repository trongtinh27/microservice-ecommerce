package com.ecommerce.user_service.service;


import com.ecommerce.user_service.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
public interface JwtService {
    String generateToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);

}
