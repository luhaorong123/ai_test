// RegisterRequest.java - 增强的注册请求DTO
package com.item.management.dto.request;

import com.item.management.enums.UserType;
import com.item.management.validation.ValidationGroups;
import com.item.management.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@Data
@Schema(description = "注册请求")
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空", groups = {Default.class, ValidationGroups.Register.class})
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间",
            groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "用户名", example = "testuser", required = true)
    private String username;

    @NotBlank(message = "密码不能为空", groups = {Default.class, ValidationGroups.Register.class})
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间",
            groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "密码", example = "password123", required = true)
    private String password;

    @NotBlank(message = "确认密码不能为空", groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "确认密码", example = "password123", required = true)
    private String confirmPassword;

    @Email(message = "邮箱格式不正确", groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "邮箱", example = "test@example.com")
    private String email;

    @com.item.management.validation.PhoneNumber(groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @ValidEnum(enumClass = UserType.class, message = "用户类型必须是USER或ADMIN",
            groups = {Default.class, ValidationGroups.Register.class})
    @Schema(description = "用户类型", example = "USER", defaultValue = "USER")
    private String userType = "USER"; // 保持为String类型，在服务层转换
}