package com.ecommerce.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddressResponse {
    private Long id;
    private String apartmentNumber;
    private String floor;
    private String building;
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    private Integer addressType;
}
