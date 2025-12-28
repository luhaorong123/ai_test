package com.library.enums;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN("admin", "管理员"),
    USER("user", "普通用户");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}