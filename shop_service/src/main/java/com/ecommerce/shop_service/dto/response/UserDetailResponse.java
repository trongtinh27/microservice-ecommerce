package com.ecommerce.shop_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class UserDetailResponse implements Serializable {
    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private String username;
}
