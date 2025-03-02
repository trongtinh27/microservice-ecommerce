package com.ecommerce.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.accessKey}")
    private String accessKey;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRole(String token) {
        Claims claims = extractAllClaim(token);
        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .filter(String.class::isInstance) // Lọc chỉ lấy String
                    .map(String.class::cast)         // Ép kiểu về String
                    .toList();
        }
        return Collections.emptyList(); // Trả về danh sách rỗng nếu không hợp lệ
    }

    public boolean isTokenValid(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaim(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey))).build().parseClaimsJws(token).getBody();
    }
}
