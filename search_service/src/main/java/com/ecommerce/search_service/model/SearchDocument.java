package com.ecommerce.search_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String type; // "product" hoặc "shop"

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    // Các trường chỉ dành cho product
    @Field(type = FieldType.Double)
    private BigDecimal price; // Giá sản phẩm

    @Field(type = FieldType.Keyword)
    private String shopId; // ID của shop (nếu là sản phẩm)

    @Field(type = FieldType.Keyword)
    private List<String> images; // Ví dụ: danh mục, nhãn sản phẩm

    @Field(type = FieldType.Keyword)
    private List<String> categories; // Ví dụ: danh mục, nhãn sản phẩm

    // Các trường chỉ dành cho shop
    @Field(type = FieldType.Text)
    private String location; // Vị trí cửa hàng

    @Field(type = FieldType.Double)
    private Double rating; // Điểm đánh giá của shop


    public SearchDocument(String id, String product, String name, String description, BigDecimal price, String shopId, List<String> images, List<String> tags) {
        this.id = id;
        this.type = product;
        this.name = name;
        this.description = description;
        this.price = price;
        this.shopId = shopId;
        this.images = images;
        this.categories = tags;
    }
}
