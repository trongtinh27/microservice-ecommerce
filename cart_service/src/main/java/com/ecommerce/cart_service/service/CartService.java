package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.request.AddProductRequest;
import com.ecommerce.cart_service.dto.response.CreateOrderResponse;
import com.ecommerce.cart_service.entity.Cart;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {

    Cart getCart(HttpServletRequest request);

    void saveCart(Cart cart);

    Cart removeProduct(HttpServletRequest request, String productId);

    Cart clearCart(HttpServletRequest request);

    Cart addProductToCart(HttpServletRequest request, AddProductRequest requestBody);

    Cart updateProductInCart(HttpServletRequest request, AddProductRequest requestBody);

    Cart updateStatusProduct(HttpServletRequest request, String productId);


}
