package com.ecommerce.shop_service.dto.request;


import com.ecommerce.shop_service.dto.validator.GenderSubset;
import com.ecommerce.shop_service.dto.validator.PhoneNumber;
import com.ecommerce.shop_service.util.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Getter
@ToString
public class SignUpRequest {

    @NotNull(message = "fullName must be not null") // Khong cho phep gia tri null
    private String fullName;

    @Email(message = "email invalid format") // Chi chap nhan nhung gia tri dung dinh dang email
    private String email;

    //@Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber(message = "phone invalid format")
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    //@Pattern(regexp = "^male|female|other$", message = "gender must be one in {male, female, other}")
    @GenderSubset(anyOf = {Gender.MALE, Gender.FEMALE})
    private Gender gender;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @NotEmpty(message = "addresses can not empty")
    private Set<AddressDTO> addresses;

    private String name;

    private String description;
}
