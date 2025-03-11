package com.ecommerce.shop_service.service;

import com.ecommerce.product_service.grpc.ProductRequest;
import com.ecommerce.product_service.grpc.ProductServiceGrpc;
import com.ecommerce.product_service.grpc.EditProductRequest;
import com.ecommerce.product_service.grpc.DeleteProductRequest;
import com.ecommerce.shop_service.dto.response.ProductResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Slf4j
@Component
@Service
public class ProductGrpcClient {
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    @Value("${app.services.product-service.name}")
    private String host;
    @Value("${app.services.product-service.port}")
    private int port;

    public ProductGrpcClient() {
        // Không khởi tạo `ManagedChannel` ở đây vì `@Value` chưa được inject
    }

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // Lúc này `host` và `port` đã có giá trị
                .usePlaintext()
                .build();

        this.productServiceStub = ProductServiceGrpc.newBlockingStub(channel);
        log.info("ProductGrpcClient initialized with host: {} and port: {}", host, port);
    }

    public ProductResponse createProduct(com.ecommerce.shop_service.dto.request.ProductRequest productRequest) {

        ProductRequest request = ProductRequest.newBuilder()
                .setShopId(productRequest.getShopId())
                .setName(productRequest.getName())
                .setDescription(productRequest.getDescription())
                .setPrice(productRequest.getPrice().toString())
                .setStock(productRequest.getStock())
                .addAllCategories(productRequest.getCategories())
                .addAllImages(productRequest.getImages())
                .build();

        return convertToProductResponse(productServiceStub.createProduct(request));
    }


    public ProductResponse editProduct(com.ecommerce.shop_service.dto.request.EditProductRequest editProductRequest) {
        EditProductRequest request = EditProductRequest.newBuilder()
                .setId(editProductRequest.getId())
                .setShopId(editProductRequest.getShopId())
                .setName(editProductRequest.getName())
                .setDescription(editProductRequest.getDescription())
                .setPrice(editProductRequest.getPrice().toString())
                .setStock(editProductRequest.getStock())
                .addAllCategories(editProductRequest.getCategories())
                .addAllImages(editProductRequest.getImages())
                .build();

        return convertToProductResponse(productServiceStub.editProduct(request));
    }

    public String deleteProduct(com.ecommerce.shop_service.dto.request.DeleteProductRequest deleteProductRequest) {
        DeleteProductRequest request = DeleteProductRequest.newBuilder()
                .setId(deleteProductRequest.getProductId())
                .setShopId(deleteProductRequest.getShopId())
                .build();

        return productServiceStub.deleteProduct(request).getIsDelete();
    }


    public ProductResponse convertToProductResponse(com.ecommerce.product_service.grpc.ProductResponse productResponse) {
        return ProductResponse.builder()
                .id(productResponse.getId())
                .shopId(productResponse.getShopId())
                .name(productResponse.getName())
                .description(productResponse.getDescription())
                .categories(productResponse.getCategoriesList())
                .images(productResponse.getImagesList())
                .stock(productResponse.getStock())
                .price(new BigDecimal(productResponse.getPrice()))
                .rating(productResponse.getRating())
                .soldCount(productResponse.getSoldCount())
                .build();
    }

}
