package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.request.AddProductRequest;
import com.ecommerce.cart_service.dto.response.CreateOrderResponse;
import com.ecommerce.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCart(HttpServletRequest request){
        return new ResponseEntity<>(cartService.getCart(request),OK);
    }

    @PostMapping("/addToCart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addProductToCart(HttpServletRequest request, @RequestBody @Valid AddProductRequest requestBody){
        return new ResponseEntity<>(cartService.addProductToCart(request, requestBody),OK);
    }

    @PutMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateQuantityProduct(HttpServletRequest request, @RequestBody @Valid AddProductRequest requestBody) {
        return new ResponseEntity<>(cartService.updateProductInCart(request, requestBody), OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateStatusProduct(HttpServletRequest request, @PathVariable("id") @NotNull String productId) {
        return new ResponseEntity<>(cartService.updateStatusProduct(request, productId), OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteProductFromCart(HttpServletRequest request, @PathVariable("id") @NotNull String productId) {
        return new ResponseEntity<>(cartService.removeProduct(request, productId), OK);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        return new ResponseEntity<>(cartService.clearCart(request), OK);
    }

    @GetMapping("/createOrder")
    public CreateOrderResponse createOder(HttpServletRequest request) {
        return cartService.createOrder(request);
    }
}
