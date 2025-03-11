package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.request.UpdateStatusRequest;
import com.ecommerce.event.dto.OrderResponse;
import com.ecommerce.order_service.dto.response.PageResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.util.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {

    Object createOrder(HttpServletRequest request, OrderRequest orderRequest);

    PageResponse<?> getOrdersUser(HttpServletRequest request, int pageNo, int pageSize);

    OrderResponse getOrderDetail(HttpServletRequest request, long id);

    OrderResponse updateStatusOrder(HttpServletRequest request, long id, UpdateStatusRequest statusRequest);

    OrderResponse cancelOrder(HttpServletRequest request, long id);

    String changeOrderStatus(long id, OrderStatus orderStatus);

    List<Order> getOrderByTransactionId(String transactionId);
}
