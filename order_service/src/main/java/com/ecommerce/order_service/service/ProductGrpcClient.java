package com.ecommerce.order_service.service;

import com.ecommerce.product_service.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

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

    public boolean decreaseStock(List<ProductItem> items) {
        DecreaseStockRequest request = DecreaseStockRequest.newBuilder()
                .addAllItems(items)
                .build();

        DecreaseStockResponse response = productServiceStub.decreaseStock(request);
        return response.getIsAvailable();
    }

    public void increaseStock(List<ProductItem> items) {
        IncreaseStockRequest request = IncreaseStockRequest.newBuilder().addAllItems(items).build();
        IncreaseStockResponse response = productServiceStub.increaseStock(request);
        log.info("ProductGrpcClient increaseStock response: {}", response);
    }
}
