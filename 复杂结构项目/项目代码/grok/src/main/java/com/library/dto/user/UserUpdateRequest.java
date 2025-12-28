package com.library.dto.user;

import com.library.enums.UserStatus;
import com.library.enums.UserType;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String name;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    private String password; // 可选更新密码

    private UserType role;

    private UserStatus status;
}