package com.ecommerce.shop_service.controller;


import com.ecommerce.shop_service.dto.response.ShopDetailResponse;
import com.ecommerce.shop_service.service.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shops")
public class ShopController {
    private final ShopService shopService;


    @GetMapping("/")
    public ShopDetailResponse getShop(HttpServletRequest request) {
        return shopService.getShopByToken(request.getHeader("Authorization"));
    }
}
