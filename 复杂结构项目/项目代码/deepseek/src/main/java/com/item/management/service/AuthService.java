// AuthService.java - 认证服务接口
package com.item.management.service;

import com.item.management.dto.request.ChangePasswordRequest;
import com.item.management.dto.request.LoginRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.response.JwtResponse;
import com.item.management.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);

    User register(RegisterRequest registerRequest);

    JwtResponse refreshToken(String refreshToken);

    void logout(HttpServletRequest request);

    void changePassword(ChangePasswordRequest request);

    boolean validateToken(String token);
}