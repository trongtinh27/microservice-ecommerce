package com.ecommerce.order_service.controller;


import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.request.UpdateStatusRequest;
import com.ecommerce.order_service.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;


    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrdersUser(HttpServletRequest request, @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize){
        return new ResponseEntity<>(orderService.getOrdersUser(request, pageNo, pageSize), OK);
    }

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrder(HttpServletRequest request, @RequestBody @Valid OrderRequest orderRequest){
        return new ResponseEntity<>(orderService.createOrder(request, orderRequest), CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderDetail(HttpServletRequest request, @PathVariable long id){
        return new ResponseEntity<>(orderService.getOrderDetail(request, id), OK);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('Seller')")
    public ResponseEntity<?> updateStatusOrder(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateStatusRequest status) {
        return new ResponseEntity<>(orderService.updateStatusOrder(request, id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelOrder(HttpServletRequest request, @PathVariable long id) {
        return new ResponseEntity<>(orderService.cancelOrder(request, id), OK);
    }
}
