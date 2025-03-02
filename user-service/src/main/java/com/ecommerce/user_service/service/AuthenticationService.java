package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.request.SignInRequest;
import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.dto.response.IntrospectResponse;
import com.ecommerce.user_service.dto.response.TokenResponse;
import com.ecommerce.user_service.entity.RedisToken;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.AccountExistedException;
import com.ecommerce.user_service.exception.InvalidDataException;
import com.ecommerce.user_service.exception.TokenException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.REFERER;
import static com.ecommerce.user_service.util.TokenType.*;
import static com.ecommerce.user_service.util.UserStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PermissionCacheService permissionCacheService;

    @Value("${jwt.expiryDay}")
    private long expireTime;


    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("---------- accessToken ----------");
        var user = userService.getByUsername(signInRequest.getUsername());
        if(user == null ) {
            throw new BadCredentialsException("Account is incorrect");
        }

        var roles = userService.findAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Password is incorrect");

        } catch (LockedException e) {
            throw new LockedException(e.getMessage());
        }

        if(!user.isEnabled() || user.getStatus().equals(INACTIVE)) throw new RuntimeException("User not active");

        Set<String> permissions = userService.findPermissionsByRoles(roles.stream().toList());
        permissionCacheService.savePermissionsToRedis(user.getUsername(), permissions);

        return generateToken(user);

    }

    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String refreshToken = request.getHeader(REFERER);
        if(StringUtils.isBlank(refreshToken)) {
            throw new TokenException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
        var user = userService.getByUsername(userName);
        if(!jwtService.isValid(refreshToken, REFRESH_TOKEN, user)) {
            throw new TokenException("Not allow access with this token");
        }
        if(!redisTokenService.isExists(userName)) {
            throw new TokenException("Token is expired");
        }
        // create new accessToken
        String accessToken = jwtService.generateToken(user);
        // save token to redis
        redisTokenService.updateAccessToken(user.getUsername(),accessToken);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public IntrospectResponse introspect(HttpServletRequest request) {
        log.info("---------- introspect ----------");

        final String token = request.getHeader(AUTHORIZATION);

        boolean isValid;
        List<String> permissions = new ArrayList<>();
        try {
            final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);
            var user = userService.getByUsername(userName);
            isValid = jwtService.isValid(token, ACCESS_TOKEN, user);
            permissions = permissionCacheService.getPermissionsFormRedis(userName).stream().toList();
        } catch (Exception e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .permissions(permissions)
                .build();
    }

    public void removeToken(HttpServletRequest request) {
        log.info("---------- removeToken ----------");

        final String token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        if(StringUtils.isBlank(token)){
            throw new TokenException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);
        redisTokenService.remove(userName);
        permissionCacheService.clearPermissionCache(userName);
        SecurityContextHolder.clearContext();
    }

    public TokenResponse register(SignUpRequest signUpRequest) {

            long userId = userService.saveUser(signUpRequest, "User");
            var user = userService.getUserById(userId);

            List<String> roles = userService.findAllRolesByUserId(user.getId());
            List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword(), authorities));
            Set<String> permissions = userService.findPermissionsByRoles(roles);
            permissionCacheService.savePermissionsToRedis(user.getUsername(), permissions);

            return generateToken(user);
    }


    private TokenResponse generateToken(User user) {
        // create new access token
        String accessToken = jwtService.generateToken(user);
        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);
        // save to redis
        redisTokenService.save(RedisToken.builder()
                .id(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiration((1000 * 60 * 60 * 24 * expireTime))
                .build());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
