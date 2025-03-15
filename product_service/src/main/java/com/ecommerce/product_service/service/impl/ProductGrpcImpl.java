package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.grpc.*;
import com.ecommerce.product_service.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class ProductGrpcImpl extends ProductServiceGrpc.ProductServiceImplBase {
    private final ProductService productService;

    @Override
    public void decreaseStock(DecreaseStockRequest request, StreamObserver<DecreaseStockResponse> responseObserver) {
        boolean success = productService.decreaseStock(request.getItemsList());

        DecreaseStockResponse response = DecreaseStockResponse.newBuilder()
                .setIsAvailable(success).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void increaseStock(IncreaseStockRequest request, StreamObserver<IncreaseStockResponse> responseObserver) {
        productService.increaseStock(request.getItemsList());

        responseObserver.onNext(IncreaseStockResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    /**
     */
    @Override
    public void createProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        com.ecommerce.product_service.dto.request.ProductRequest productRequest = com.ecommerce.product_service.dto.request.ProductRequest
                .builder()
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .price(new BigDecimal(request.getPrice()))
                .stock(request.getStock())
                .images(request.getImagesList().stream().toList())
                .categories(request.getCategoriesList().stream().toList())
                .build();

        com.ecommerce.product_service.dto.response.ProductResponse productResponse = productService.createProduct(productRequest);

        ProductResponse response = convertToProductResponse(productResponse);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     */
    @Override
    public void editProduct(EditProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        com.ecommerce.product_service.dto.request.EditProductRequest editProductRequest = com.ecommerce.product_service.dto.request.EditProductRequest
                .builder()
                .id(request.getId())
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .images(Optional.of(request.getImagesList().stream().toList()).orElse(null))
                .categories(Optional.of(request.getCategoriesList().stream().toList()).orElse(null))
                .price(new BigDecimal(request.getPrice()))
                .stock(request.getStock())
                .build();


        com.ecommerce.product_service.dto.response.ProductResponse productResponse = productService.editProduct(editProductRequest);

        ProductResponse response = convertToProductResponse(productResponse);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    /**
     */
    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        com.ecommerce.product_service.dto.request.DeleteProductRequest deleteProductRequest =
                com.ecommerce.product_service.dto.request.DeleteProductRequest.builder()
                        .productId(request.getId())
                        .shopId(request.getShopId())
                        .build();

        productService.deleteProductForSeller(deleteProductRequest);

        DeleteProductResponse response = DeleteProductResponse.newBuilder().setIsDelete("Deleted!").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    public ProductResponse convertToProductResponse(com.ecommerce.product_service.dto.response.ProductResponse productResponse) {
        return ProductResponse.newBuilder()
                .setId(productResponse.getId())
                .setShopId(productResponse.getShopId())
                .setName(productResponse.getName())
                .setDescription(productResponse.getDescription())
                .addAllCategories(productResponse.getCategories())
                .addAllImages(productResponse.getImages())
                .setPrice(productResponse.getPrice().toString())
                .setStock(productResponse.getStock())
                .setRating(productResponse.getRating())
                .setSoldCount(productResponse.getSoldCount())
                .build();
    }
}
