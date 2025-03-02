package com.ecommerce.user_service.config;

import com.ecommerce.user_service.service.JwtService;
import com.ecommerce.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.ecommerce.user_service.exception.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.ecommerce.user_service.util.TokenType.ACCESS_TOKEN;


@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("---------- doFilterInternal ----------");

        final String authorization = request.getHeader(AUTHORIZATION);

        if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring("Bearer ".length());

        String userName;
        try {
            userName = jwtService.extractUsername(token, ACCESS_TOKEN);
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
//        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        if(StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);
            if(jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
