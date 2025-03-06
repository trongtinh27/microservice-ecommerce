package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.request.UpdateStatusRequest;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {

    Object createOrder(HttpServletRequest request, OrderRequest orderRequest);

    PageResponse<?> getOrdersUser(HttpServletRequest request, int pageNo, int pageSize);

    OrderResponse getOrderDetail(HttpServletRequest request, long id);

    OrderResponse updateStatusOrder(HttpServletRequest request, long id, UpdateStatusRequest statusRequest);

    OrderResponse cancelOrder(HttpServletRequest request, long id);
}
