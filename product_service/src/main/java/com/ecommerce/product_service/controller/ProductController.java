package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.request.DeleteProductRequest;
import com.ecommerce.product_service.dto.request.EditProductRequest;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), OK);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('Seller', 'Admin', 'Super Admin')")
    public ResponseEntity<?> editProduct(@RequestBody EditProductRequest request) {
        return new ResponseEntity<>(productService.editProduct(request), OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('Seller')")
    public ResponseEntity<?> deleteProductForSeller(@RequestBody DeleteProductRequest request) {
        productService.deleteProductForSeller(request);
        return new ResponseEntity<>("Deleted", OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Super Admin')")
    public ResponseEntity<?> deleteProductForAdmin(@PathVariable String id) {
        productService.deleteProductForAdmin(id);
        return new ResponseEntity<>("Deleted", OK);
    }

}
