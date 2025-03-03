package com.ecommerce.product_service.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class EditProductRequest {
    private String id;
    private String shopId;
    private String name;
    private String description;
    private List<String> images;
    private List<String> categories;
    private BigDecimal price;
    private int stock;
}
