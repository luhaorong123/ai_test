// UserServiceImpl.java - 用户服务实现类
package com.item.management.service.impl;

import com.item.management.dto.request.ChangeUserInfoRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.response.UserWithStatsDTO;
import com.item.management.entity.BorrowRecord;
import com.item.management.entity.User;
import com.item.management.enums.BorrowStatus;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import com.item.management.exception.BusinessException;
import com.item.management.repository.BorrowRecordRepository;
import com.item.management.repository.UserRepository;
import com.item.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在，ID: " + userId));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在，用户名: " + username));
    }

    @Override
    @Transactional
    public User createUser(RegisterRequest request) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在: " + request.getUsername());
        }

        // 验证邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册: " + request.getEmail());
        }

        // 验证手机号是否已存在
        if (StringUtils.hasText(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册: " + request.getPhone());
        }

        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .userType(request.getUserType() != null ?
                        UserType.fromCode(request.getUserType()) : UserType.USER) // 转换String到UserType
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("创建用户成功: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, ChangeUserInfoRequest request) {
        User user = getUserById(userId);

        // 验证邮箱是否被其他用户使用
        if (StringUtils.hasText(request.getEmail()) &&
                !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被其他用户使用: " + request.getEmail());
        }

        // 验证手机号是否被其他用户使用
        if (StringUtils.hasText(request.getPhone()) &&
                !request.getPhone().equals(user.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被其他用户使用: " + request.getPhone());
        }

        // 更新用户信息
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }

        if (request.getUserType() != null) {
            // 不允许将最后一个管理员修改为普通用户
            if (user.getUserType() == UserType.ADMIN && request.getUserType() == UserType.USER) {
                long adminCount = userRepository.countByUserType(UserType.ADMIN);
                if (adminCount <= 1) {
                    throw new BusinessException("不能将最后一个管理员修改为普通用户");
                }
            }
            user.setUserType(request.getUserType());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("更新用户信息成功: {} (ID: {})", updatedUser.getUsername(), updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional
    public User updateUserStatus(Long userId, UserStatus status) {
        User user = getUserById(userId);

        // 不允许禁用最后一个管理员
        if (user.getUserType() == UserType.ADMIN && status == UserStatus.INACTIVE) {
            long activeAdminCount = userRepository.countByUserType(UserType.ADMIN);
            if (activeAdminCount <= 1) {
                throw new BusinessException("不能禁用最后一个管理员");
            }
        }

        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        log.info("更新用户状态成功: {} (ID: {}) -> {}",
                updatedUser.getUsername(), updatedUser.getId(), status);
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        // 检查用户是否存在未归还的物品
        List<BorrowRecord> activeBorrows = borrowRecordRepository
                .findByUserIdAndStatus(userId, BorrowStatus.ACTIVE, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        if (!activeBorrows.isEmpty()) {
            throw new BusinessException("用户有未归还的物品，无法删除");
        }

        // 检查是否是最后一个管理员
        if (user.getUserType() == UserType.ADMIN) {
            long adminCount = userRepository.countByUserType(UserType.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessException("不能删除最后一个管理员");
            }
        }

        // 软删除用户
        user.softDelete();
        userRepository.save(user);

        // 级联删除用户的借阅记录（软删除）
        List<BorrowRecord> userBorrowRecords = borrowRecordRepository
                .findByUserId(userId, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        for (BorrowRecord record : userBorrowRecords) {
            record.softDelete();
        }

        log.info("删除用户成功: {} (ID: {})", user.getUsername(), user.getId());
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable);
    }

    @Override
    public Page<User> searchUsers(String username, String email, String phone,
                                  UserType userType, UserStatus status, Pageable pageable) {
        return userRepository.searchUsers(username, email, phone, userType, status, pageable);
    }

    @Override
    public Page<User> getUsersByType(UserType userType, Pageable pageable) {
        return userRepository.findByUserType(userType, pageable);
    }

    @Override
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    @Override
    public long countAllUsers() {
        return userRepository.count();
    }

    @Override
    public long countByUserType(UserType userType) {
        return userRepository.countByUserType(userType);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == status)
                .count();
    }

    @Override
    public UserWithStatsDTO getUserWithStats(Long userId) {
        User user = getUserById(userId);

        // 统计用户的借阅信息
        long totalBorrows = borrowRecordRepository.findByUserId(userId, org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        long activeBorrows = borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.ACTIVE,
                org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        long overdueBorrows = borrowRecordRepository.findUserOverdueRecords(userId,
                java.time.LocalDate.now(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        long returnedBorrows = borrowRecordRepository.findByUserIdAndStatus(userId,
                BorrowStatus.RETURNED, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();

        return UserWithStatsDTO.builder()
                .user(user)
                .totalBorrows(totalBorrows)
                .activeBorrows(activeBorrows)
                .overdueBorrows(overdueBorrows)
                .returnedBorrows(returnedBorrows)
                .build();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("重置用户密码成功: {} (ID: {})", user.getUsername(), user.getId());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("用户修改密码成功: {} (ID: {})", user.getUsername(), user.getId());
    }
}