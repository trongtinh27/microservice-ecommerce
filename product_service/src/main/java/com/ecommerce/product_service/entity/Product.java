package com.ecommerce.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
public class Product implements Serializable {
    @Id
    private String _id;
    private String name;
    private String description;
    private BigDecimal price;
    private String shopId;
    private List<String> categories;
    private List<String> images;
    private boolean available;
    private int stock;
    private double rating;
    private int soldCount;
}
