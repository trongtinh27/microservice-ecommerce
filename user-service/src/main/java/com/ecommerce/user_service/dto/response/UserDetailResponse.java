package com.ecommerce.user_service.dto.response;

import com.ecommerce.user_service.util.Gender;
import com.ecommerce.user_service.util.UserStatus;
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

    private Gender gender;

    private String username;

    private UserStatus status;
}
