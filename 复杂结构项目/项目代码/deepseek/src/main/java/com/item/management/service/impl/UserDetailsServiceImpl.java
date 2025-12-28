// UserDetailsServiceImpl.java - 自定义用户详情服务
package com.item.management.service.impl;

import com.item.management.entity.User;
import com.item.management.enums.UserStatus;
import com.item.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 检查用户状态
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 构建角色权限
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == UserStatus.ACTIVE,
                true, // 账户是否未过期
                true, // 凭证是否未过期
                true, // 账户是否未锁定
                authorities
        );
    }

    @Transactional
    public void updateLastLoginTime(Long userId) {
        userRepository.updateLastLoginTime(userId, java.time.LocalDateTime.now());
    }
}