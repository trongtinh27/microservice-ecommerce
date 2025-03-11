package com.ecommerce.payment._service.service;

import com.ecommerce.payment._service.config.VNPayConfig.VNPayConfig;
import com.ecommerce.payment._service.dto.request.PaymentRequest;
import com.ecommerce.payment._service.entity.Payment;
import com.ecommerce.payment._service.exception.ResourceNotFoundException;
import com.ecommerce.payment._service.repository.PaymentRepository;
import com.ecommerce.payment._service.util.PaymentStatus;
import com.ecommerce.payment._service.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final VNPayConfig vnpayConfig;
    private final PaymentRepository paymentRepository;
    private final OrderGrpcClient orderGrpcClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    public String createVNPAYPayment(HttpServletRequest request, PaymentRequest paymentRequest) {
        long amount = paymentRequest.getTotalAmount() * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnpayConfig.getVNPAYConfig(paymentRequest);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getVnp_SecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        paymentRequest.getItems().forEach(item -> {
            Payment payment = Payment.builder()
                    .orderId(item.getOrderId())
                    .userId(item.getUserId())
                    .shopId(item.getShopId())
                    .amount(item.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentType(paymentRequest.getPaymentType())
                    .transactionId(paymentRequest.getTransactionId())
                    .build();
            paymentRepository.save(payment);
            schedulePaymentTimeout(payment.getId());
        });
        return paymentUrl;
    }

    public ResponseEntity<?> vnPayCallback(HttpServletRequest request) {
        String transactionId = request.getParameter("vnp_TxnRef");
        List<Payment> payments = paymentRepository.findAllByTransactionId(transactionId);
        if(!request.getParameter("vnp_ResponseCode").equals("00")) {
            payments.forEach(payment -> {
                changeStatusPayment(payment.getId(), PaymentStatus.CANCEL, "cancelled");
            });

            Map<String, String> responseMap = Map.of("paymentId", transactionId, "paymentStatus", "cancelled");

            return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
        }
        payments.forEach(payment -> {
            changeStatusPayment(payment.getId(), PaymentStatus.SUCCESS, "paid");
        });

        orderGrpcClient.getOrders(transactionId).forEach(order -> {
            kafkaTemplate.send("new-order", order);
        });
        Map<String, String> responseMap = Map.of("paymentId", transactionId, "paymentStatus", "Successfully");

        return new ResponseEntity<>(responseMap, HttpStatus.OK);

    }

    public void changeStatusPayment(long paymentId, PaymentStatus paymentStatus, String orderStatus) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("Payment not found with id: " + paymentId)
        );
        payment.setStatus(paymentStatus);

        String statusOrder = orderGrpcClient.changeOrderStatus(payment.getOrderId(), orderStatus);

        log.info("Change payment with id: {}, status: {}", paymentId, statusOrder);
        paymentRepository.save(payment);
    }


    @Async
    public void schedulePaymentTimeout(long paymentId) {
        scheduler.schedule(() -> {
            changeStatusPayment(paymentId, PaymentStatus.CANCEL, "cancelled");
        }, 1, TimeUnit.HOURS);
    }
}
