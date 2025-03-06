package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.request.UpdateStatusRequest;
import com.ecommerce.order_service.dto.response.CreateOrderResponse;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.dto.response.PageResponse;
import com.ecommerce.order_service.dto.response.ShopDetailResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.httpClient.CartClient;
import com.ecommerce.order_service.repository.httpClient.ShopClient;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.util.OrderStatus;
import com.ecommerce.security.JwtService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final ShopClient shopClient;
    private final JwtService jwtService;

    @Override
    public Object createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        long userId = extractUserIdFromRequest(request);

        // Lấy thông tin từ Cart Service
        CreateOrderResponse createOrderResponse = fetchCreateOrderResponseFromCart();

        // Nếu không có item trong giỏ, trả về null
        if (createOrderResponse == null || createOrderResponse.getItems().isEmpty()) {
            return null;
        }

        // Tạo "Order" từ dữ liệu nhận được
        Order order = buildOrder(userId, orderRequest, createOrderResponse);

        // Sinh transactionId duy nhất
        String transactionId = UUID.randomUUID().toString(); // Sinh transactionId duy nhất

        order.setTransactionId(transactionId);

        if ("COD".equals(orderRequest.getPaymentType())) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }


        // Lưu Order vào database
        order = orderRepository.save(order);


        if ("COD".equalsIgnoreCase(String.valueOf(orderRequest.getPaymentType()))) {
            List<OrderResponse.OrderItem> responseItems = mapOrderItemsToResponse(order);
            return buildOrderResponse(order, responseItems);
        }


        // Thanh toán VNPay: Gọi Payment Service để trả về đường link thanh toán
        if ("VNPay".equalsIgnoreCase(String.valueOf(orderRequest.getPaymentType()))) {
            // Gọi Payment Service để tạo VNPay URL
//            String paymentUrl = paymentService.createVNPayPayment(transactionId, order.getTotalPrice());
            String paymentUrl = "paymentService.createVNPayPayment(transactionId, order.getTotalPrice())";
            return Map.of(
                    "transactionId", transactionId,
                    "paymentUrl", paymentUrl
            );
        }
        return null;

    }

    @Override
    public PageResponse<?> getOrdersUser(HttpServletRequest request, int pageNo, int pageSize) {
        long userId = extractUserIdFromRequest(request);
        Specification<Order> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
        Page<Order> orderList = orderRepository.findAll(spec, PageRequest.of(pageNo, pageSize));

        List<OrderResponse> orders = new ArrayList<>();

        orderList.forEach(order -> {
            List<OrderResponse.OrderItem> items = mapOrderItemsToResponse(order);
            OrderResponse orderResponse = buildOrderResponse(order, items);
            orders.add(orderResponse);
        });



        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(orderList.getTotalPages())
                .items(orders)
                .build();
    }

    @Override
    public OrderResponse getOrderDetail(HttpServletRequest request, long id) {
        long userId = extractUserIdFromRequest(request);
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderResponse.OrderItem> items = mapOrderItemsToResponse(order);

        return buildOrderResponse(order, items);
    }

    @Override
    public OrderResponse updateStatusOrder(HttpServletRequest request, long id, UpdateStatusRequest statusRequest) {

        try {
            ShopDetailResponse shopDetailResponse = shopClient.getShop();

            Order order = orderRepository.findByIdAndShopId(id, shopDetailResponse.getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            order.setStatus(statusRequest.getStatus());
            orderRepository.save(order);

            return buildOrderResponse(order, mapOrderItemsToResponse(order));
        } catch (FeignException e) {
            return null;
        }

    }

    @Override
    public OrderResponse cancelOrder(HttpServletRequest request, long id) {
        long userId = extractUserIdFromRequest(request);
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        return buildOrderResponse(order, mapOrderItemsToResponse(order));
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