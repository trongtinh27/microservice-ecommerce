package com.ecommerce.notification_service.service;

import com.ecommerce.shop_service.grpc.GetOwnerIdRequest;
import com.ecommerce.shop_service.grpc.ShopServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Service
public class ShopGrpcClient extends ShopServiceGrpc.ShopServiceImplBase {
    private ShopServiceGrpc.ShopServiceBlockingStub shopServiceBlockingStub;
    @Value("${app.services.shop-service.name}")
    private String host;
    @Value("${app.services.shop-service.port}")
    private int port;


    public ShopGrpcClient() {}

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // Lúc này `host` và `port` đã có giá trị
                .usePlaintext()
                .build();

        this.shopServiceBlockingStub = ShopServiceGrpc.newBlockingStub(channel);
        log.info("ProductGrpcClient initialized with host: {} and port: {}", host, port);
    }

    public long getOwnerId(long shopId) {

        return shopServiceBlockingStub.getOwnerId(GetOwnerIdRequest.newBuilder().setShopId(shopId).build()).getOwnerId();
    }

}
