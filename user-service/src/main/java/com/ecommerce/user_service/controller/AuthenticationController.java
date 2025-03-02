package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.request.SignInRequest;
import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.dto.response.TokenResponse;
import com.ecommerce.user_service.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest request) {
            TokenResponse tokenResponse = authenticationService.accessToken(request);
            return new ResponseEntity<>(tokenResponse, OK);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(authenticationService.register(request), OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refreshToken(request), OK);
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.introspect(request), OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authenticationService.removeToken(request);
        return new ResponseEntity<>("logout", OK);
    }
}
