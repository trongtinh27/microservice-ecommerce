package com.ecommerce.user_service.dto.request;

import com.ecommerce.user_service.dto.validator.GenderSubset;
import com.ecommerce.user_service.dto.validator.PhoneNumber;
import com.ecommerce.user_service.util.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
public class UpdateUserRequest {
    private long userId;
    private String fullName;
    private String email;
    @PhoneNumber(message = "phone invalid format")
    private String phone;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;
    @GenderSubset(anyOf = {Gender.MALE, Gender.FEMALE})
    private Gender gender;

}
