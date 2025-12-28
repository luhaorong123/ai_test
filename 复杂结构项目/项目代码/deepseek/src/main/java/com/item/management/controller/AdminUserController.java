// AdminUserController.java - 管理员用户管理控制器
package com.item.management.controller;

import com.item.management.converter.UserConverter;
import com.item.management.dto.request.ChangeUserInfoRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.request.ResetPasswordRequest;
import com.item.management.dto.request.UserQueryRequest;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.UserSummaryDTO;
import com.item.management.dto.response.UserWithStatsDTO;
import com.item.management.entity.User;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import com.item.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "管理员-用户管理", description = "管理员操作用户的API")
@Validated
public class AdminUserController {

    private final UserService userService;
    private final UserConverter userConverter;

    @PostMapping
    @Operation(summary = "创建用户", description = "管理员创建新用户账号")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user, "用户创建成功"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public ResponseEntity<ApiResponse<UserWithStatsDTO>> getUserDetail(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId) {

        UserWithStatsDTO userWithStats = userService.getUserWithStats(userId);
        return ResponseEntity.ok(ApiResponse.success(userWithStats, "获取用户详情成功"));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @Valid @RequestBody ChangeUserInfoRequest request) {

        User updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "用户信息更新成功"));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户账号")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @RequestParam @Parameter(description = "用户状态", required = true) UserStatus status) {

        User updatedUser = userService.updateUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "用户状态更新成功"));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "删除用户账号（软删除）")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId) {

        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "用户删除成功"));
    }

    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "分页查询所有用户，支持搜索和筛选")
    public ResponseEntity<ApiResponse<Page<UserSummaryDTO>>> getUsers(
            @Valid UserQueryRequest queryRequest) {

        Page<User> userPage = userService.searchUsers(
                queryRequest.getUsername(),
                queryRequest.getEmail(),
                queryRequest.getPhone(),
                queryRequest.getUserType(),
                queryRequest.getStatus(),
                queryRequest.toPageable()
        );

        Page<UserSummaryDTO> resultPage = userPage.map(userConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/type/{userType}")
    @Operation(summary = "按类型查询用户", description = "根据用户类型分页查询用户")
    public ResponseEntity<ApiResponse<Page<UserSummaryDTO>>> getUsersByType(
            @PathVariable @Parameter(description = "用户类型", required = true) UserType userType,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<User> userPage = userService.getUsersByType(userType,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<UserSummaryDTO> resultPage = userPage.map(userConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询用户", description = "根据用户状态分页查询用户")
    public ResponseEntity<ApiResponse<Page<UserSummaryDTO>>> getUsersByStatus(
            @PathVariable @Parameter(description = "用户状态", required = true) UserStatus status,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        Page<User> userPage = userService.getUsersByStatus(status,
                org.springframework.data.domain.PageRequest.of(page, size));

        Page<UserSummaryDTO> resultPage = userPage.map(userConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable @Parameter(description = "用户ID", required = true) Long userId,
            @Valid @RequestBody ResetPasswordRequest request) {

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("新密码和确认密码不一致"));
        }

        userService.resetPassword(userId, request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "密码重置成功"));
    }

    @GetMapping("/stats")
    @Operation(summary = "用户统计信息", description = "获取用户统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userService.countAllUsers();
        long adminCount = userService.countByUserType(UserType.ADMIN);
        long userCount = userService.countByUserType(UserType.USER);
        long activeCount = userService.countByStatus(UserStatus.ACTIVE);
        long inactiveCount = userService.countByStatus(UserStatus.INACTIVE);

        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("userCount", userCount);
        stats.put("activeCount", activeCount);
        stats.put("inactiveCount", inactiveCount);
        stats.put("adminPercentage", totalUsers > 0 ?
                String.format("%.2f%%", (double) adminCount / totalUsers * 100) : "0.00%");
        stats.put("activePercentage", totalUsers > 0 ?
                String.format("%.2f%%", (double) activeCount / totalUsers * 100) : "0.00%");

        return ResponseEntity.ok(ApiResponse.success(stats, "获取统计信息成功"));
    }

    @GetMapping("/exists/username/{username}")
    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被注册")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameExists(
            @PathVariable @Parameter(description = "用户名", required = true) String username) {

        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists, "检查用户名是否存在成功"));
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被注册")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @PathVariable @Parameter(description = "邮箱", required = true) String email) {

        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists, "检查邮箱是否存在成功"));
    }

    @GetMapping("/exists/phone/{phone}")
    @Operation(summary = "检查手机号是否存在", description = "检查手机号是否已被注册")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(
            @PathVariable @Parameter(description = "手机号", required = true) String phone) {

        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(exists, "检查手机号是否存在成功"));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
    public ResponseEntity<ApiResponse<Page<UserSummaryDTO>>> searchUsers(
            @RequestParam @Parameter(description = "搜索关键词", required = true) String keyword,
            @RequestParam(defaultValue = "0") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {

        // 构建查询参数
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setPage(page);
        queryRequest.setSize(size);
        queryRequest.setUsername(keyword);
        queryRequest.setEmail(keyword);
        queryRequest.setPhone(keyword);

        Page<User> userPage = userService.searchUsers(
                keyword, // 用户名
                keyword, // 邮箱
                keyword, // 手机号
                null,    // 用户类型
                null,    // 状态
                queryRequest.toPageable()
        );

        Page<UserSummaryDTO> resultPage = userPage.map(userConverter::toSummaryDTO);
        return ResponseEntity.ok(ApiResponse.success(resultPage));
    }
}