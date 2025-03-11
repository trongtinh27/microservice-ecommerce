package com.ecommerce.payment._service.controller;

import com.ecommerce.payment._service.dto.request.PaymentRequest;
import com.ecommerce.payment._service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/vn-pay")
    public String pay(HttpServletRequest request, @RequestBody PaymentRequest paymentRequest) {
        return paymentService.createVNPAYPayment(request, paymentRequest);
    }



    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> payCallbackHandler(HttpServletRequest request) {
//        String status = request.getParameter("vnp_ResponseCode");
//        if(status.equals("00")) {
//            return new ResponseEntity<>("Success", OK);
//        }
//
        return paymentService.vnPayCallback(request);
    }
}
