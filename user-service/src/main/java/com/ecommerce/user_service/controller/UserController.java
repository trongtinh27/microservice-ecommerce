package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.request.AddressDTO;
import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.dto.request.UpdateUserRequest;
import com.ecommerce.user_service.dto.response.UserDetailResponse;
import com.ecommerce.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getProfile(request), OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest updateUserRequest) {
        return new ResponseEntity<>(userService.updateUser(updateUserRequest), OK);
    }

    @PostMapping("/seller-register")
    public ResponseEntity<?> sellerRegister(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(userService.saveUser(request, "Seller"), OK);
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getListAddress(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getListAddress(request), OK);
    }

    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(HttpServletRequest request, @RequestBody @NonNull AddressDTO addressDTO) {
        return new ResponseEntity<>(userService.addAddress(request, addressDTO), OK);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(HttpServletRequest request, @PathVariable long id) {
        return new ResponseEntity<>(userService.deleteAddress(request, id),OK);
    }
}
