package com.ecommerce.shop_service.service.impl;


import com.ecommerce.shop_service.dto.request.DeleteProductRequest;
import com.ecommerce.shop_service.dto.request.EditProductRequest;
import com.ecommerce.shop_service.dto.request.ProductRequest;
import com.ecommerce.shop_service.dto.response.ProductResponse;
import com.ecommerce.shop_service.dto.response.ShopDetailResponse;
import com.ecommerce.shop_service.exception.InvalidDataException;
import com.ecommerce.shop_service.exception.ResourceExistedException;
import com.ecommerce.shop_service.exception.ResourceNotFoundException;
import com.ecommerce.shop_service.service.ProductGrpcClient;
import com.ecommerce.shop_service.service.ProductService;
import com.ecommerce.shop_service.service.ShopService;
import feign.FeignException;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductGrpcClient productClient;
    private final ShopService shopService;

    @Override
    public ProductResponse addProduct(HttpServletRequest httpServletRequest, ProductRequest productRequest) {
        String token = httpServletRequest.getHeader("Authorization").substring("Bearer ".length());
        ShopDetailResponse shopDetailResponse = shopService.getShopByToken(token);
        productRequest.setShopId(String.valueOf(shopDetailResponse.getId()));

        try {
            return productClient.createProduct(productRequest);
        } catch (StatusRuntimeException e) {
            throw new ResourceExistedException("Product is existed!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductResponse editProduct(HttpServletRequest httpServletRequest, String id, EditProductRequest request) {
        String token = httpServletRequest.getHeader("Authorization").substring("Bearer ".length());
        ShopDetailResponse shopDetailResponse = shopService.getShopByToken(token);
        request.setId(id);
        request.setShopId(String.valueOf(shopDetailResponse.getId()));

        try {
            return productClient.editProduct(request);
        } catch (FeignException e){
            throw new ResourceNotFoundException("Product not found");
        }
    }

    @Override
    public String deleteProduct(HttpServletRequest httpServletRequest, String id) {
        String token = httpServletRequest.getHeader("Authorization").substring("Bearer ".length());
        ShopDetailResponse shopDetailResponse = shopService.getShopByToken(token);
        DeleteProductRequest request = DeleteProductRequest.builder()
                .productId(id)
                .shopId(String.valueOf(shopDetailResponse.getId()))
                .build();

        return productClient.deleteProduct(request);
    }

}
