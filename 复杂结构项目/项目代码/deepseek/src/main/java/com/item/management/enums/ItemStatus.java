// ItemStatus.java - 物品状态枚举
package com.item.management.enums;

import lombok.Getter;

@Getter
public enum ItemStatus {
    AVAILABLE("AVAILABLE", "可借"),
    BORROWED("BORROWED", "已借出"),
    MAINTENANCE("MAINTENANCE", "维护中");

    private final String code;
    private final String description;

    ItemStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ItemStatus fromCode(String code) {
        for (ItemStatus status : ItemStatus.values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的物品状态: " + code);
    }
}