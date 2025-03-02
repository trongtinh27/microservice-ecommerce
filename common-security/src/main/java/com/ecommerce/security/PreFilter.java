package com.ecommerce.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(AUTHORIZATION);
        if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authorization.substring("Bearer ".length());
        final String userName;
        try {
            userName = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Xử lý khi Token lỗi
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(APPLICATION_JSON_VALUE);
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTimestamp(new Date());
            errorResponse.setPath("PreFilter");
            errorResponse.setStatus(FORBIDDEN.value());
            errorResponse.setError(FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage(e.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
            return;
        }
        if (jwtService.isTokenValid(token, userName)) {
            List<String> roles = jwtService.extractRole(token);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(roles.get(0)));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userName, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);

    }

    @Getter
    @Setter
    @Data
    static
    class ErrorResponse {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Ho_Chi_Minh")
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String message;
    }
}
