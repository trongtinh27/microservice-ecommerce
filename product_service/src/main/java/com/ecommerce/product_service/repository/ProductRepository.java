package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBy_idAndShopId(String id, String shopId);

    void deleteBy_idAndShopId(String id, String shopId);

    boolean existsProductByNameAndShopId(@NotNull String name, @NotNull String shopId);
}
