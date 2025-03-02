package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.service.UserService;
import com.ecommerce.user_service.util.UserStatus;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@Slf4j
@Validated
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                         @Min(10) @RequestParam(defaultValue = "10", required = false) int pageSize) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);
        return new ResponseEntity<>(userService.getAllUsersByRole(pageNo, pageSize, List.of("User", "Seller")), OK);
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<?> getBanUser(@PathVariable long userId) {
        return new ResponseEntity<>(userService.changeStatus(userId, UserStatus.LOCKED), OK);
    }
}
