package com.ecommerce.order_service.service;

import com.ecommerce.product_service.grpc.ProductServiceGrpc;
import com.ecommerce.shop_service.grpc.GetShopIdRequest;
import com.ecommerce.shop_service.grpc.ShopServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class ShopGrpcClient {

    @Value("${app.services.shop-service.name}")
    private String host;
    @Value("${app.services.shop-service.port}")
    private int port;
    private ShopServiceGrpc.ShopServiceBlockingStub shopServiceStub;

    public ShopGrpcClient() {
    }

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // Lúc này `host` và `port` đã có giá trị
                .usePlaintext()
                .build();

        this.shopServiceStub = ShopServiceGrpc.newBlockingStub(channel);
        log.info("ProductGrpcClient initialized with host: {} and port: {}", host, port);
    }

    public long getShopId(String token) {
        return shopServiceStub.getShopId(GetShopIdRequest.newBuilder().setToken(token).build()).getShopId();
    }

}
