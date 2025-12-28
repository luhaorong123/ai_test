// UserType.java - 增强枚举类
package com.item.management.enums;

import lombok.Getter;

@Getter
public enum UserType {
    USER("USER", "普通用户"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserType fromCode(String code) {
        if (code == null) {
            return USER; // 默认返回普通用户
        }

        for (UserType type : UserType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }

    public static UserType fromCode(String code, UserType defaultValue) {
        if (code == null) {
            return defaultValue;
        }

        for (UserType type : UserType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return defaultValue;
    }
}