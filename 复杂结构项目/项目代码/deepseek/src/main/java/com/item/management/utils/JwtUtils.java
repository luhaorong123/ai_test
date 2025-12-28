// JwtUtils.java - JWT工具类
package com.item.management.utils;

import com.item.management.config.JwtProperties;
import com.item.management.dto.response.JwtResponse;
import com.item.management.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // 从令牌中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从令牌中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从令牌中提取指定声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JWT令牌格式错误: {}", e.getMessage());
            throw new IllegalArgumentException("无效的JWT令牌");
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败: {}", e.getMessage());
            throw new IllegalArgumentException("JWT签名无效");
        } catch (IllegalArgumentException e) {
            log.warn("JWT令牌参数错误: {}", e.getMessage());
            throw new IllegalArgumentException("JWT令牌参数无效");
        }
    }

    // 验证令牌是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 验证令牌
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 生成访问令牌
    public String generateAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();

        // 添加用户角色到claims
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        // 如果是用户实体，添加额外信息
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            claims.put("userId", user.getId());
            claims.put("userType", user.getUserType().name());
        }

        return createToken(claims, authentication.getName(), jwtProperties.getExpiration());
    }

    // 生成刷新令牌
    public String generateRefreshToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, authentication.getName(), jwtProperties.getRefreshExpiration());
    }

    // 从用户信息生成令牌
    public JwtResponse generateTokenResponse(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userType", user.getUserType().name());
        claims.put("roles", "ROLE_" + user.getUserType().name());

        String accessToken = createToken(claims, user.getUsername(), jwtProperties.getExpiration());
        String refreshToken = createRefreshToken(user.getUsername());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .userInfo(JwtResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .userType(user.getUserType().name())
                        .status(user.getStatus().name())
                        .build())
                .build();
    }

    // 创建令牌
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 创建刷新令牌
    private String createRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 刷新访问令牌
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            String username = claims.getSubject();
            String tokenType = claims.get("type", String.class);

            if (!"refresh".equals(tokenType)) {
                throw new IllegalArgumentException("无效的刷新令牌");
            }

            // 创建新的访问令牌
            Map<String, Object> newClaims = new HashMap<>();
            if (claims.containsKey("userId")) {
                newClaims.put("userId", claims.get("userId"));
            }
            if (claims.containsKey("userType")) {
                newClaims.put("userType", claims.get("userType"));
            }
            if (claims.containsKey("roles")) {
                newClaims.put("roles", claims.get("roles"));
            }

            return createToken(newClaims, username, jwtProperties.getExpiration());

        } catch (ExpiredJwtException e) {
            log.warn("刷新令牌已过期: {}", e.getMessage());
            throw new IllegalArgumentException("刷新令牌已过期，请重新登录");
        } catch (Exception e) {
            log.warn("刷新令牌验证失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的刷新令牌");
        }
    }
}