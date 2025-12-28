// BorrowStatus.java - 借阅状态枚举
package com.item.management.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    ACTIVE("ACTIVE", "进行中"),
    RETURNED("RETURNED", "已归还"),
    OVERDUE("OVERDUE", "逾期");

    private final String code;
    private final String description;

    BorrowStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BorrowStatus fromCode(String code) {
        for (BorrowStatus status : BorrowStatus.values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的借阅状态: " + code);
    }

}