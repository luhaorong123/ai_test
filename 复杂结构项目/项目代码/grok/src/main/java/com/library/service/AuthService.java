package com.library.service;

import com.library.dto.auth.LoginRequest;
import com.library.dto.auth.LoginResponse;
import com.library.entity.User;
import com.library.enums.UserType;
import com.library.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("用户名或密码错误");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        // 获取用户信息（实际项目中可从userDetails扩展或额外查询）
        User user = (User) userDetails; // 假设UserDetails实现来自实体
        String role = user.getRole() == UserType.ADMIN ? "ADMIN" : "USER";

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtUtil.extractExpiration(token).toInstant().getEpochSecond() - System.currentTimeMillis() / 1000);

        return new LoginResponse(token, user.getUsername(), user.getName(), role, expiresAt);
    }
}