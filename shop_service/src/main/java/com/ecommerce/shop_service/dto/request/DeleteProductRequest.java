package com.ecommerce.shop_service.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class DeleteProductRequest {
    private String productId;
    private String shopId;
}
