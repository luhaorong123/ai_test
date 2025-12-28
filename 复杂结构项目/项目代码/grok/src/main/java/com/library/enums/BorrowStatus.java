package com.library.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    BORROWED("borrowed", "借出中"),
    RETURNED("returned", "已归还"),
    OVERDUE("overdue", "逾期");

    private final String code;
    private final String description;

    BorrowStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}