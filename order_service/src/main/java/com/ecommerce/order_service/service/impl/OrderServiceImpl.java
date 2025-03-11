package com.ecommerce.order_service.service.impl;

import com.ecommerce.event.dto.OrderResponse;
import com.ecommerce.order_service.service.ProductGrpcClient;
import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.request.PaymentRequest;
import com.ecommerce.order_service.dto.request.UpdateStatusRequest;
import com.ecommerce.order_service.dto.response.*;
import com.ecommerce.order_service.entity.Cart;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.InsufficientStockException;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.httpClient.PaymentClient;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.RedisCartService;
import com.ecommerce.order_service.service.ShopGrpcClient;
import com.ecommerce.order_service.util.OrderStatus;
import com.ecommerce.order_service.util.PaymentType;
import com.ecommerce.order_service.util.VNPayUtil;
import com.ecommerce.security.JwtService;
import com.ecommerce.product_service.grpc.ProductItem;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RedisCartService redisCartService;
    private final PaymentClient paymentClient;
    private final JwtService jwtService;
    private final ProductGrpcClient productGrpcClient;
    private final ShopGrpcClient shopGrpcClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Object createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        long userId = extractUserIdFromRequest(request);

        // Lấy thông tin giỏ hàng từ Redis
        CreateOrderResponse createOrderResponse = orderResponse(String.valueOf(userId));

        // Tạo danh sách kiểm tra tồn kho
        List<ProductItem> items = createOrderResponse.getItems().stream()
                .map(item -> ProductItem.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // Kiểm tra và giảm tồn kho
        if (!productGrpcClient.decreaseStock(items)) {
            throw new InsufficientStockException("Insufficient stock for one or more products.");
        }

        String transactionId = VNPayUtil.createTransaction(8); // Sinh transactionId
        Map<Long, List<CartItem>> itemsGroupedByShop = createOrderResponse.getItems().stream()
                .collect(Collectors.groupingBy(item -> Long.parseLong(item.getShopId())));

        List<Order> orders = new ArrayList<>();
        List<PaymentRequest.PaymentItem> paymentItems = new ArrayList<>();
        long totalAmount = 0L;

        for (var entry : itemsGroupedByShop.entrySet()) {
            long shopId = entry.getKey();
            List<CartItem> shopCartItems = entry.getValue();

            // Tạo Order
            Order order = buildOrder(userId, orderRequest, shopCartItems);
            order.setTransactionId(transactionId);
            order.setShopId(shopId);
            order.setStatus(orderRequest.getPaymentType() == PaymentType.COD
                    ? OrderStatus.CONFIRMED
                    : OrderStatus.PENDING);

            // Cộng dồn tổng tiền nếu không phải COD
            if (order.getStatus() == OrderStatus.PENDING) {
                totalAmount += order.getTotalPrice().longValue();
            }

            order = orderRepository.save(order);
            orders.add(order);

            // Thêm vào danh sách thanh toán
            paymentItems.add(PaymentRequest.PaymentItem.builder()
                    .userId(userId)
                    .amount(order.getTotalPrice())
                    .orderId(order.getId())
                    .shopId(shopId)
                    .build());
        }

        removeProductFromCart(String.valueOf(userId), items);

        // Xử lý thanh toán VNPay
        if (orderRequest.getPaymentType() == PaymentType.VNPAY) {
            return processVNPayPayment(transactionId, totalAmount, paymentItems);
        }

        // Tạo danh sách phản hồi đơn hàng
        List<OrderResponse> responses = orders.stream()
                .map(order -> buildOrderResponse(order, mapOrderItemsToResponse(order)))
                .collect(Collectors.toList());

        // Gửi Kafka event
        responses.forEach(orderResponse -> kafkaTemplate.send("new-order", orderResponse));

        return responses;
    }

    /**
     * Xử lý thanh toán VNPay
     */
    private Map<String, String> processVNPayPayment(String transactionId, long totalAmount,
                                                    List<PaymentRequest.PaymentItem> paymentItems) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .items(paymentItems)
                .transactionId(transactionId)
                .orderType("Order")
                .totalAmount(totalAmount)
                .paymentType(PaymentType.VNPAY)
                .build();

        String paymentUrl = paymentClient.pay(paymentRequest);
        return Map.of("transactionId", transactionId, "paymentUrl", paymentUrl);
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
        String token = request.getHeader("Authorization").substring("Bearer ".length());

        long shopId = shopGrpcClient.getShopId(token);

        Order order = orderRepository.findByIdAndShopId(id, shopId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(statusRequest.getStatus());
        orderRepository.save(order);

        return buildOrderResponse(order, mapOrderItemsToResponse(order));
    }

    @Override
    public OrderResponse cancelOrder(HttpServletRequest request, long id) {
        long userId = extractUserIdFromRequest(request);
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<ProductItem> items = order.getOrderItems().stream()
                .map(item -> ProductItem.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();
        productGrpcClient.increaseStock(items);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return buildOrderResponse(order, mapOrderItemsToResponse(order));
    }

    @Override
    public String changeOrderStatus(long id, OrderStatus orderStatus) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(orderStatus);

        if(orderStatus.equals(OrderStatus.CANCELLED)) {
            List<ProductItem> items = order.getOrderItems().stream()
                    .map(item -> ProductItem.newBuilder()
                            .setProductId(item.getProductId())
                            .setQuantity(item.getQuantity())
                            .build())
                    .toList();
            productGrpcClient.increaseStock(items);
        }

        orderRepository.save(order);
        return orderStatus.name();
    }

    @Override
    public List<Order> getOrderByTransactionId(String transactionId) {
        return orderRepository.getOrderByTransactionId(transactionId);
    }


    private long extractUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring("Bearer ".length());
        return jwtService.extractUserId(token);
    }

    private Order buildOrder(long userId, OrderRequest orderRequest, List<CartItem> cartItemList) {
        // Khởi tạo Order
        Order order = Order.builder()
                .userId(userId)
                .paymentType(orderRequest.getPaymentType())
                .shippingAddress(orderRequest.getShippingAddress())
                .totalPrice(BigDecimal.ZERO) // Khởi tạo tổng giá trị là 0
                .build();

        // Tính tổng giá và thêm từng OrderItem vào Order
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItemList) {
            BigDecimal itemTotalPrice = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())) // Giá của item = Đơn giá * Số lượng
                    .subtract(BigDecimal.valueOf(item.getDiscount() != 0.0 ? item.getDiscount() : 0.0)); // Trừ giảm giá (nếu có)

            // Khởi tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .productId(item.getProductId())
                    .shopId(Long.parseLong(item.getShopId()))
                    .productName(item.getProductName())
                    .imageUrl(item.getImage())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .discount(item.getDiscount() != 0.0 ? item.getDiscount() : 0.0)
                    .totalPrice(itemTotalPrice) // Tổng giá của item sau khi giảm giá
                    .build();

            // Thêm OrderItem vào danh sách item của Order
            order.addItems(orderItem);

            // Cộng tổng giá trị của item vào tổng giá trị của Order
            totalPrice = totalPrice.add(itemTotalPrice);
        }

        // Cập nhật tổng giá trị cho Order
        order.setTotalPrice(totalPrice);

        return order;

    }

    private List<OrderResponse.OrderItem> mapOrderItemsToResponse(Order order) {
        return order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItem.builder()
                        .productId(item.getProductId())
                        .shopId(item.getShopId())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .discount(item.getDiscount())
                        .build())
                .collect(Collectors.toList());
    }

    private CreateOrderResponse orderResponse(String userId) {
        // Lấy dữ liệu từ Redis
        Cart cartData = redisCartService.get(userId);

        if (cartData == null || cartData.getItems() == null || cartData.getItems().isEmpty())
            throw new ResourceNotFoundException("Cart is empty");

        // Tạo và trả về OrderResponse
        return CreateOrderResponse.builder()
                .userId(userId)
                .items(cartData.getItems().stream().filter(CartItem::isChecked).toList())
                .totalPrice(calculateTotalPrice(cartData))
                .build();
    }

    private void removeProductFromCart(String userId, List<ProductItem> cartItemList) {
        Cart cart = redisCartService.get(userId);
        List<String> productIds = cartItemList.stream().map(ProductItem::getProductId).toList();

        cart.getItems().removeIf(item -> productIds.contains(item.getProductId()));
        cart.setTotalPrice(BigDecimal.ZERO);

        redisCartService.save(cart);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (item.isChecked()) {
                BigDecimal itemTotal = item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .subtract(item.getDiscount() != 0.0 ? BigDecimal.valueOf(item.getDiscount()) : BigDecimal.ZERO);
                totalPrice = totalPrice.add(itemTotal);
            }
        }
        return totalPrice;
    }

    private OrderResponse buildOrderResponse(Order order, List<OrderResponse.OrderItem> items) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .paymentMethod("COD")
                .shippingAddress(order.getShippingAddress())
                .createAt(order.getCreatedAt())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .transactionId(order.getTransactionId())
                .items(items)
                .build();
    }
}