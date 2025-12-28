// UserService.java - 扩展用户服务接口
package com.item.management.service;

import com.item.management.dto.request.ChangeUserInfoRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.response.UserWithStatsDTO;
import com.item.management.entity.User;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User getCurrentUser();

    // 用户基本信息管理
    User getUserById(Long userId);

    User getUserByUsername(String username);

    User createUser(RegisterRequest request);

    User updateUser(Long userId, ChangeUserInfoRequest request);

    User updateUserStatus(Long userId, UserStatus status);

    void deleteUser(Long userId);

    // 用户查询
    Page<User> getAllUsers(Pageable pageable);

    Page<User> searchUsers(String username, String email, String phone,
                           UserType userType, UserStatus status, Pageable pageable);

    Page<User> getUsersByType(UserType userType, Pageable pageable);

    Page<User> getUsersByStatus(UserStatus status, Pageable pageable);

    // 统计信息
    long countAllUsers();

    long countByUserType(UserType userType);

    long countByStatus(UserStatus status);

    UserWithStatsDTO getUserWithStats(Long userId);

    // 验证
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // 密码管理
    void resetPassword(Long userId, String newPassword);

    void changePassword(Long userId, String currentPassword, String newPassword);
}