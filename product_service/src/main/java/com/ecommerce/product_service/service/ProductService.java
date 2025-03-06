package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.DeleteProductRequest;
import com.ecommerce.product_service.dto.request.EditProductRequest;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse getProduct(String id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse editProduct(EditProductRequest request);
    void deleteProductForSeller(DeleteProductRequest request);
    void deleteProductForAdmin(String id);
}
