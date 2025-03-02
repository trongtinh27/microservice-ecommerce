package com.ecommerce.user_service.dto.response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PageResponse<T> {

    int pageNo;
    int pageSize;
    int totalPage;
    T items;
}