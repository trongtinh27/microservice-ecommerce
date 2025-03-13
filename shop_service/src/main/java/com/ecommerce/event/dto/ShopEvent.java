package com.ecommerce.event.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ShopEvent {
    private String id; // shop id
    private String name;
    private String description;
    private String location;
    private Double rating;
    private String operation; // "CREATE", "UPDATE", "DELETE"
}