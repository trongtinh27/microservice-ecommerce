package com.ecommerce.shop_service.service.impl;

import com.ecommerce.event.dto.NotificationEvent;
import com.ecommerce.event.dto.ShopEvent;
import com.ecommerce.security.JwtService;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final UserClient userClient;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

            Map<String, Object> params = new HashMap<>();
            params.put("shopName", shop.getName());
            params.put("userName", request.getUsername());
            params.put("description", shop.getDescription());
            params.put("createdAt", shop.getCreatedAt());

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("Email")
                    .templateCode("ShopCreated")
                    .recipient(request.getEmail())
                    .data(params)
                    .build();

            kafkaTemplate.send("shop-created", notificationEvent);
            sendShopEvent(shop, request.getAddresses().toString());

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
    public ShopDetailResponse getShopByToken(String token) {
        long ownerId = getOwnerIdFromToken(token);
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

    @Override
    public long getShopIdByToken(String token) {
        return getShopByToken(token).getId();
    }

    @Override
    public long getOwnerId(long shopId) {
        return shopRepository.findShopById(shopId).getOwnerId();
    }

    private long getOwnerIdFromToken(String token) {
        try {
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            throw new InvalidDataException("Invalid token");
        }
    }

    private void sendShopEvent(Shop shop, String address) {
        kafkaTemplate.send("shop-event", ShopEvent.builder()
                .id(shop.getId().toString())
                .name(shop.getName())
                .description(shop.getDescription())
                .location(address)
                .rating(0.0)
                .operation("CREATE")
                .build());
    }


}
