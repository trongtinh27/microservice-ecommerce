package com.ecommerce.product_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class ProductResponse {
    private String shopId;
    private String name;
    private String description;
    private List<String> images;
    private List<String> categories;
    private BigDecimal price;
    private int stock;
    private double rating;
    private int soldCount;
}
