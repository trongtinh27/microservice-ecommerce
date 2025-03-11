package com.ecommerce.cart_service.service.impl;

import com.ecommerce.cart_service.dto.request.AddProductRequest;
import com.ecommerce.cart_service.dto.response.CreateOrderResponse;
import com.ecommerce.cart_service.dto.response.ProductResponse;
import com.ecommerce.cart_service.entity.Cart;
import com.ecommerce.cart_service.entity.CartItem;
import com.ecommerce.cart_service.exception.ResourceNotFoundException;
import com.ecommerce.cart_service.repository.httpClient.ProductClient;
import com.ecommerce.cart_service.service.CartService;
import com.ecommerce.cart_service.service.RedisCartService;
import com.ecommerce.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisCartService redisCartService;
    private final JwtService jwtService;
    private final ProductClient productClient;

    @Override
    public Cart getCart(HttpServletRequest request) {
        try {
            long userId = jwtService.extractUserId(request.getHeader("Authorization").substring("Bearer ".length()));
            return redisCartService.get(String.valueOf(userId));

        } catch (Exception e) {
            throw new ResourceNotFoundException("Cart not found");

        }
    }

    @Override
    public void saveCart(Cart cart) {
        redisCartService.save(cart);
    }

    @Override
    public Cart clearCart(HttpServletRequest request) {
        long userId = jwtService.extractUserId(request.getHeader("Authorization").substring("Bearer ".length()));

        if(!redisCartService.isExists(String.valueOf(userId))) throw new ResourceNotFoundException("Cart not found");
        Cart cart = getCart(request);
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.valueOf(0));
        redisCartService.remove(String.valueOf(userId));
        return cart;
    }

    @Override
    public Cart addProductToCart(HttpServletRequest request, AddProductRequest requestBody) {
        long userId = jwtService.extractUserId(request.getHeader("Authorization").substring("Bearer ".length()));

        Cart cart = getCart(request);

        if(cart == null) {
            cart = Cart.builder()
                    .userId(String.valueOf(userId))
                    .build();
            saveCart(cart);
        }
        Map<String, CartItem> itemMap;

        if(cart.getItems() == null) {
            itemMap = new HashMap<>();
        } else {
            itemMap = cart.getItems().stream()
                    .collect(Collectors.toMap(CartItem::getProductId, item -> item));
        }

        ProductResponse product = productClient.getProducts(requestBody.getProductId());
        if(product != null) {
            CartItem existingItem = itemMap.get(requestBody.getProductId());

            // Cập nhật số lượng khi sản phẩm có trong giỏ hàng
            if(existingItem != null) {
                int checked = checkQuantity(requestBody.getQuantity()+existingItem.getQuantity(), product.getStock());
                existingItem.setQuantity(checked);

            } else {
            // Thêm sản phẩm mới vào giỏ hàng
                CartItem newItem = CartItem.builder()
                        .productId(requestBody.getProductId())
                        .productName(product.getName())
                        .shopId(product.getShopId())
                        .image(product.getImages().get(0))
                        .price(product.getPrice())
                        .discount(0.0)
                        .quantity(checkQuantity(requestBody.getQuantity(), product.getStock()))
                        .maxQuantity(product.getStock())
                        .isChecked(true)
                        .build();
                itemMap.put(requestBody.getProductId(), newItem);
            }
        }
        // Cập nhật lại danh sách items từ HashMap
        cart.setItems(new ArrayList<>(itemMap.values()));

        cart.setTotalPrice(calculateTotalPrice(cart));




        // Lưu giỏ hàng lại vào Redis
        saveCart(cart);

        return cart;
    }

    @Override
    public Cart updateProductInCart(HttpServletRequest request, AddProductRequest requestBody) {
        Cart cart = getCart(request);

        Map<String, CartItem> itemMap = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, item -> item));

        if(itemMap.containsKey(requestBody.getProductId())) {
            CartItem item = itemMap.get(requestBody.getProductId());
            item.setQuantity(requestBody.getQuantity());
            cart.setItems(new ArrayList<>(itemMap.values()));
            cart.setTotalPrice(calculateTotalPrice(cart));
        }

        saveCart(cart);
        return cart;
    }

    @Override
    public Cart removeProduct(HttpServletRequest request, String productId) {
        Cart cart = getCart(request);
        if(cart != null) {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
            cart.setTotalPrice(calculateTotalPrice(cart));
            saveCart(cart);
        }
        return cart;
    }

    @Override
    public Cart updateStatusProduct(HttpServletRequest request, String productId) {
        Cart cart = getCart(request);
        if(cart != null) {
            cart.getItems()
                    .forEach(item -> {
                        if(item.getProductId().equals(productId)) item.setChecked(!item.isChecked());
                    });
            cart.setTotalPrice(calculateTotalPrice(cart));
            saveCart(cart);
        }
        return cart;
    }


    private int checkQuantity(int quantity, int maxQuantity) {
        return Math.min(quantity, maxQuantity);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (item.isChecked()) {
                BigDecimal itemTotal = item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .subtract(item.getDiscount() != 0.0 ? BigDecimal.valueOf(item.getDiscount()) : BigDecimal.ZERO);
                totalPrice = totalPrice.add(itemTotal);
            }
        }
        return totalPrice;
    }
}
