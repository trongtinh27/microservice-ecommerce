package com.ecommerce.product_service.dto.request;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
public class ProductRequest {
    private String shopId;
    private String name;
    private String description;
    private List<String> images;
    private List<String> categories;
    private BigDecimal price;
    private int stock;
}
