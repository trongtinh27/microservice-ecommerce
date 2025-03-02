package com.ecommerce.shop_service.service.impl;

import com.ecommerce.shop_service.dto.request.ProductRequest;
import com.ecommerce.shop_service.dto.response.ProductResponse;
import com.ecommerce.shop_service.dto.response.ShopDetailResponse;
import com.ecommerce.shop_service.dto.response.UserDetailResponse;
import com.ecommerce.shop_service.exception.ResourceNotFoundException;
import com.ecommerce.shop_service.repository.httpClient.ProductClient;
import com.ecommerce.shop_service.repository.httpClient.UserClient;
import com.ecommerce.shop_service.service.ProductService;
import com.ecommerce.shop_service.service.ShopService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductClient productClient;
    private final UserClient userClient;
    private final ShopService shopService;
    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        UserDetailResponse owner = userClient.getProfile().getBody();
        if(owner == null) throw new ResourceNotFoundException("Owner not found");
        ShopDetailResponse shopDetailResponse = shopService.getShopByOwner(owner.getId());
        productRequest.setShopId(String.valueOf(shopDetailResponse.getId()));

        return productClient.createProduct(productRequest).getBody();
    }
}
