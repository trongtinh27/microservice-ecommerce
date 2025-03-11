package com.ecommerce.shop_service.service;


import com.ecommerce.shop_service.dto.request.EditProductRequest;
import com.ecommerce.shop_service.dto.request.ProductRequest;
import com.ecommerce.shop_service.dto.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ProductService {
    ProductResponse addProduct(HttpServletRequest httpServletRequest, ProductRequest productRequest);

    ProductResponse editProduct(HttpServletRequest httpServletRequest, String id, EditProductRequest productRequest);

    String deleteProduct(HttpServletRequest httpServletRequest, String id);


}
