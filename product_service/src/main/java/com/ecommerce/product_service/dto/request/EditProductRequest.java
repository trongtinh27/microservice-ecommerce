package com.ecommerce.product_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class EditProductRequest {
    @NotNull
    private String id;
    @NotNull
    private String shopId;
    private String name;
    private String description;
    private List<String> images;
    private List<String> categories;
    private BigDecimal price;
    private int stock;
}
