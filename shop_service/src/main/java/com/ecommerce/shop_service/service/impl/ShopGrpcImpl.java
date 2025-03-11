package com.ecommerce.shop_service.service.impl;

import com.ecommerce.shop_service.grpc.*;
import com.ecommerce.shop_service.service.ShopService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ShopGrpcImpl extends ShopServiceGrpc.ShopServiceImplBase {
    private final ShopService shopService;

    @Override
    public void getShopId(GetShopIdRequest request, StreamObserver<GetShopIdResponse> responseStreamObserver) {
        long shopId = shopService.getShopIdByToken(request.getToken());

        responseStreamObserver.onNext(GetShopIdResponse.newBuilder().setShopId(shopId).build());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getOwnerId(GetOwnerIdRequest request, StreamObserver<GetOwnerIdResponse> responseStreamObserver) {
        long ownerId = shopService.getOwnerId(request.getShopId());

        responseStreamObserver.onNext(GetOwnerIdResponse.newBuilder().setOwnerId(ownerId).build());
        responseStreamObserver.onCompleted();
    }

}
