// AuthController.java - 增强的认证控制器
package com.item.management.controller;

import com.item.management.dto.request.LoginRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.response.ApiResponse;
import com.item.management.dto.response.JwtResponse;
import com.item.management.entity.User;
import com.item.management.service.AuthService;
import com.item.management.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证、注册、令牌管理接口")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "登录成功"));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    public ResponseEntity<ApiResponse<User>> register(
            @Validated(ValidationGroups.Register.class) @RequestBody RegisterRequest registerRequest) {

        // 验证密码和确认密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new com.item.management.exception.ValidationException("密码和确认密码不一致");
        }

        User user = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(user, "注册成功"));
    }

    // ... 其他方法
}