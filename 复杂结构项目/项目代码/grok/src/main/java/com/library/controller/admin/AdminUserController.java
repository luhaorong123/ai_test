package com.library.controller.admin;

import com.library.common.ApiResponse;
import com.library.dto.user.UserCreateRequest;
import com.library.dto.user.UserResponse;
import com.library.dto.user.UserUpdateRequest;
import com.library.entity.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')") // 所有接口仅管理员可访问
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);
        return ApiResponse.success(UserResponse.from(user));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id,
                                                @Valid @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(id, request);
        return ApiResponse.success(UserResponse.from(user));
    }

    /**
     * 删除用户（级联删除借阅记录）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("用户删除成功");
    }

    /**
     * 获取单个用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(UserResponse.from(user));
    }

    /**
     * 分页查询用户列表（支持关键词搜索）
     */
    @GetMapping
    public ApiResponse<Page<UserResponse>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> userPage = userService.searchUsers(keyword.trim().isEmpty() ? null : keyword.trim(), page, size);
        Page<UserResponse> responsePage = userPage.map(UserResponse::from);

        return ApiResponse.success(responsePage);
    }
}