// UserStatus.java - 用户状态枚举
package com.item.management.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("ACTIVE", "正常"),
    INACTIVE("INACTIVE", "禁用");

    private final String code;
    private final String description;

    UserStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserStatus fromCode(String code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态: " + code);
    }
}