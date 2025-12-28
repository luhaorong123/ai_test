package com.library.enums;

import lombok.Getter;

@Getter
public enum ItemStatus {
    AVAILABLE("available", "可借"),
    MAINTENANCE("maintenance", "维护中"),
    LOST("lost", "丢失");

    private final String code;
    private final String description;

    ItemStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}