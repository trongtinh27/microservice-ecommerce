package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.grpc.UserIdRequest;
import com.ecommerce.user_service.grpc.UserResponse;
import com.ecommerce.user_service.grpc.UserServiceGrpc;
import com.ecommerce.user_service.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserService userService;

    @Override
    public void getUser(UserIdRequest request, StreamObserver<UserResponse> streamObserver) {
        User user = userService.getUserById(request.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserResponse response = UserResponse.newBuilder()
                .setUserName(user.getUsername())
                .setEmail(user.getEmail())
                .setFullName(user.getFullName())
                .setPhoneNumber(user.getPhone())
                .build();
        streamObserver.onNext(response);
        streamObserver.onCompleted();
    }


}
