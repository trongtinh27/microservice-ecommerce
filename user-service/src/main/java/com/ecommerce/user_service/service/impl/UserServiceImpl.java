package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.request.AddressDTO;
import com.ecommerce.user_service.dto.request.SignUpRequest;
import com.ecommerce.user_service.dto.request.UpdateUserRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.dto.response.PageResponse;
import com.ecommerce.user_service.dto.response.UserDetailResponse;
import com.ecommerce.user_service.entity.Address;
import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.entity.UserHasRole;
import com.ecommerce.user_service.exception.AccountExistedException;
import com.ecommerce.user_service.exception.ResourceNotFoundException;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.JwtService;
import com.ecommerce.user_service.service.UserService;
import com.ecommerce.user_service.util.UserStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ecommerce.user_service.util.UserStatus.*;
import static com.ecommerce.user_service.util.TokenType.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;


    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getByUsername(String userName) {
        return userRepository.findByUsername(userName).orElse(null);
    }

    private boolean isExistAccount(String userName) {
        boolean isExist = false;

        User user = getByUsername(userName);
        if(user != null) {
            isExist = true;
        }

        return isExist;
    }

    @Override
    public long saveUser(SignUpRequest request, String roleName) {
        if(isExistAccount(request.getUsername())) {
            throw new AccountExistedException("Account already exists ");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = User.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(ACTIVE)
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        // Tạo UserHasRole và liên kết với user + role
        UserHasRole userHasRole = new UserHasRole();
        userHasRole.setUser(user);
        userHasRole.setRole(role);
        user.saveRoles(userHasRole);
        userRepository.save(user);

        log.info("{} has been added successfully with role {}", user.getUsername(), role);
        return user.getId();
    }

    @Transactional
    @Override
    public UserDetailResponse updateUser(UpdateUserRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Dùng Map với generic <T> để đảm bảo kiểu dữ liệu đúng
        List<Runnable> updates = new ArrayList<>();

        addUpdateIfChanged(request.getFullName(), user::getFullName, user::setFullName, updates);

        addUpdateIfChanged(request.getEmail(), user::getEmail, user::setEmail, updates);
        addUpdateIfChanged(request.getPhone(), user::getPhone, user::setPhone, updates);
        addUpdateIfChanged(request.getDateOfBirth(), user::getDateOfBirth, user::setDateOfBirth, updates);
        addUpdateIfChanged(request.getGender(), user::getGender, user::setGender, updates);

        if (!updates.isEmpty()) {
            updates.forEach(Runnable::run); // Cập nhật tất cả giá trị thay đổi
            userRepository.save(user);
        }
        log.info("User has updated successfully, userId={}", request.getUserId());

        return UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }

    @Override
    public List<AddressResponse> getListAddress(HttpServletRequest request) {
        String userName = getUsername(request);
        var user = getByUsername(userName);
        if(user == null) throw new ResourceNotFoundException("User not found");
        Set<Address> addresses = user.getAddresses();
        List<AddressResponse> addressesResponse = new ArrayList<>();
        addresses.forEach(address ->
                addressesResponse.add(AddressResponse.builder()
                                .id(address.getId())
                                .apartmentNumber(address.getApartmentNumber())
                                .floor(address.getBuilding())
                                .streetNumber(address.getStreetNumber())
                                .street(address.getStreet())
                                .city(address.getCity())
                                .country(address.getCountry())
                                .addressType(address.getAddressType())
                        .build()));
        return addressesResponse;
    }

    @Override
    public List<AddressResponse> addAddress(HttpServletRequest request, AddressDTO addressDTO) {
        String userName = getUsername(request);
        var user = getByUsername(userName);
        if(user == null) throw new ResourceNotFoundException("User not found");
        user.saveAddress(Address.builder()
                .apartmentNumber(addressDTO.getApartmentNumber())
                .floor(addressDTO.getFloor())
                .building(addressDTO.getBuilding())
                .streetNumber(addressDTO.getStreetNumber())
                .street(addressDTO.getStreet())
                .city(addressDTO.getCity())
                .country(addressDTO.getCountry())
                .addressType(addressDTO.getAddressType())
                .build());
        user = userRepository.save(user);
        List<AddressResponse> addressResponses = new ArrayList<>();
        user.getAddresses().forEach(address ->
                addressResponses.add(AddressResponse.builder()
                        .id(address.getId())
                        .apartmentNumber(address.getApartmentNumber())
                        .floor(address.getBuilding())
                        .streetNumber(address.getStreetNumber())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .country(address.getCountry())
                        .addressType(address.getAddressType())
                        .build()));
        return addressResponses;

    }

    @Override
    public List<AddressResponse> deleteAddress(HttpServletRequest request, long id) {
        String userName = getUsername(request);
        var user = getByUsername(userName);
        if(user == null) throw new ResourceNotFoundException("User not found");
        user.getAddresses().removeIf(address -> address.getId()==id);

        user = userRepository.save(user);
        List<AddressResponse> addressResponses = new ArrayList<>();
        user.getAddresses().forEach(address ->
                addressResponses.add(AddressResponse.builder()
                        .id(address.getId())
                        .apartmentNumber(address.getApartmentNumber())
                        .floor(address.getBuilding())
                        .streetNumber(address.getStreetNumber())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .country(address.getCountry())
                        .addressType(address.getAddressType())
                        .build()));
        return addressResponses;
    }

    private <T> void addUpdateIfNotNull(T value, Consumer<T> setter, List<Runnable> updates) {
        if (value != null) {
            updates.add(() -> setter.accept(value));
        }
    }

    private <T> void addUpdateIfChanged(T newValue, Supplier<T> currentGetter, Consumer<T> updater, List<Runnable> updates) {
        if (newValue != null && !newValue.equals(currentGetter.get())) {
            updates.add(() -> updater.accept(newValue));
        }
    }



    @Override
    public UserStatus changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        if(user != null) {
            user.setStatus(status);
            userRepository.save(user);

            log.info("User status has changed successfully, userId={}", userId);
            return status;
        }
        log.info("User status has changed failed, userId={}", userId);
        return status;
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        if(user != null) {
            return UserDetailResponse.builder()
                    .id(userId)
                    .fullName(user.getFullName())
                    .dateOfBirth(user.getDateOfBirth())
                    .gender(user.getGender())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .status(user.getStatus())
                    .build();
        }
        return null;
    }

    @Override
    public UserDetailResponse getProfile(HttpServletRequest request) {
        String userName = getUsername(request);
        var user = getByUsername(userName);
        if(user == null) throw new ResourceNotFoundException("User not found") ;

        return UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }

    public static Specification<User> hasRoleIn(List<String> roleNames) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roleJoin = root.join("roles"); // Join với bảng Role
            return roleJoin.get("role").get("name").in(roleNames);
        };
    }
    @Override
    public PageResponse<?> getAllUsersByRole(int pageNo, int pageSize, List<String> roles) {
        Specification<User> spec = hasRoleIn(roles);
        Page<User> page = userRepository.findAll(spec, PageRequest.of(pageNo, pageSize));

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }


    @Override
    public List<String> findAllRolesByUserId(long userId) {
        return userRepository.findAllRolesByUserId(userId);
    }

    @Override
    public Set<String> findPermissionsByRoles(List<String> roles) {
        return roleRepository.findByNameIn(roles).stream()
                .flatMap(role -> role.getRoles().stream())
                .map(roleHasPermission -> roleHasPermission.getPermission().getName())
                .collect(Collectors.toSet());
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private boolean clearUserCache(long userId) {

        return true;
    }

    private String getUsername(HttpServletRequest request) {
        final String authorization = request.getHeader(AUTHORIZATION);

        if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) throw new RuntimeException("User not found") ;

        final String token = authorization.substring("Bearer ".length());
        return jwtService.extractUsername(token, ACCESS_TOKEN);
    }
}
