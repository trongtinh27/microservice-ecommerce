package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.request.AddressDTO;
import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.dto.request.UpdateUserRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.dto.response.PageResponse;
import com.ecommerce.user_service.dto.response.UserDetailResponse;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.util.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDetailsService userDetailsService();
    User getByUsername(String userName);
    long saveUser(SignUpRequest request, String roleName);
    UserDetailResponse updateUser(UpdateUserRequest request);
    // Address
    List<AddressResponse> getListAddress(HttpServletRequest request);
    List<AddressResponse> addAddress(HttpServletRequest request, AddressDTO addressDTO);
    List<AddressResponse> deleteAddress(HttpServletRequest request, long id);
    UserStatus changeStatus(long userId, UserStatus status);
    void deleteUser(long userId);
    UserDetailResponse getUser(long userId);
    UserDetailResponse getProfile(HttpServletRequest request);
    PageResponse<?> getAllUsersByRole(int pageNo, int pageSize, List<String> roles);
    List<String> findAllRolesByUserId(long userId);
    Set<String> findPermissionsByRoles(List<String> role);
    User getUserById(long userId);

}
