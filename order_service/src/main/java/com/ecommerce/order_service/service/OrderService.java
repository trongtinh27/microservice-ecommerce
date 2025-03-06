package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.entity.Order;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {

    OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest);
}
