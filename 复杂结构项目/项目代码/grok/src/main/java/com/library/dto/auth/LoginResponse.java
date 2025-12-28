package com.library.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String name;
    private String role; // ADMIN 或 USER
    private LocalDateTime expiresAt;

    public LoginResponse(String token, String username, String name, String role, LocalDateTime expiresAt) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.role = role;
        this.expiresAt = expiresAt;
    }
}