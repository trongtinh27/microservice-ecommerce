package com.ecommerce.search_service.controller;

import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.event.dto.ShopEvent;
import com.ecommerce.search_service.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchListener {

    private final SearchService searchService;

    @KafkaListener(topics = "product-event", groupId = "search-group")
    public void productListener(ProductEvent productEvent) {
        System.out.println("ProductEvent: " + productEvent);
        try {
            if("DELETE".equals(productEvent.getOperation())) {
                searchService.delete(productEvent.getId());
                return;
            }
            // Tạo tài liệu từ sự kiện
            searchService.productEvent(productEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "shop-event", groupId = "search-group")
    public void shopListener(ShopEvent shopEvent) {
        System.out.println("ShopEvent: " + shopEvent);
        try {
            if("DELETE".equals(shopEvent.getOperation())) {
                searchService.delete(shopEvent.getId());
                return;
            }
            // Tạo tài liệu từ sự kiện
            searchService.shopEvent(shopEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
