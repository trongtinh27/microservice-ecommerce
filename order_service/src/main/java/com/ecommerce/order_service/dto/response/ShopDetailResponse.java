package com.ecommerce.order_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ShopDetailResponse {
    private long id;
    private long ownerId;
    private String name;
    private String description;
    private Date createDate;
}
