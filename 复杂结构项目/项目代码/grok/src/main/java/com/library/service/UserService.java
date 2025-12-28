package com.library.service;

import com.library.common.ApiResponse;
import com.library.dto.user.UserCreateRequest;
import com.library.dto.user.UserUpdateRequest;
import com.library.entity.User;
import com.library.enums.UserStatus;
import com.library.enums.UserType;
import com.library.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建用户（管理员专用）
     */
    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : UserType.USER)
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    /**
     * 更新用户信息（管理员专用）
     */
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 用户名不可修改
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()
                    && !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        // 更新密码（可选）
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * 删除用户（管理员专用）
     * 借阅记录通过 JPA CascadeType.ALL + orphanRemoval=true 自动级联删除
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id); // 自动级联删除 borrow_records
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 分页查询用户列表（支持搜索）
     */
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        User probe = new User();
        if (keyword != null && !keyword.isBlank()) {
            probe.setUsername(keyword);
            probe.setName(keyword);
            probe.setEmail(keyword);
        }

        Example<User> example = Example.of(probe, matcher);
        return userRepository.findAll(example, pageable);
    }
}