package com.ecommerce.shop_service.service;

import com.ecommerce.shop_service.dto.request.SignUpRequest;
import com.ecommerce.shop_service.dto.response.ShopDetailResponse;

public interface ShopService {


    ShopDetailResponse sellerRegister(SignUpRequest request);

    ShopDetailResponse getShopByOwner(long ownerId);
}
