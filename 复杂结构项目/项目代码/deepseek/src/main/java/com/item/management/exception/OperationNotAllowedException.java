// OperationNotAllowedException.java - 操作不允许异常
package com.item.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OperationNotAllowedException extends BusinessException {

    private final String operation;
    private final String reason;

    public OperationNotAllowedException(String operation, String reason) {
        super(String.format("操作 '%s' 不被允许: %s", operation, reason),
                HttpStatus.FORBIDDEN.value());
        this.operation = operation;
        this.reason = reason;
    }
}