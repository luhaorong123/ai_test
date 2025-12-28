// JwtAuthenticationFilter.java - 完全重写doFilterInternal方法
package com.item.management.filter;

import com.item.management.config.JwtProperties;
import com.item.management.service.impl.UserDetailsServiceImpl;
import com.item.management.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 从请求中提取JWT令牌
            String jwt = parseJwt(request);

            if (jwt != null) {
                // 从令牌中获取用户名
                String username = jwtUtils.extractUsername(jwt);

                // 验证令牌并设置认证上下文
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证令牌
                    if (jwtUtils.validateToken(jwt, userDetails)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // 设置认证详情
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("已认证用户: {}，角色: {}", username, userDetails.getAuthorities());
                    }
                }
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // 从请求中解析JWT令牌
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(jwtProperties.getHeader());

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(jwtProperties.getPrefix())) {
            return headerAuth.substring(jwtProperties.getPrefix().length());
        }

        // 也支持从查询参数获取token（用于WebSocket等场景）
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }
}