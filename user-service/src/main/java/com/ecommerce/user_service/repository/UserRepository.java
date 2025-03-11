package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsUserByUsernameOrEmail(String username, String email);
    @Query(value = "select r.name from Role r inner join UserHasRole ur on r.id = ur.role.id where ur.user.id= :userId")
    List<String> findAllRolesByUserId(Long userId);

}
