package com.ecommerce.notification_service.service;

import com.ecommerce.user_service.grpc.UserIdRequest;
import com.ecommerce.user_service.grpc.UserResponse;
import com.ecommerce.user_service.grpc.UserServiceGrpc;
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
public class UserGrpcClient {
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @Value("${app.services.user-service.name}")
    private String host;
    @Value("${app.services.user-service.port}")
    private int port;

    public UserGrpcClient() {

    }

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // Lúc này `host` và `port` đã có giá trị
                .usePlaintext()
                .build();

        this.userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
        log.info("ProductGrpcClient initialized with host: {} and port: {}", host, port);
    }

    public UserResponse getUser(long userId) {
        return userServiceBlockingStub.getUser(UserIdRequest.newBuilder().setUserId(userId).build());
    }
}
