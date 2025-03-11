package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.grpc.*;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.util.OrderStatus;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class OrderGrpcImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private final OrderService orderService;


    @Override
    public void changeOrderStatus(ChangeOrderStatusRequest request, StreamObserver<ChangeOrderStatusResponse> responseObserver) {
        OrderStatus status = OrderStatus.PENDING;
        if(request.getStatus().equals("cancelled")) {
            status = OrderStatus.CANCELLED;
        }
        if (request.getStatus().equals("paid")) {
            status = OrderStatus.PAID;
        }
        if (request.getStatus().equals("confirmed")) {
            status = OrderStatus.CONFIRMED;
        }
        if (request.getStatus().equals("completed")) {
            status = OrderStatus.COMPLETED;
        }


        String res = orderService.changeOrderStatus(request.getOrderId(), status);
        ChangeOrderStatusResponse response = ChangeOrderStatusResponse.newBuilder().setStatus(res).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getOderByTransactionId(GetOrderRequest request, StreamObserver<ListOrderResponse> responseObserver) {
        List<Order> orderList = orderService.getOrderByTransactionId(request.getTransactionId());

        List<OrderResponse> responses = orderList.stream()
                .map(order -> buildOrderResponse(order, mapOrderItemsToResponse(order)))
                .toList();

        responseObserver.onNext(ListOrderResponse.newBuilder().addAllOrders(responses).build());
        responseObserver.onCompleted();
    }

    private List<OrderItemResponse> mapOrderItemsToResponse(Order order) {
        return order.getOrderItems().stream()
                .map(item -> OrderItemResponse.newBuilder()
                        .setProductId(item.getProductId())
                        .setShopId(item.getShopId())
                        .setProductName(item.getProductName())
                        .setImageUrl(item.getImageUrl())
                        .setQuantity(item.getQuantity())
                        .setPrice(item.getPrice().toString())
                        .setDiscount(item.getDiscount())
                        .build())
                .collect(Collectors.toList());
    }


    private OrderResponse buildOrderResponse(Order order, List<OrderItemResponse> items) {
        return OrderResponse.newBuilder()
                .setOrderId(order.getId())
                .setUserId(order.getUserId())
                .setPaymentMethod(order.getPaymentType().name())
                .setShippingAddress(order.getShippingAddress())
                .setCreatedAt(
                        Timestamp.newBuilder()
                                .setSeconds(order.getCreatedAt().getTime() / 1000)
                                .setNanos((int) ((order.getCreatedAt().getTime() % 1000) * 1_000_000))
                                .build()
                )
                .setStatus(order.getStatus().name())
                .setTotalPrice(order.getTotalPrice().toString())
                .setTransactionId(order.getTransactionId())
                .addAllItems(items)
                .build();
    }


}
