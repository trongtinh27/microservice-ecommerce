package com.ecommerce.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;
}
