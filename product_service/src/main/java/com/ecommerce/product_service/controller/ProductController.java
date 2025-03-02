package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;


    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), OK);
    }

}
