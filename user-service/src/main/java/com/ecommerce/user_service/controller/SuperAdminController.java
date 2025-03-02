package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/super-admin")
public class SuperAdminController {
    private final UserService userService;


    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                         @Min(10) @RequestParam(defaultValue = "10", required = false) int pageSize) {
        log.info("Request get admin list, pageNo={}, pageSize={}", pageNo, pageSize);
        return new ResponseEntity<>(userService.getAllUsersByRole(pageNo, pageSize, List.of("Admin")), OK);
    }

    @PostMapping("/admins")
    public ResponseEntity<?> addAdmin(@RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(userService.saveUser(signUpRequest,"Admin"),OK);
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("Deleted admin " + id,OK);
    }
}
