package com.ecommerce.shop_service.controller;

import com.ecommerce.shop_service.dto.request.ProductRequest;
import com.ecommerce.shop_service.dto.request.SignUpRequest;
import com.ecommerce.shop_service.service.ProductService;
import com.ecommerce.shop_service.service.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/seller")
public class SellerController {
    private final ShopService shopService;
    private final ProductService productService;


    @PostMapping("/register")
    public ResponseEntity<?> sellerRegister(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(shopService.sellerRegister(request), OK);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> getOrder(HttpServletRequest request) {
        return new ResponseEntity<>(null, OK);
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id) {
        return new ResponseEntity<>(null, OK);
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> postNewProduct(@RequestBody ProductRequest request) {
        System.out.println("Request: " + request);
        return new ResponseEntity<>(productService.addProduct(request), OK);
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestBody Object request) {
        return new ResponseEntity<>(null, OK);
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> deletedProduct(@PathVariable Long id) {
        return new ResponseEntity<>(null, OK);
    }





}
