package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);
}
