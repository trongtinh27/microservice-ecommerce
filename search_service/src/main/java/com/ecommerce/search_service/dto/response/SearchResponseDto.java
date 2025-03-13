package com.ecommerce.search_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class SearchResponseDto {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private List<Shop> shops;
    private List<Product> products;



    @Builder
    @Data
    public static class Shop {
        private String id;
        private String name;
        private String description;
        private String location;
        private double rating;
    }

    @Builder
    @Data
    public static class Product {
        private String id;
        private String name;
        private String shopId;
        private String description;
        private BigDecimal price;
        private List<String> images;
        private List<String> categories;
    }
}
