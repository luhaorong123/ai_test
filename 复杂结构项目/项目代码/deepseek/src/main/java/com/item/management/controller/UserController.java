// UserController.java - 普通用户控制器
package com.item.management.controller;

import com.item.management.dto.request.ChangePasswordRequest;
import com.item.management.dto.request.ChangeUserInfoRequest;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.UserWithStatsDTO;
import com.item.management.entity.User;
import com.item.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户个人信息管理API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user, "获取用户信息成功"));
    }

    @GetMapping("/me/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取当前用户统计信息", description = "获取当前用户的借阅统计信息")
    public ResponseEntity<ApiResponse<UserWithStatsDTO>> getCurrentUserStats() {
        User currentUser = userService.getCurrentUser();
        UserWithStatsDTO userWithStats = userService.getUserWithStats(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(userWithStats, "获取用户统计信息成功"));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的基本信息")
    public ResponseEntity<ApiResponse<User>> updateCurrentUser(
            @Valid @RequestBody ChangeUserInfoRequest request) {

        User currentUser = userService.getCurrentUser();
        User updatedUser = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "用户信息更新成功"));
    }

    @PostMapping("/me/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "修改当前用户密码", description = "修改当前登录用户的密码")
    public ResponseEntity<ApiResponse<Void>> changeCurrentUserPassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        User currentUser = userService.getCurrentUser();

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("新密码和确认密码不一致"));
        }

        userService.changePassword(currentUser.getId(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "密码修改成功"));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户公开信息", description = "获取指定用户的公开信息（仅限管理员查看其他用户详情）")
    public ResponseEntity<ApiResponse<User>> getUserInfo(
            @PathVariable Long userId) {

        // 普通用户只能查看自己的信息，管理员可以查看所有用户信息
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("无权查看其他用户信息", 403));
        }

        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "获取用户信息成功"));
    }
}