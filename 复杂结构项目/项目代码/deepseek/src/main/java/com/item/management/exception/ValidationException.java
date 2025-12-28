// ValidationException.java - 验证异常
package com.item.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidationException extends BusinessException {

    private final Map<String, String> validationErrors;

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
        this.validationErrors = null;
    }

    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, HttpStatus.BAD_REQUEST.value());
        this.validationErrors = validationErrors;
    }

    public ValidationException(Map<String, String> validationErrors) {
        super("参数验证失败", HttpStatus.BAD_REQUEST.value());
        this.validationErrors = validationErrors;
    }
}