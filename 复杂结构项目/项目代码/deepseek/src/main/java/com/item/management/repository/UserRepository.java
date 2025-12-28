// UserRepository.java - 用户数据访问接口
package com.item.management.repository;

import com.item.management.entity.User;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Page<User> findByUserType(UserType userType, Pageable pageable);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    Page<User> findByUserTypeAndStatus(UserType userType, UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:phone IS NULL OR u.phone LIKE %:phone%) AND " +
            "(:userType IS NULL OR u.userType = :userType) AND " +
            "(:status IS NULL OR u.status = :status)")
    Page<User> searchUsers(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("userType") UserType userType,
            @Param("status") UserStatus status,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    long countByUserType(@Param("userType") UserType userType);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActive(Pageable pageable);

    // 在UserRepository中添加以下方法：
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND u.deleted = false")
    long countByUserTypeAndNotDeleted(@Param("userType") UserType userType);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR " +
            "u.username LIKE %:keyword% OR " +
            "u.email LIKE %:keyword% OR " +
            "u.phone LIKE %:keyword%) AND " +
            "u.deleted = false")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}