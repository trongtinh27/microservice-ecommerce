package com.ecommerce.order_service.controller;


import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(HttpServletRequest request, @RequestBody @Valid OrderRequest orderRequest){
        return new ResponseEntity<>(orderService.createOrder(request, orderRequest), CREATED);
    }
}
