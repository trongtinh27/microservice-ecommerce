package com.ecommerce.shop_service.util;

import com.fasterxml.jackson.annotation.JsonProperty;
public enum Gender {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE,
}
