// JwtResponse.java - JWT响应DTO
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT认证响应")
public class JwtResponse {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    @JsonProperty("token_type")
    private String tokenType;

    @Schema(description = "过期时间（毫秒）", example = "86400000")
    @JsonProperty("expires_in")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {

        @Schema(description = "用户ID", example = "1")
        private Long id;

        @Schema(description = "用户名", example = "admin")
        private String username;

        @Schema(description = "邮箱", example = "admin@example.com")
        private String email;

        @Schema(description = "手机号", example = "13800138000")
        private String phone;

        @Schema(description = "用户类型", example = "ADMIN")
        private String userType;

        @Schema(description = "账号状态", example = "ACTIVE")
        private String status;
    }
}