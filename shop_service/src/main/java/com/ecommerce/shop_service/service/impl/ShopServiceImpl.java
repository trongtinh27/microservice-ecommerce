package com.ecommerce.shop_service.service.impl;

import com.ecommerce.shop_service.dto.request.SignUpRequest;
import com.ecommerce.shop_service.dto.response.ShopDetailResponse;
import com.ecommerce.shop_service.dto.response.UserDetailResponse;
import com.ecommerce.shop_service.entity.Shop;
import com.ecommerce.shop_service.exception.AccountExistedException;
import com.ecommerce.shop_service.exception.InvalidDataException;
import com.ecommerce.shop_service.exception.ResourceNotFoundException;
import com.ecommerce.shop_service.repository.ShopRepository;
import com.ecommerce.shop_service.repository.httpClient.UserClient;
import com.ecommerce.shop_service.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final UserClient userClient;
    @Override
    public ShopDetailResponse sellerRegister(SignUpRequest request) {
        try {
            ResponseEntity<Long> response = userClient.sellerRegister(request);
            if (response == null || response.getBody() == null) {
                throw new InvalidDataException("Failed to register seller. Response is null.");
            }
            long sellerId = response.getBody();
            Shop shop = Shop.builder()
                .ownerId(sellerId)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        shop = shopRepository.save(shop);
        return ShopDetailResponse.builder()
                .id(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .description(shop.getDescription())
                .createDate(shop.getCreatedAt())
                .build();
        } catch (Exception e ){
            throw new AccountExistedException("Account already exists ");
        }
    }

    @Override
    public ShopDetailResponse getShopByOwner(long ownerId) {
        Shop shop = shopRepository.findShopByOwnerId(ownerId);

        if(shop == null) throw new ResourceNotFoundException("Shop not found");

        return ShopDetailResponse.builder()
                .id(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .description(shop.getDescription())
                .createDate(shop.getCreatedAt())
                .build();
    }
}
