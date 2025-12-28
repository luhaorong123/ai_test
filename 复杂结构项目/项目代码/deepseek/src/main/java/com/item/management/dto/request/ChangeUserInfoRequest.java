// ChangeUserInfoRequest.java - 修改用户信息请求DTO
package com.item.management.dto.request;

import com.item.management.enums.UserStatus;
import com.item.management.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "修改用户信息请求")
public class ChangeUserInfoRequest {

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "updated@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138001")
    private String phone;

    @Schema(description = "用户类型", example = "USER")
    private UserType userType; // 改为UserType类型

    @Schema(description = "账号状态", example = "ACTIVE")
    private UserStatus status;
}