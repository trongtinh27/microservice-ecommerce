package com.ecommerce.shop_service.repository;

import com.ecommerce.shop_service.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Shop findShopByOwnerId(Long id);
    Shop findShopById(Long id);
}
