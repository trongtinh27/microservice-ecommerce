package com.ecommerce.search_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchRequestDto {
    private String query; // Từ khóa tìm kiếm
    private String type; // "product" hoặc "shop"
    private List<String> categories; // Lọc theo danh mục
    private BigDecimal minPrice; // Giá tối thiểu (nếu tìm sản phẩm)
    private BigDecimal maxPrice; // Giá tối đa
    private String sortField; // "price" hoặc "rating"
    private String sortOrder; // "asc" hoặc "desc"
}
