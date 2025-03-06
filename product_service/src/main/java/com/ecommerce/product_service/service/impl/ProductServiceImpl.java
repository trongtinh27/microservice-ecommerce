package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.request.DeleteProductRequest;
import com.ecommerce.product_service.dto.request.EditProductRequest;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import com.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final JwtService jwtService;

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return ProductResponse.builder()
                    .shopId(product.getShopId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .categories(product.getCategories())
                    .images(product.getCategories())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .rating(product.getRating())
                    .soldCount(product.getSoldCount())
                    .build();
        }
        return null;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .images(request.getImages())
                .categories(request.getCategories())
                .rating(0.0)
                .soldCount(0)
                .build();
        product = productRepository.save(product);
        return ProductResponse.builder()
                .shopId(product.getShopId())
                .name(product.getName())
                .description(product.getDescription())
                .categories(product.getCategories())
                .images(product.getCategories())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .soldCount(product.getSoldCount())
                .build();
    }

    @Override
    public ProductResponse editProduct(EditProductRequest request) {
        Product product = productRepository.findBy_idAndShopId(request.getId(), request.getShopId())
                .orElseThrow(() ->  new RuntimeException("Product not found"));

        List<Runnable> updates = new ArrayList<>();

        addUpdateIfChanged(request.getName(), product::getName, product::setName, updates);
        addUpdateIfChanged(request.getDescription(), product::getDescription, product::setDescription, updates);
        addUpdateIfChanged(request.getCategories(), product::getCategories, product::setCategories, updates);
        addUpdateIfChanged(request.getImages(), product::getImages, product::setImages, updates);
        addUpdateIfChanged(request.getStock(), product::getStock, product::setStock, updates);
        addUpdateIfChanged(request.getPrice(), product::getPrice, product::setPrice, updates);

        if (!updates.isEmpty()) {
            updates.forEach(Runnable::run); // Cập nhật tất cả giá trị thay đổi
            productRepository.save(product);
        }

        return ProductResponse.builder()
                .shopId(product.getShopId())
                .name(product.getName())
                .description(product.getDescription())
                .categories(product.getCategories())
                .images(product.getCategories())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .soldCount(product.getSoldCount())
                .build();
    }

    @Override
    public void deleteProductForSeller(DeleteProductRequest request) {
        productRepository.deleteBy_idAndShopId(request.getProductId(), request.getShopId());
    }

    @Override
    public void deleteProductForAdmin(String id) {
        productRepository.deleteById(id);
    }

    private <T> void addUpdateIfChanged(T newValue, Supplier<T> currentGetter, Consumer<T> updater, List<Runnable> updates) {
        if (newValue != null && !newValue.equals(currentGetter.get())) {
            updates.add(() -> updater.accept(newValue));
        }
    }

}
