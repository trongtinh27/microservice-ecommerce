package com.ecommerce.notification_service.controller;


import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.event.dto.OrderResponse;
import com.ecommerce.notification_service.dto.request.EmailRequest;
import com.ecommerce.notification_service.dto.request.SendEmailRequest;
import com.ecommerce.notification_service.service.EmailService;
import com.ecommerce.notification_service.service.ShopGrpcClient;
import com.ecommerce.notification_service.service.UserGrpcClient;
import com.ecommerce.user_service.grpc.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final UserGrpcClient userGrpcClient;
    private final ShopGrpcClient shopGrpcClient;

    @KafkaListener(topics = "shop-created", containerFactory = "kafkaListenerContainerFactory")
    public void listen(NotificationEvent notificationEvent, Acknowledgment ack) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", notificationEvent.getData().get("userName"));
        properties.put("shopName", notificationEvent.getData().get("shopName"));
        properties.put("registrationDate", notificationEvent.getData().get("createdAt"));
        properties.put("shopDescription", notificationEvent.getData().get("description"));

        Context context = new Context();
        context.setVariables(properties);

        String html = templateEngine.process("shop-created", context);

        SendEmailRequest request = SendEmailRequest.builder()
                .to(EmailRequest.Recipient.builder()
                        .name(notificationEvent.getData().get("userName").toString())
                        .email(notificationEvent.getRecipient())
                        .build())
                .subject("Shop created successfully")
                .htmlContent(html)
                .build();

        emailService.sendEmail(request);

        ack.acknowledge();

    }

    @KafkaListener(topics = "new-order", containerFactory = "kafkaListenerContainerFactory")
    public void newOrderListener(OrderResponse orderResponse, Acknowledgment ack) {
        try {
            long userId = orderResponse.getUserId();
            long sellerId = shopGrpcClient.getOwnerId(orderResponse.getItems().get(0).getShopId());

            UserResponse user = userGrpcClient.getUser(userId);
            UserResponse seller = userGrpcClient.getUser(sellerId);

            Map<String, Object> properties = createEmailProperties(orderResponse, user);

            sendOrderEmail(user, "new-order-user", properties);
            sendOrderEmail(seller, "new-order-seller", properties);

            // ✅ Commit offset sau khi xử lý xong
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng", e);
            // Không commit offset để retry lại sau
        }
    }


    private Map<String, Object> createEmailProperties(OrderResponse orderResponse, UserResponse user) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("orderId", orderResponse.getOrderId());
        properties.put("createdAt", orderResponse.getCreateAt());
        properties.put("totalPrice", orderResponse.getTotalPrice());
        properties.put("discountCode", "NONE");
        properties.put("discountAmount", 0);
        properties.put("finalTotal", orderResponse.getTotalPrice());
        properties.put("customerName", user.getFullName());
        properties.put("customerPhone", user.getPhoneNumber());
        properties.put("customerEmail", user.getEmail());
        properties.put("customerAddress", orderResponse.getShippingAddress());
        properties.put("paymentMethod", orderResponse.getPaymentMethod());
        properties.put("products", orderResponse.getItems());
        return properties;
    }

    private void sendOrderEmail(UserResponse recipient, String template, Map<String, Object> properties) {
        Context contextHtml = new Context();
        contextHtml.setVariables(properties);
        String htmlContent = templateEngine.process(template, contextHtml);

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(EmailRequest.Recipient.builder()
                        .name(recipient.getUserName())
                        .email(recipient.getEmail())
                        .build())
                .subject("New order")
                .htmlContent(htmlContent)
                .build();

        emailService.sendEmail(emailRequest);
    }

}
