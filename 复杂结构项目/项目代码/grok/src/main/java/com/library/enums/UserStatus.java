package com.library.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("active", "正常"),
    INACTIVE("inactive", "禁用");

    private final String code;
    private final String description;

    UserStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}