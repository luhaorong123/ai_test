// AuthServiceImpl.java - 认证服务实现类
package com.item.management.service.impl;

import com.item.management.dto.request.ChangePasswordRequest;
import com.item.management.dto.request.LoginRequest;
import com.item.management.dto.request.RegisterRequest;
import com.item.management.dto.response.JwtResponse;
import com.item.management.entity.User;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import com.item.management.exception.BusinessException;
import com.item.management.repository.UserRepository;
import com.item.management.service.AuthService;
import com.item.management.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            // 更新最后登录时间
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // 生成JWT令牌
            return jwtUtils.generateTokenResponse(user);

        } catch (BadCredentialsException e) {
            log.warn("用户登录失败: {}", loginRequest.getUsername());
            throw new BusinessException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(registerRequest.getEmail()) &&
                userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(registerRequest.getPhone()) &&
                userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建新用户
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .userType(UserType.fromCode(registerRequest.getUserType())) // 转换String为UserType
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            String username = jwtUtils.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtils.validateToken(refreshToken, userDetails)) {
                throw new BusinessException("刷新令牌无效");
            }

            // 生成新的访问令牌
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("用户不存在"));

            return jwtUtils.generateTokenResponse(user);

        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new BusinessException("刷新令牌失败: " + e.getMessage());
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        // 清除安全上下文
        SecurityContextHolder.clearContext();

        // 在实际生产环境中，这里可以添加令牌黑名单逻辑
        log.info("用户注销成功");
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("用户 {} 修改密码成功", username);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtUtils.validateToken(token, userDetails);
        } catch (Exception e) {
            log.warn("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
}