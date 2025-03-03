package com.ecommerce.product_service.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DeleteProductRequest {
    private String productId;
    private String shopId;
}
