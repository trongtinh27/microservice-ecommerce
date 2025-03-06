package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.response.CreateOrderResponse;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.httpClient.CartClient;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.util.OrderStatus;
import com.ecommerce.security.JwtService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final JwtService jwtService;

    @Override
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        long userId = extractUserIdFromRequest(request);

        // Lấy thông tin từ Cart Service
        CreateOrderResponse createOrderResponse = fetchCreateOrderResponseFromCart();

        // Nếu không có item trong giỏ, trả về null
        if (createOrderResponse == null || createOrderResponse.getItems().isEmpty()) {
            return null;
        }

        // Tạo "Order" từ dữ liệu nhận được
        Order order = buildOrder(userId, orderRequest, createOrderResponse);

        // Lưu Order vào database
        order = orderRepository.save(order);

        // Tạo danh sách các OrderItem để trả về trong OrderResponse
        List<OrderResponse.OrderItem> responseItems = mapOrderItemsToResponse(order);

        // Trả về OrderResponse
        return buildOrderResponse(order, responseItems);
    }

    private long extractUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring("Bearer ".length());
        return jwtService.extractUserId(token);
    }

    private CreateOrderResponse fetchCreateOrderResponseFromCart() {
        try {
            return cartClient.createOrder(); // Gọi đến CartService qua Feign
        } catch (FeignException e) {
            return null; // Xử lý lỗi từ Feign client
        }
    }

    private Order buildOrder(long userId, OrderRequest orderRequest, CreateOrderResponse createOrderResponse) {
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalPrice(createOrderResponse.getTotalPrice())
                .shippingAddress(orderRequest.getShippingAddress())
                .build();

        // Thêm từng OrderItem vào Order
        createOrderResponse.getItems().forEach(item -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(item.getProductId())
                    .shopId(Long.parseLong(item.getShopId()))
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .discount(item.getDiscount())
                    .totalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();

            order.addItems(orderItem); // Thêm item vào order
        });

        return order;
    }

    private List<OrderResponse.OrderItem> mapOrderItemsToResponse(Order order) {
        return order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItem.builder()
                        .productId(item.getProductId())
                        .shopId(item.getShopId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .discount(item.getDiscount())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderResponse buildOrderResponse(Order order, List<OrderResponse.OrderItem> items) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .transactionId(order.getTransactionId())
                .items(items)
                .build();
    }
}