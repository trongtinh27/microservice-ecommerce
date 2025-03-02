package com.ecommerce.user_service.dto.request;

import com.ecommerce.user_service.dto.validator.EnumPattern;
import com.ecommerce.user_service.dto.validator.GenderSubset;
import com.ecommerce.user_service.dto.validator.PhoneNumber;
import com.ecommerce.user_service.util.Gender;
import com.ecommerce.user_service.util.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@ToString
public class SignUpRequest implements Serializable {

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


    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    @NotEmpty(message = "addresses can not empty")
    private Set<AddressDTO> addresses;

//    public SignUpRequest(String fullName, String email, String phone) {
//        this.fullName = fullName;
//        this.email = email;
//        this.phone = phone;
//    }
}
