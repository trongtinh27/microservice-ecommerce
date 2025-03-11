package com.ecommerce.payment._service.service;

import com.ecommerce.event.dto.OrderResponse;
import com.ecommerce.order_service.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderGrpcClient {
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    @Value("${app.services.order-service.name}")
    private String host;
    @Value("${app.services.order-service.port}")
    private int port;

    public OrderGrpcClient() {}

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // Lúc này `host` và `port` đã có giá trị
                .usePlaintext()
                .build();

        this.orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
        log.info("ProductGrpcClient initialized with host: {} and port: {}", host, port);
    }

    public String changeOrderStatus(long orderId, String orderStatus) {
        ChangeOrderStatusRequest request = ChangeOrderStatusRequest.newBuilder().setOrderId(orderId).setStatus(orderStatus).build();

        ChangeOrderStatusResponse response = orderServiceBlockingStub.changeOrderStatus(request);
        return response.getStatus();
    }

    public List<OrderResponse> getOrders(String transactionId) {
        GetOrderRequest request = GetOrderRequest.newBuilder().setTransactionId(transactionId).build();

        ListOrderResponse listOrderResponse = orderServiceBlockingStub.getOderByTransactionId(request);

        return listOrderResponse.getOrdersList().stream()
                .map(order -> buildOrderResponse(order, mapOrderItemsToResponse(order)))
                .collect(Collectors.toList());

    }

    private List<OrderResponse.OrderItem> mapOrderItemsToResponse(com.ecommerce.order_service.grpc.OrderResponse order) {
        return order.getItemsList().stream()
                .map(item -> OrderResponse.OrderItem.builder()
                        .productId(item.getProductId())
                        .shopId(item.getShopId())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .quantity(item.getQuantity())
                        .price(new BigDecimal(item.getPrice()))
                        .discount(item.getDiscount())
                        .build())
                .collect(Collectors.toList());
    }


    private OrderResponse buildOrderResponse(com.ecommerce.order_service.grpc.OrderResponse order, List<OrderResponse.OrderItem> items) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .paymentMethod(order.getPaymentMethod() + "- Đã thanh toán")
                .shippingAddress(order.getShippingAddress())
                .createAt(new Date(order.getCreatedAt().getSeconds() * 1000 + order.getCreatedAt().getNanos() / 1_000_000))
                .status(order.getStatus())
                .totalPrice(new BigDecimal(order.getTotalPrice()))
                .transactionId(order.getTransactionId())
                .items(items)
                .build();
    }


}
