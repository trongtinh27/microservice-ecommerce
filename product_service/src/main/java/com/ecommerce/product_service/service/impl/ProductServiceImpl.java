package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .images(request.getImages())
                .categories(request.getCategories())
                .rating(0.0)
                .soldCount(0)
                .build();
        product = productRepository.save(product);
        return ProductResponse.builder()
                .shopId(product.getShopId())
                .name(product.getName())
                .description(product.getDescription())
                .categories(product.getCategories())
                .images(product.getCategories())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .soldCount(product.getSoldCount())
                .build();
    }
}
