// UserSummaryDTO.java - 用户摘要DTO（用于列表展示）
package com.item.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户摘要信息")
public class UserSummaryDTO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "用户类型", example = "ADMIN")
    private UserType userType;

    @Schema(description = "账号状态", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @Schema(description = "借阅次数")
    private long borrowCount;

    @Schema(description = "当前借阅中")
    private long activeBorrowCount;

    @Schema(description = "逾期次数")
    private long overdueCount;
}