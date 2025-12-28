package com.library.controller;

import com.library.common.ApiResponse;
import com.library.dto.auth.LoginRequest;
import com.library.dto.auth.LoginResponse;
import com.library.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // JWT 无状态，登出通常前端清除 token 即可，后端可记录黑名单（此处简化）
        return ApiResponse.success("登出成功，请前端清除 token");
    }
}