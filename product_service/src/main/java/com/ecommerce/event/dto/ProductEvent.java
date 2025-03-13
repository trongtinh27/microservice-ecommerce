package com.ecommerce.event.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductEvent {
    private String id; // product id
    private String name;
    private String description;
    private BigDecimal price;
    private String shopId;
    private List<String> images;
    private List<String> tags;
    private String operation; // "CREATE", "UPDATE", "DELETE"
}
