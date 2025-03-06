package com.ecommerce.order_service.dto.request;

import com.ecommerce.order_service.util.OrderStatus;

@lombok.Data
@lombok.Getter
public class UpdateStatusRequest {
    private OrderStatus status;

}
